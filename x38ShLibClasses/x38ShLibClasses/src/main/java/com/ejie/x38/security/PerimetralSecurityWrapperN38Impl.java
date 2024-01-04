/*
 * Copyright 2012 E.J.I.E., S.A.
 *
 * Licencia con arreglo a la EUPL, VersiÃ³n 1.1 exclusivamente (la Â«LicenciaÂ»);
 * Solo podrÃ¡ usarse esta obra si se respeta la Licencia.
 * Puede obtenerse una copia de la Licencia en
 *
 * http://ec.europa.eu/idabc/eupl.html
 *
 * Salvo cuando lo exija la legislaciÃ³n aplicable o se acuerde por escrito,
 * el programa distribuido con arreglo a la Licencia se distribuye Â«TAL CUALÂ»,
 * SIN GARANTÃ�AS NI CONDICIONES DE NINGÃšN TIPO, ni expresas ni implÃ­citas.
 * VÃ©ase la Licencia en el idioma concreto que rige los permisos y limitaciones
 * que establece la Licencia.
 */
package com.ejie.x38.security;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.w3c.dom.Document;

import com.ejie.x38.log.LogConstants;
import com.ejie.x38.util.StaticsContainer;
import com.ejie.x38.util.ThreadStorageManager;

import n38a.exe.N38APISesion;
import n38c.exe.N38API;
import n38i.exe.N38Excepcion;
import n38i.exe.N38ParameterException;

/**
 * 
 * @author UDA
 *
 */
public class PerimetralSecurityWrapperN38Impl implements
		PerimetralSecurityWrapper {

	private static final Logger logger = LoggerFactory
			.getLogger(PerimetralSecurityWrapperN38Impl.class);

	private Long xlnetCachingPeriod = new Long(0);
	private String xlnetsDomain = null;
	private boolean destroySessionSecuritySystem = false;
	private String userChangeUrl = null;
	private ExcludeFilter excludeFilter = null;
	private UdaCustomJdbcDaoImpl alternativeStorageUserCredentials = null;
	private boolean useXlnetProfiles = false;
	private HashMap<String, String> anonymousProfile = new HashMap<String, String>();
	private AlternativeOriginCredentialsApp alternativeOriginCredentialsApp = null;
	private String specificCredentialsName = null;
	private Object specificCredentials = null;

	public PerimetralSecurityWrapperN38Impl() {
		this.anonymousProfile.put("position", "udaAnonymousPosition");
		this.anonymousProfile.put("userProfiles", "udaAnonymousProfile");
	}

	public synchronized String validateSession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws SecurityException {

		String udaXLNetsSessionId = getXlnetsUserId(httpRequest);

		// Getting Authentication credentials
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Credentials credentials = null;

		// Setpoint of the User Session. if the session is not created, it will
		// proceed to create it
		HttpSession httpSession = httpRequest.getSession(true);

		if (authentication != null) {
			credentials = (Credentials) authentication.getCredentials();
		}

		if (credentials != null) {
			if (udaXLNetsSessionId != null) {

				if (excludeFilter != null && (!excludeFilter.accept(httpRequest, httpResponse))) {
					springSecurityContextClean(httpSession);
					return excludeFilter.getAccessDeniedUrl();

					// If the sessionId changed, disable XLNET caching
				} else if (credentials.getUdaValidateSessionId().compareTo(udaXLNetsSessionId.toString()) != 0) {

					logger.info("XLNet's caching of session " + httpSession.getId() + " expired, because the XLNets user has changed");
					authenticationLogContextClean();

					// redirect, if is necesary
					if (userChangeUrl != null) {
						udaXLNetsSessionId = null;
						springSecurityContextClean(httpSession);
						return userChangeUrl;
					}

					// Validate the Object of XLnets
					if (!(isN38ApiValid(httpRequest, httpResponse))) {
						udaXLNetsSessionId = null;
						springSecurityContextClean(httpSession);
						return "false";
					}

					// springSecurityContextClean(httpSession);
					loadReloadData(httpRequest, ThreadStorageManager.getCurrentThreadId());
					httpSession.setAttribute("userChange", "true");

					// If the last XLNET session refresh was performed more than
					// X minutes ago, disable caching
				} else if (httpSession != null && httpSession.getAttribute("udaTimeStamp") != null) {
					if (reloadData(httpRequest)) {
						logger.info("XLNet's caching of session " + httpSession.getId() + " expired, after, at least, " + xlnetCachingPeriod + " Seconds");

						// Validate the Object of XLnets
						if (isN38ApiValid(httpRequest, httpResponse)) {
							loadReloadData(httpRequest, ThreadStorageManager.getCurrentThreadId());
						} else {
							authenticationLogContextClean();
							udaXLNetsSessionId = null;
							springSecurityContextClean(httpSession);
							return "false";
						}
					}
				}
				return "true";

			} else {
				// There isn't a correct session of XLNET
				logger.info("There isn't a correct session of XLNET");
				logout(httpRequest, httpResponse);
				springSecurityContextClean(httpSession);
				udaXLNetsSessionId = null;
				return "false";
			}
		} else {
			logger.info("authentication.getCredentials() null");
			// Validate the Object of XLnets
			if (udaXLNetsSessionId != null && isN38ApiValid(httpRequest, httpResponse)) {
				// The entry is accepting by the security system
				udaXLNetsSessionId = null;
				return "true";
			} else {
				// The entry isn't accepting by the security system
				udaXLNetsSessionId = null;
				return "false";
			}
		}
	}

	/* Methods to recovery the credentials data */

	public String getUserConnectedUserName(HttpServletRequest httpRequest) {
		String userName = null;
		String xlnetUserId = getXlnetsUserId(httpRequest);
		HttpSession httpSession = httpRequest.getSession(false);
		Credentials credentials = null;
		Authentication authentication = null;

		// Getting Authentication credentials
		authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			credentials = (Credentials) authentication.getCredentials();
		}

		if (httpSession != null) {
			userName = (String) httpSession.getAttribute("userName");

			if (!(userName == null && credentials == null)) {
				if (userName == null) {
					httpSession.removeAttribute("fullName");
					httpSession.removeAttribute("destroySessionSecuritySystem");
					userName = credentials.getUserName();
				} else {
					httpSession.removeAttribute("userName");
				}
			} else {
				userName = loadXlnetsCredentialInfo(httpRequest, xlnetUserId);
			}
		} else {
			userName = loadXlnetsCredentialInfo(httpRequest, xlnetUserId);
		}

		logger.trace("Connected User's Name is: " + userName);
		return userName;
	}

	public HashMap<String, String> getUserDataInfo(HttpServletRequest httpRequest, boolean isCertificate) {
		HttpSession httpSession = httpRequest.getSession(false);
		String fullName = (String) httpSession.getAttribute("fullName");

		// Returning UserDataInfo
		HashMap<String, String> userData = new HashMap<String, String>();
		HashMap<String, String> userInfo = null;
		N38API n38Api = XlnetCore.getN38API(httpRequest);
		Document xmlSesion;

		if (isCertificate) {
			xmlSesion = XlnetCore.getN38ItemSesion(n38Api);
			userData = XlnetCore.getN38SubjectCert(xmlSesion);
		}

		if (fullName != null) {
			userData.put("fullName", fullName);
			userData.put("name", (String) httpSession.getAttribute("name"));
			userData.put("surname", (String) httpSession.getAttribute("surname"));

			httpSession.removeAttribute("name");
			httpSession.removeAttribute("surname");
		} else {
			try {
				String n38uidOrg = n38Api.n38ItemSesion("n38uidOrg")[0];
				if (!n38uidOrg.equals("0")) {
					// User is in the XLNets's LDap
					userInfo = XlnetCore.getUserDataInfo(n38Api);
					userData.put("name", userInfo.get("name"));
					userData.put("surname", userInfo.get("surname"));
					userData.put("fullName", userInfo.get("fullName"));
					httpSession.setAttribute("fullName", userInfo.get("fullName"));

				} else {
					// User isn't in the XLNets's LDap: certificado o juego de barcos
					userData.put("name", userData.get("GIVENNAME"));
					userData.put("surname", userData.get("SURNAME"));
					// En caso de autenticaciÃ³n mediante juego de barcos el campo CN tendrÃ¡ 
					// el valor de la propiedad dni del xml de sesiÃ³n de XLNetS
					// o, si estuviera vacio el dni, el valor del n38SubjectCert  
					userData.put("fullName", userData.get("CN"));
					httpSession.setAttribute("fullName", userData.get("CN"));
				}

				xmlSesion = null;
				userInfo = null;
			} catch (N38ParameterException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (N38Excepcion e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		logger.trace("Connected User's data is: " + userData.toString());

		return userData;
	}

	public String getUserPosition(HttpServletRequest httpRequest) {
		String userPosition = null;

		userPosition = (String) httpRequest.getSession(false).getAttribute("position");

		httpRequest.getSession(false).removeAttribute("position");

		logger.trace("Connected User's Position is: " + userPosition);
		return userPosition;

	}

	public String getUdaValidateSessionId(HttpServletRequest httpRequest) {
		String udaValidateSessionId = null;

		udaValidateSessionId = (String) httpRequest.getSession(false).getAttribute("udaValidateSessionId");

		// Returning UdaValidateSessionId
		httpRequest.getSession(false).removeAttribute("udaValidateSessionId");

		logger.trace("Connected UserConnectedUidSession is: " + udaValidateSessionId);
		return udaValidateSessionId;

	}

	@SuppressWarnings("unchecked")
	public Vector<String> getUserInstances(HttpServletRequest httpRequest) {
		Vector<String> userInstances = null;

		userInstances = (Vector<String>) httpRequest.getSession(false).getAttribute("userProfiles");

		// Returning UserInstances
		httpRequest.getSession(false).removeAttribute("userProfiles");

		logger.trace("Connected UserConnectedUidSession is: " + userInstances);
		return userInstances;

	}

	public String getUserConnectedUidSession(HttpServletRequest httpRequest) {
		String userConnectedUidSession = null;
		HttpSession httpSession = httpRequest.getSession(false);

		if (httpSession != null) {
			userConnectedUidSession = (String) httpSession.getAttribute("uidSession");

			// Returning UserConnectedUidSession
			httpRequest.getSession(false).removeAttribute("uidSession");

			logger.trace("Connected UserConnectedUidSession is: " + userConnectedUidSession);
		}

		return userConnectedUidSession;
	}

	public String getPolicy(HttpServletRequest httpRequest) {
		String userPolicy = null;

		userPolicy = (String) httpRequest.getSession(false).getAttribute("policy");

		// Returning UserPosition
		httpRequest.getSession(false).removeAttribute("policy");

		logger.trace("Connected User's Policy is: " + userPolicy);
		return userPolicy;
	}

	public boolean getIsCertificate(HttpServletRequest httpRequest) {
		String userIsCertificate = null;
		boolean userBooleanIsCertificate;

		userIsCertificate = (String) httpRequest.getSession(false).getAttribute("isCertificate");

		userBooleanIsCertificate = userIsCertificate.equals("true");
		httpRequest.getSession(false).removeAttribute("isCertificate");

		logger.trace("Connected User's isCertificate is: " + userBooleanIsCertificate);
		return userBooleanIsCertificate;
	}

	public String getNif(HttpServletRequest httpRequest) {
		String userNif = null;

		userNif = (String) httpRequest.getSession(false).getAttribute("nif");

		// Returning UserNif
		httpRequest.getSession(false).removeAttribute("nif");

		logger.trace("Connected User's nif is: " + userNif);
		return userNif;
	}

	/* [END] Methods to recovery the credentials data */

	@Override
	public String getURLLogin(String originalURL, boolean ajax) {
		logger.debug("Original URLLogin is :" + originalURL);
		StringBuilder resultURL = new StringBuilder(StaticsContainer.loginUrl);

		if (originalURL != null && !"".equals(originalURL)) {
			resultURL.append(resultURL.indexOf("?")!=-1?"&":"?");
			resultURL.append("N38API=");
			resultURL.append(originalURL);
		}
		logger.debug("URLLogin is: " + resultURL);

		return resultURL.toString();
	}

	// Method not properly working due to N38 related issues
	@Override
	public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

		String uidSession = getUserConnectedUidSession(httpRequest);

		if (uidSession == null) {
			N38API n38Api = XlnetCore.getN38API(httpRequest);
			uidSession = XlnetCore.getParameterSession(n38Api, N38API.NOMBRE_N38UIDSESION);
		}

		logger.info("Proceeding to destroy uidSession: " + uidSession);

		N38APISesion n38ApiSesion = new N38APISesion();
		n38ApiSesion.n38APISesionDestruir(uidSession);

		// Cleaning the cookies of XLNets
		deleteAllXLNetsCookies(httpRequest, httpResponse);

		logger.info("XLNets Session " + uidSession + " destroyed!");

	}

	// Validates the N38API and, if is necessary, cleans the XLNets cookies
	protected boolean isN38ApiValid(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws SecurityException {

		Document xmlSesion = null;
		HttpSession httpSession = httpRequest.getSession(false);
		HashMap<String, String> userInfo = null;

		N38API n38Api = XlnetCore.getN38API(httpRequest);
		n38Api = XlnetCore.getN38API(httpRequest);

		if (n38Api != null) {
			logger.info("Validating the session of XLNets!");

			try {
				xmlSesion = XlnetCore.getN38ItemSesion(n38Api);

				if (XlnetCore.isXlnetSessionContainingErrors(xmlSesion)
						|| XlnetCore.isXlnetSessionContainingWarnings(xmlSesion)) {

					logger.info("The XLNET session is invalid");

					// Deleting security context data
					n38Api = null;
					xmlSesion = null;
					// deleteAllXLNetsCookies(httpRequest, httpResponse);
					logout(httpRequest, httpResponse);
					authenticationLogContextClean();

					return false;
				} else {
					logger.info("XLNET session is valid.");

					String n38uidOrg = n38Api.n38ItemSesion("n38uidOrg")[0];
					if (!n38uidOrg.equals("0")) {
						// User is in the XLNets's LDap
						userInfo = XlnetCore.getUserDataInfo(n38Api);
						httpSession.setAttribute("name", userInfo.get("name"));
						httpSession.setAttribute("surname", userInfo.get("surname"));
						httpSession.setAttribute("fullName", userInfo.get("fullName"));

					} else {
						// User isn't in the XLNets's LDap
						userInfo = XlnetCore.getN38SubjectCert(xmlSesion);
						httpSession.setAttribute("serialNumber", userInfo.get("SERIALNUMBER"));
						httpSession.setAttribute("name", userInfo.get("GIVENNAME"));
						httpSession.setAttribute("surname", userInfo.get("SURNAME"));
						// En caso de autentificarse mediante juego de barcos el
						// campo CN tendrÃ¡ el valor de la propiedad dni del xml
						// de sesiÃ³n de XLNets.
						httpSession.setAttribute("fullName", userInfo.get("CN"));
					}

					if (xlnetsDomain == null) {
						xlnetsDomain = XlnetCore.getN38DominioComunCookie(xmlSesion);
					}

					// Deleting security context data
					n38Api = null;
					xmlSesion = null;

					return true;
				}
			} catch (Exception e) {
				logger.error("isN38ApiValid(): There was an access error in XLNets. it Is possible that you having any problem with the configuration of XLNets or XLNets have some own internal error (Check that the service works correctly).", e);
				SecurityException sec = new SecurityException("isN38ApiValid(): There was an access error in XLNets. it Is possible that you having any problem with the configuration of XLNets or XLNets have some own internal error (Check that the service works correctly).", e.getCause());
				throw sec;
			}
		} else {
			deleteAllXLNetsCookies(httpRequest, httpResponse);
			return false;
		}
	}

	// Cleaner method unregister Authentication Configuration
	private void authenticationLogContextClean() {
		MDC.put(LogConstants.SESSION, "N/A");
		MDC.put(LogConstants.USER, "N/A");
		MDC.put(LogConstants.POSITION, "N/A");
	}

	// Cleaner method of SpringSecurity context
	private void springSecurityContextClean(HttpSession httpSession) {
		logger.info("XLNET session is invalid. Proceeding to clean the Security Context Holder.");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

		if (authentication != null) {
			authentication.setAuthenticated(false);
		}
		SecurityContextHolder.clearContext();

		if (httpSession.getAttribute("SPRING_SECURITY_CONTEXT") != null) {
			httpSession.removeAttribute("SPRING_SECURITY_CONTEXT");
		}
	}

	// Delete all the security cookies of XLNets
	private void deleteAllXLNetsCookies(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {

		if (xlnetsDomain != null) {
			Cookie requestCookies[] = httpRequest.getCookies();
			Cookie n38Cookie;

			if (requestCookies != null) {
				for (int i = 0; i < requestCookies.length; i++) {
					if (requestCookies[i].getName().split("n38").length > 1) {
						n38Cookie = requestCookies[i];
						n38Cookie.setDomain(xlnetsDomain);
						n38Cookie.setMaxAge(0);
						n38Cookie.setPath("/");
						httpResponse.addCookie(n38Cookie);
					}
				}
			}
		}
	}

	// Recovery the unique User Id
	private String getXlnetsUserId(HttpServletRequest httpRequest) {
		String udaXLNetsSessionId = XlnetCore.getN38ItemSesion(XlnetCore.getN38API(httpRequest), "n38UidSesion");

		if (udaXLNetsSessionId == null) {
			logger.debug("getXlnetsUserId: udaXlnetsSession value => null");
			return null;
		} else {
			logger.debug("getXlnetsUserId: udaXlnetsSession value => " + udaXLNetsSessionId);
			return udaXLNetsSessionId;
		}
	}

	// Recovery and storage of the Credential info of XLNets
	protected String loadXlnetsCredentialInfo(HttpServletRequest httpRequest, String xLNetsUserId) {

		N38API n38Api = XlnetCore.getN38API(httpRequest);
		HttpSession httpSession = httpRequest.getSession(false);
		Document xmlSecurityData = null;
		Vector<String> userProfiles = new Vector<String>();
		String UserName = null;
		String serialNumber = null;
		String policy;

		// Recovering general data of XLNets user credentials
		httpSession.setAttribute("nif", XlnetCore.getParameterSession(n38Api, N38API.NOMBRE_DNI));
		policy = XlnetCore.getParameterSession(n38Api, N38API.NOMBRE_N38CERTIFICADOPOLITICAS);
		httpSession.setAttribute("policy", policy);

		if (!(policy.toLowerCase().equals("no"))) {
			httpSession.setAttribute("isCertificate", "true");
		} else {
			httpSession.setAttribute("isCertificate", "false");
		}
		
		try {
			String n38uidOrg = n38Api.n38ItemSesion("n38uidOrg")[0];
			
			if (!n38uidOrg.equals("0")) {
				// User is in the XLNets's LDap
				UserName = XlnetCore.getParameterSession(n38Api, "n38personasuid");

				// Recovering XLNets user credentials
				httpSession.setAttribute("userName", UserName);
				httpSession.setAttribute("position", XlnetCore.getParameterSession(n38Api, N38API.NOMBRE_N38PUESTOUID));
				httpSession.setAttribute("uidSession", XlnetCore.getParameterSession(n38Api, N38API.NOMBRE_N38UIDSESION));
				httpSession.setAttribute("udaValidateSessionId", xLNetsUserId);

				// Getting user's profiles
				if (this.alternativeOriginCredentialsApp != null && this.alternativeOriginCredentialsApp.existAditionalsAppCodes(httpRequest)) {
					List<String> appCodes = this.alternativeOriginCredentialsApp.getAppCodes(httpRequest);
					Iterator<String> appCodesIterator = appCodes.iterator();
					String appCode;

					while (appCodesIterator.hasNext()) {
						appCode = appCodesIterator.next();
						xmlSecurityData = XlnetCore.getN38ItemSeguridad(n38Api, appCode);
						if (xmlSecurityData != null) {
							userProfiles.addAll(XlnetCore.searchParameterIntoXlnetSesion(xmlSecurityData, XlnetCore.PATH_SUBTIPO_N38INSTANCIA));
						}
					}
				}

				xmlSecurityData = XlnetCore.getN38ItemSeguridad(n38Api, StaticsContainer.webAppName);
				if (xmlSecurityData != null) {
					if (this.useXlnetProfiles){
						userProfiles.addAll(XlnetCore.searchParameterIntoXlnetSesion(XlnetCore.getN38ItemSesion(n38Api), XlnetCore.PATH_XMLSESION_N38PERFILES));
					}
					userProfiles.addAll(XlnetCore.searchParameterIntoXlnetSesion(xmlSecurityData, XlnetCore.PATH_SUBTIPO_N38INSTANCIA));
					
					if (this.alternativeStorageUserCredentials != null) {
						userProfiles.addAll(this.alternativeStorageUserCredentials.loadUserAuthorities(UserName,  XlnetCore.getParameterSession(n38Api, N38API.NOMBRE_DNI), n38Api));
					}
					
				}
				// Set obtain user's profiles
				httpSession.setAttribute("userProfiles", userProfiles);

			} else {
				// User isn't in the XLNets's LDap
				UserName = (String) httpSession.getAttribute("fullName");
				serialNumber = (String) httpSession.getAttribute("serialNumber");

				// Recovering user credentials
				httpSession.setAttribute("userName", UserName);
				httpSession.setAttribute("uidSession", httpSession.getId());
				httpSession.setAttribute("udaValidateSessionId", xLNetsUserId);

				Vector<String> userprofile = new Vector<String>();

				if (this.alternativeStorageUserCredentials == null) {
					userprofile.add(this.anonymousProfile.get("userProfiles"));
					httpSession.setAttribute("position", this.anonymousProfile.get("position"));
				} else {
					userprofile = this.alternativeStorageUserCredentials.loadUserAuthorities(UserName, serialNumber, n38Api);
					httpSession.setAttribute("position", this.alternativeStorageUserCredentials.loadUserPosition(UserName, serialNumber, n38Api));
				}

				// Deleting the serialNumber object
				httpSession.removeAttribute("serialNumber");

				httpSession.setAttribute("userProfiles", userprofile);
			}
		} catch (N38ParameterException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (N38Excepcion e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		httpSession.setAttribute("destroySessionSecuritySystem", this.destroySessionSecuritySystem);

		return UserName;
	}

	private synchronized void loadReloadData(HttpServletRequest httpRequest, Long currentThreadId) {
		HttpSession session = httpRequest.getSession(false);

		if (session != null && session.getAttribute("reloadData") == null) {
			logger.debug("loadReloadData: " + currentThreadId.toString());
			session.setAttribute("reloadData", currentThreadId);
		}
	}

	private synchronized boolean reloadData(HttpServletRequest httpRequest) {
		HttpSession httpSession = httpRequest.getSession(false);

		if (httpSession.getAttribute("credentialsLoading") == null && (System.currentTimeMillis() - Long.valueOf(httpSession.getAttribute("udaTimeStamp") + "")) > xlnetCachingPeriod.longValue()) {
			// Recharging the lifetime of the cache
			httpSession.removeAttribute("udaTimeStamp");
			httpSession.setAttribute("udaTimeStamp", System.currentTimeMillis());
			return true;
		} else {
			return false;
		}
	}

	// Getters & Setters
	public Long getXlnetCachingPeriod() {
		return TimeUnit.SECONDS.convert(this.xlnetCachingPeriod.longValue(), TimeUnit.MILLISECONDS);
	}

	public String getUserChangeUrl() {
		return this.userChangeUrl;
	}

	public UdaCustomJdbcDaoImpl getAlternativeStorageUserCredentials() {
		return this.alternativeStorageUserCredentials;
	}

	public HashMap<String, String> getAnonymousProfile() {
		return this.anonymousProfile;
	}

	public boolean getDestroySessionSecuritySystem() {
		return this.destroySessionSecuritySystem;
	}

	public ExcludeFilter getExcludeFilter() {
		return this.excludeFilter;
	}

	public Object getAlternativeOriginCredentialsApp() {
		return this.alternativeOriginCredentialsApp;
	}

	public Object getSpecificCredentials() {
		return this.specificCredentials;
	}

	public Credentials getCredentials() {
		if (specificCredentialsName == null) {
			return new UserCredentials();
		} else {
			try {
				return (Credentials) Class.forName(specificCredentialsName).newInstance();
			} catch (Exception e) {
				logger.error("getCredentials(): The object specified to the parameter \"SpecificCredentials\" is not correct. The object has not been instantiated", e);
				SecurityException sec = new SecurityException("getCredentials(): The object specified to the parameter \"SpecificCredentials\" is not correct. The object has not been instantiated", e.getCause());
				throw sec;
			}
		}
	}

	public void setXlnetCachingPeriod(Long xlnetCachingPeriod) {
		this.xlnetCachingPeriod = TimeUnit.MILLISECONDS.convert(xlnetCachingPeriod.longValue(), TimeUnit.SECONDS);
	}

	public void setUserChangeUrl(String userChangeUrl) {
		this.userChangeUrl = userChangeUrl;
	}

	public void setAlternativeStorageUserCredentials(UdaCustomJdbcDaoImpl alternativeStorageUserCredentials) {
		if (alternativeStorageUserCredentials.getPositionByUserdataQuery() != null && alternativeStorageUserCredentials.getAuthoritiesByUserdataQuery() != null) {
			this.alternativeStorageUserCredentials = alternativeStorageUserCredentials;
		} else {
			// Not being a path to an xml file an exception is raised and does
			// not load
			UnsatisfiedDependencyException exc = new UnsatisfiedDependencyException("security-config", "PerimetralSecurityWrapperN38Impl", "alternativeStorageUserCredentials", "The PositionByUserdataQuery parameter and the AuthoritiesByUserdataQuery parameter can't be nulls");
			logger.error("The PositionByUserdataQuery parameter and the AuthoritiesByUserdataQuery parameter can't be nulls.", exc);
			throw exc;
		}
	}

	public void setAnonymousCredentials(HashMap<String, String> anonymousProfile) {
		this.anonymousProfile = anonymousProfile;
	}

	public void setDestroySessionSecuritySystem(boolean destroySessionSecuritySystem) {
		this.destroySessionSecuritySystem = destroySessionSecuritySystem;
	}

	public void setExcludeFilter(ExcludeFilter excludeFilter) {
		this.excludeFilter = excludeFilter;
	}
	
	public boolean isUseXlnetProfiles() {
		return useXlnetProfiles;
	}

	public void setUseXlnetProfiles(boolean useXlnetProfiles) {
		this.useXlnetProfiles = useXlnetProfiles;
	}

	@SuppressWarnings("unchecked")
	public void setAlternativeOriginCredentialsApp(Object alternativeOriginCredentialsApp) {

		AlternativeOriginCredentialsApp alternativeOriginCredentialsAppObject;

		try {
			if (alternativeOriginCredentialsApp instanceof String) {
				alternativeOriginCredentialsAppObject = new AlternativeOriginCredentialsAppImp((String) alternativeOriginCredentialsApp);
			} else if (alternativeOriginCredentialsApp instanceof List<?>) {

				List<Object> validateAlternativeOriginCredentialsApp = (List<Object>) alternativeOriginCredentialsApp;

				for (Object o : validateAlternativeOriginCredentialsApp)
					if (!(o instanceof String))
						throw new UnsatisfiedDependencyException("security-config", "PerimetralSecurityWrapperN38Impl", "alternativeOriginCredentialsApp", "The specified object is not correct to the parameter  \"alternativeOriginCredentialsApp\". The object must be instace of String, List <String> or AlternativeOriginCredentialsApp.");

				alternativeOriginCredentialsAppObject = new AlternativeOriginCredentialsAppImp((List<String>) alternativeOriginCredentialsApp);
			} else if (alternativeOriginCredentialsApp instanceof AlternativeOriginCredentialsApp) {
				alternativeOriginCredentialsAppObject = (AlternativeOriginCredentialsApp) alternativeOriginCredentialsApp;
			} else {
				throw new UnsatisfiedDependencyException("security-config", "PerimetralSecurityWrapperN38Impl", "alternativeOriginCredentialsApp", "The specified object is not correct to the parameter  \"alternativeOriginCredentialsApp\". The object must be instace of String, List <String> or AlternativeOriginCredentialsApp.");
			}
		} catch (Exception e) {
			// As the specified object is not correct to the parameter
			// "alternativeOriginCredentialsApp", an exception is raised and the
			// application doesn't will deploy an exception is raised and does
			// not load
			UnsatisfiedDependencyException exc = new UnsatisfiedDependencyException("security-config", "PerimetralSecurityWrapperN38Impl", "alternativeOriginCredentialsApp", "The specified object is not correct to the parameter  \"alternativeOriginCredentialsApp\". The object must be instace of String, List <String> or AlternativeOriginCredentialsApp.");
			logger.error("The specified object is not correct to the parameter  \"alternativeOriginCredentialsApp\". The object must be instace of String, List <String> or AlternativeOriginCredentialsApp.", exc);
			throw exc;
		}

		this.alternativeOriginCredentialsApp = alternativeOriginCredentialsAppObject;
	}

	public void setSpecificCredentials(Object credentials) {
		Object specificCredentials = credentials;

		try {
			if (specificCredentials instanceof String) {
				specificCredentials = Class.forName((String) credentials).newInstance();
			}
			if (specificCredentials instanceof Credentials) {
				this.specificCredentialsName = specificCredentials.getClass().getName();
			} else {
				throw new UnsatisfiedDependencyException("security-config", "PerimetralSecurityWrapperN38Impl", "setSpecificCredentials", "The specified object is not correct to the parameter  \"SpecificCredentials\". The object must be instace of String (className of a Class than extend the \"Credentials\" Class) or one Bean of a Class than extend the \"Credentials\" Class.");
			}
		} catch (Exception e) {
			throw new UnsatisfiedDependencyException("security-config", "PerimetralSecurityWrapperN38Impl", "setSpecificCredentials", "The specified object is not correct to the parameter  \"SpecificCredentials\". The object must be instace of String (className of a Class than extend the \"Credentials\" Class) or one Bean of a Class than extend the \"Credentials\" Class.");
		} finally {
			this.specificCredentials = specificCredentials;
		}
	}

}