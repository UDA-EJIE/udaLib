/*
* Copyright 2012 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.security;

import java.io.IOException;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import n38a.exe.N38APISesion;
import n38c.exe.N38API;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.w3c.dom.Document;

import com.ejie.x38.log.LogConstants;
import com.ejie.x38.util.StaticsContainer;

/**
 * 
 * @author UDA
 *
 */
public class PerimetralSecurityWrapperN38Impl implements
		PerimetralSecurityWrapper {

	private static final Logger logger = LoggerFactory
			.getLogger(PerimetralSecurityWrapperN38Impl.class);
	
	private Long xlnetCachingPeriod;
	private String xlnetsDomain = null;
	private String userChangeUrl = null;
	private ExcludeFilter excludeFilter;
	private UdaCustomJdbcDaoImpl alternativeStorageUserCredentials = null;
	private HashMap<String, String> anonymousProfile = new HashMap<String, String>();
	
	public PerimetralSecurityWrapperN38Impl(){
		this.anonymousProfile.put("position", "udaAnonymousPosition");
		this.anonymousProfile.put("userProfiles", "udaAnonymousProfile");
	}

	public String validateSession(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws IOException {
		
		HttpSession httpSession = httpRequest.getSession(false);
		UserCredentials credentials = null; 
		Authentication authentication = null;

		if (httpSession == null || (httpSession.getAttribute("securityRedirection") == null && httpSession.getAttribute("userChange") == null)){
			
			String udaXLNetsSessionId = getXlnetsUserId(httpRequest);
			
			//XLNets retrieve n38UidSesionGlobal cookie
			if (udaXLNetsSessionId!=null){			

				//Getting Authentication credentials
				authentication = SecurityContextHolder.getContext().getAuthentication();
				if (authentication != null){
					credentials = (UserCredentials)authentication.getCredentials();
				}
						
				//If the sessionId changed, disable XLNET caching
				if (credentials != null){
					//
					if(!(credentials.getUdaValidateSessionId().equals(udaXLNetsSessionId.toString()))){
						
						logger.info("XLNet's caching of session "+httpSession.getId()+" expired, because the XLNets user has changed");
						authenticationLogContextClean();
						httpSession.setAttribute("reloadData", "true");
						httpSession.setAttribute("userChange", "true");
						
						//Validate the Object of XLnets
						if(!(isN38ApiValid(httpRequest, httpResponse))){
							udaXLNetsSessionId = null;
							return "false";
						} else if (excludeFilter != null && (!excludeFilter.accept(httpRequest, httpResponse))){
							SecurityContextHolder.clearContext();
							httpSession.setAttribute("securityRedirection", "true");
							return excludeFilter.getAccessDeniedUrl();
						}
						
						if(userChangeUrl != null){
							SecurityContextHolder.clearContext();
							return userChangeUrl;
						}
					}
				} else {
					
					//Validate the Object of XLnets
					if(!(isN38ApiValid(httpRequest, httpResponse))){
						udaXLNetsSessionId = null;
						return "false";
					} else if (excludeFilter != null && (!excludeFilter.accept(httpRequest, httpResponse))){
						httpSession.setAttribute("securityRedirection", "true");
						return excludeFilter.getAccessDeniedUrl();
					}
				}
				
				//If the last XLNET session refresh was performed more than X minutes ago, disable caching
				if(httpSession!=null && httpSession.getAttribute("udaTimeStamp")!=null){
					if((System.currentTimeMillis()- Long.valueOf(httpSession.getAttribute("udaTimeStamp")+""))>TimeUnit.MILLISECONDS.convert(xlnetCachingPeriod.longValue(), TimeUnit.SECONDS)){
		
						//Validate the Object of XLnets
						if(isN38ApiValid(httpRequest, httpResponse)){
							httpSession.setAttribute("reloadData", "true");
						} else {
							udaXLNetsSessionId = null;
							return "false";
						}
						
						logger.info("XLNet's caching of session "+httpSession.getId()+" expired, after, at least, "+xlnetCachingPeriod+" Seconds");
					}
				}
				
				return "true";
					
			} else {
				logger.info("There isn't a correct session of XLNET");
				udaXLNetsSessionId = null;
				return "false";
			}
			
		}else if (httpSession != null && httpSession.getAttribute("securityRedirection") != null && httpSession.getAttribute("securityRedirection").equals("true")){
			httpSession.removeAttribute("securityRedirection");
			return "false";
			
		}else {
			//Validate the Object of XLnets
			if(isN38ApiValid(httpRequest, httpResponse)){
				return "true";
			} else {
				return "false";
			}
		}
	}
	
	/* Methods to recovery the credentials data */
	
	public String getUserConnectedUserName(HttpServletRequest httpRequest) {
		String userName = null;
		String xlnetUserId = getXlnetsUserId(httpRequest);
		
		UserCredentials credentials = null; 
		Authentication authentication = null;
		
		//Getting Authentication credentials
		authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null){
			credentials = (UserCredentials)authentication.getCredentials();
		}
		
		if(httpRequest.getSession(false) != null){
			userName = (String)httpRequest.getSession(false).getAttribute("userName");
			
			if (!(userName == null && credentials == null)){
				if (credentials != null){
					if (userName != null){
						if(userName.equals(credentials.getUserName())){
							httpRequest.getSession(false).removeAttribute("userName");
						}
					} else {
						if (!(credentials.getUdaValidateSessionId().equals(xlnetUserId))){
							userName = loadXlnetsCredentialInfo(httpRequest, xlnetUserId);
						} else {
							userName = credentials.getUserName();
						}
					}
				}
			} else {
				userName = loadXlnetsCredentialInfo(httpRequest, xlnetUserId);
			}
		} else {
			userName = loadXlnetsCredentialInfo(httpRequest, xlnetUserId);
		}			
			
		logger.trace("Connected User's Name is: "+userName);
		return userName;		
	}
	
	public String getUserPosition(HttpServletRequest httpRequest) {
		UserCredentials credentials = null; 
		Authentication authentication = null;
		String userPosition = null;
		
		//Getting Authentication credentials
		authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null){
			credentials = (UserCredentials)authentication.getCredentials();
		}
		
		userPosition = (String)httpRequest.getSession(false).getAttribute("position");
		
		//Returning UserPosition
		if (userPosition == null){
			userPosition = credentials.getPosition();
		} else {
			httpRequest.getSession(false).removeAttribute("position");
		}
		
		logger.trace("Connected User's Position is: "+userPosition);
		return userPosition;
		
	}	

	public String getUdaValidateSessionId(HttpServletRequest httpRequest) {
		UserCredentials credentials = null; 
		Authentication authentication = null;
		String udaValidateSessionId = null;
		
		//Getting Authentication credentials
		authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null){
			credentials = (UserCredentials)authentication.getCredentials();
		}
		
		udaValidateSessionId = (String)httpRequest.getSession(false).getAttribute("udaValidateSessionId");
		
		//Returning UdaValidateSessionId
		if (udaValidateSessionId == null){
			if(credentials != null){
				udaValidateSessionId = credentials.getUdaValidateSessionId();
			} else {
				return null;
			}
		} else {
			httpRequest.getSession(false).removeAttribute("udaValidateSessionId");
		}
		
		logger.trace("Connected UserConnectedUidSession is: "+udaValidateSessionId);
		return udaValidateSessionId;
		
	}

	@SuppressWarnings("unchecked")
	public Vector<String> getUserInstances(HttpServletRequest httpRequest) {		
		UserCredentials credentials = null; 
		Authentication authentication = null;
		Vector<String> userInstances = null;
		
		//Getting Authentication credentials
		authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null){
			credentials = (UserCredentials)authentication.getCredentials();
		}
		
		userInstances = (Vector<String>)httpRequest.getSession(false).getAttribute("userProfiles");
		
		//Returning UserInstances
		if (userInstances == null){
			userInstances = credentials.getUserProfiles();
		} else {
			httpRequest.getSession(false).removeAttribute("userProfiles");
		}
		
		logger.trace("Connected UserConnectedUidSession is: "+userInstances);
		return userInstances;
		
	}
	
	public String getUserConnectedUidSession(HttpServletRequest httpRequest) {
		UserCredentials credentials = null; 
		Authentication authentication = null;
		String userConnectedUidSession = null;
		
		//Getting Authentication credentials
		authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null){
			credentials = (UserCredentials)authentication.getCredentials();
		}
		
		userConnectedUidSession = (String)httpRequest.getSession(false).getAttribute("uidSession");
		
		//Returning UserConnectedUidSession
		if (userConnectedUidSession == null){
			if (credentials != null){
				userConnectedUidSession = credentials.getUidSession();
			} else {
				return null;
			}
		} else {
			httpRequest.getSession(false).removeAttribute("uidSession");
		}
		
		logger.trace("Connected UserConnectedUidSession is: "+userConnectedUidSession);
		return userConnectedUidSession;
		
	}
	
	public String getPolicy(HttpServletRequest httpRequest) {
		UserCredentials credentials = null; 
		Authentication authentication = null;
		String userPolicy = null;
		
		//Getting Authentication credentials
		authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null){
			credentials = (UserCredentials)authentication.getCredentials();
		}
		
		userPolicy = (String)httpRequest.getSession(false).getAttribute("policy");
		
		//Returning UserPosition
		if (userPolicy == null){
			userPolicy = credentials.getPolicy();
		} else {
			httpRequest.getSession(false).removeAttribute("policy");
		}
		
		logger.trace("Connected User's Policy is: "+userPolicy);
		return userPolicy;
	}	
	
	public boolean getIsCertificate(HttpServletRequest httpRequest) {
		UserCredentials credentials = null; 
		Authentication authentication = null;
		String userIsCertificate = null;
		boolean userBooleanIsCertificate;
		
		//Getting Authentication credentials
		authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null){
			credentials = (UserCredentials)authentication.getCredentials();
		}
		
		userIsCertificate = (String)httpRequest.getSession(false).getAttribute("isCertificate");
		
		//Returning UserPosition
		if (userIsCertificate == null){
			userBooleanIsCertificate = credentials.getIsCertificate();
		} else {
			userBooleanIsCertificate = userIsCertificate.equals("true");
			httpRequest.getSession(false).removeAttribute("isCertificate");
		}
		
		logger.trace("Connected User's isCertificate is: "+userBooleanIsCertificate);
		return userBooleanIsCertificate;
	}
	
	public String getNif(HttpServletRequest httpRequest) {
		UserCredentials credentials = null; 
		Authentication authentication = null;
		String userNif = null;
		
		//Getting Authentication credentials
		authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if (authentication != null){
			credentials = (UserCredentials)authentication.getCredentials();
		}
		
		userNif = (String)httpRequest.getSession(false).getAttribute("nif");
		
		//Returning UserPosition
		if (userNif == null){
			userNif = credentials.getNif();
		} else {
			httpRequest.getSession(false).removeAttribute("nif");
		}
		
		logger.trace("Connected User's nif is: "+userNif);
		return userNif;
	}
	
	/* [END] Methods to recovery the credentials data */
	
	@Override
	public String getURLLogin(String originalURL, boolean ajax) {
		logger.debug("Original URLLogin is :"+originalURL);
		StringBuilder resultURL = new StringBuilder(StaticsContainer.loginUrl);

		if (originalURL != null && !"".equals(originalURL)){
			resultURL.append("?N38API=");
			resultURL.append(originalURL);
		}
		logger.debug("URLLogin is: "+resultURL);
		
		return resultURL.toString();
	}

	//Method not properly working due to N38 related issues
	@Override
	public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		
		String uidSession = getUserConnectedUidSession(httpRequest);
		
		if (uidSession == null){
			N38API n38Api = XlnetCore.getN38API(httpRequest);
			uidSession = XlnetCore.getParameterSession(n38Api, N38API.NOMBRE_N38UIDSESION);
		}
		
		logger.info( "Proceeding to destroy uidSession: "+ uidSession);

		N38APISesion n38ApiSesion = new N38APISesion();
		n38ApiSesion.n38APISesionDestruir(uidSession);
		
		//Cleaning SpringSecurity context
		springSecurityContextClean();
		
		//Cleaning the cookies of XLNets 
		deleteAllXLNetsCookies(httpRequest, httpResponse);

		logger.info( "Session "+uidSession+" destroyed!");
	}
	
	//Validates the N38API and, if is necessary, cleans the XLNets cookies
	protected boolean isN38ApiValid(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
		
		N38API n38Api;
		Document xmlSesion = null;
		
		//It clears the cache of XLNets and SpringSecurity context  
		xlnetCleanCache(httpRequest);
		
		n38Api = XlnetCore.getN38API(httpRequest);
		
		if (n38Api != null){
			logger.info( "Validating the session of XLNets!");
			
			xmlSesion = XlnetCore.getN38ItemSesion(n38Api);
			
			if (XlnetCore.isXlnetSessionContainingErrors(xmlSesion)
					|| XlnetCore.isXlnetSessionContainingWarnings(xmlSesion)) {
				
				logger.info("The XLNET session is invalid");
				
				//Cleaning objects
				xlnetCleanCache(httpRequest);
				
				//Deleting security context data
				n38Api = null;
				xmlSesion = null;
				springSecurityContextClean();
				deleteAllXLNetsCookies(httpRequest, httpResponse);
				authenticationLogContextClean();
				
				return false;
			} else {
				logger.info("XLNET session is valid.");
				
				//Saving the xml of XLNets Session
				httpRequest.getSession(true).setAttribute("xmlSesion", xmlSesion);
				
				if(xlnetsDomain == null){
					xlnetsDomain = XlnetCore.getN38DominioComunCookie(xmlSesion);
				}
				
				//Deleting security context data
				n38Api = null;
				xmlSesion = null;
											
				return true;
			}			
		} else {
			xlnetCleanCache(httpRequest);
			springSecurityContextClean();
			deleteAllXLNetsCookies(httpRequest, httpResponse);
			
			return false;
		}
	}
	
	//Cleaner method of XLNETs cached information
	private void xlnetCleanCache(HttpServletRequest httpRequest){
		HttpSession session = httpRequest.getSession(false);
		if(session != null){
			session.removeAttribute("nif");
			session.removeAttribute("policy");
			session.removeAttribute("userName");
			session.removeAttribute("position");
			session.removeAttribute("uidSession");
			session.removeAttribute("userProfiles");
			session.removeAttribute("isCertificate");
			session.removeAttribute("udaValidateSessionId");
		}
	}
	
	//Cleaner method unregister Authentication Configuration
	private void authenticationLogContextClean(){
		MDC.put(LogConstants.SESSION,"N/A");
		MDC.put(LogConstants.USER,"N/A");
		MDC.put(LogConstants.POSITION,"N/A");
	}
	
	//Cleaner method of SpringSecurity context
	private void springSecurityContextClean(){
		logger.error("XLNET session is invalid. Proceeding to clean the Security Context Holder.");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication != null){
			authentication.setAuthenticated(false);
		}
		SecurityContextHolder.clearContext();
	}
	
	//Delete all the security cookies of XLNets 
	private void deleteAllXLNetsCookies(HttpServletRequest httpRequest, HttpServletResponse httpResponse){
		
		if(xlnetsDomain != null){
			Cookie requestCookies[] = httpRequest.getCookies ();
			Cookie n38Cookie;
			
			if (requestCookies != null){
				for (int i = 0; i < requestCookies.length; i++) {
					if (requestCookies[i].getName().split("n38").length > 1){
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
	
	//Recovery the unique User Id 
	private String getXlnetsUserId(HttpServletRequest httpRequest){
		
		Cookie requestCookies[] = httpRequest.getCookies();
		Cookie n38UidSesion = null;
		Cookie n38UidSistemasXLNetS = null;
		StringBuilder udaXLNetsSessionId = new StringBuilder();
		
		if (requestCookies != null){
			for (int i = 0; i < requestCookies.length; i++) {
				if (requestCookies[i].getName().equals("n38UidSesion")){
					n38UidSesion = requestCookies[i];
				} else if (requestCookies[i].getName().equals("n38UidSistemasXLNetS")){
					n38UidSistemasXLNetS = requestCookies[i];
				}
			}
		}
		
		logger.debug( "getXlnetsUserId: udaXlnetsSession value");
		if (n38UidSesion != null && n38UidSistemasXLNetS != null){
			udaXLNetsSessionId.append(n38UidSistemasXLNetS.getValue()).append("-").append(n38UidSesion.getValue());
			logger.debug( "getXlnetsUserId: cookie - n38UidSistemasXLNetS => " + n38UidSistemasXLNetS.getValue());
		} else if (n38UidSesion != null){
			udaXLNetsSessionId.append(n38UidSesion.getValue());
			logger.debug( "getXlnetsUserId: cookie - n38UidSesion => " + n38UidSesion.getValue());
		} else {
			logger.debug( "getXlnetsUserId: null");
			return null;
		}
		logger.debug( "getXlnetsUserId: udaXlnetsSession value => " + udaXLNetsSessionId.toString());
		return udaXLNetsSessionId.toString();
	}
	
	//Recovery and storage of the Credential info of XLNets    
	protected String loadXlnetsCredentialInfo(HttpServletRequest httpRequest, String xLNetsUserId){
		
		N38API n38Api = XlnetCore.getN38API(httpRequest);
		HttpSession httpSession = httpRequest.getSession(false);
		Document xmlSesion = (Document)httpSession.getAttribute("xmlSesion");
		Document xmlSecurityData = null;
		String UserName;
		HashMap<String, String> userInfo = null;
		String policy;
		
		//Recharging  the lifetime of the cache
		httpSession.setAttribute("udaTimeStamp", System.currentTimeMillis());
		
		//Recovering general data of XLNets user credentials
		httpSession.setAttribute("nif", XlnetCore.getParameterSession(n38Api, N38API.NOMBRE_DNI));
		policy = XlnetCore.getParameterSession(n38Api, N38API.NOMBRE_N38CERTIFICADOPOLITICAS);
		httpSession.setAttribute("policy", policy);
		
		if (!(policy.toLowerCase().equals("no"))){
			httpSession.setAttribute("isCertificate", "true");
		} else {
			httpSession.setAttribute("isCertificate", "false");
		}
		
		if(!(XlnetCore.getParameterSession(n38Api, "n38uidOrg").equals("0"))){
			
			UserName = XlnetCore.getParameterSession(n38Api, "n38personasuid");
			
			//Recovering XLNets user credentials
			httpSession.setAttribute("userName", UserName);
			httpSession.setAttribute("position", XlnetCore.getParameterSession(n38Api, N38API.NOMBRE_N38PUESTOUID));
			httpSession.setAttribute("uidSession", XlnetCore.getParameterSession(n38Api, N38API.NOMBRE_N38UIDSESION));
			httpSession.setAttribute("udaValidateSessionId", xLNetsUserId);
			
			xmlSecurityData = XlnetCore.getN38ItemSeguridad(n38Api, StaticsContainer.webAppName);
			if (xmlSecurityData != null) {
				httpSession.setAttribute("userProfiles", XlnetCore.searchParameterIntoXlnetSesion(xmlSecurityData, XlnetCore.PATH_SUBTIPO_N38INSTANCIA));
			} else {
				httpSession.setAttribute("userProfiles", null);
			}
		} else{
			
			userInfo = XlnetCore.getN38SubjectCert(xmlSesion);
			UserName = userInfo.get("CN");
			
			//Recovering user credentials
			httpSession.setAttribute("userName", UserName);
			httpSession.setAttribute("uidSession", httpSession.getId());
			httpSession.setAttribute("udaValidateSessionId", xLNetsUserId);
					
			Vector<String> userprofile = new Vector<String>();
			
			if(this.alternativeStorageUserCredentials == null){
				userprofile.add(this.anonymousProfile.get("userProfiles"));
				httpSession.setAttribute("position", this.anonymousProfile.get("position"));
			} else {
				userprofile = this.alternativeStorageUserCredentials.loadUserAuthorities(UserName, userInfo.get("SERIALNUMBER"), n38Api, xmlSesion);
				httpSession.setAttribute("position", this.alternativeStorageUserCredentials.loadUserPosition(UserName, userInfo.get("SERIALNUMBER"), n38Api, xmlSesion));
			}
			
			httpSession.setAttribute("userProfiles", userprofile);
		}

		//Deleting the xmlSession object 
		httpSession.removeAttribute("xmlSesion");
		return UserName;
	}
	
	//Getters & Setters
	public Long getXlnetCachingPeriod() {
		return this.xlnetCachingPeriod;
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
	
	public ExcludeFilter excludeFilter(){
		return this.excludeFilter;
	}
	
	public void setXlnetCachingPeriod(Long xlnetCachingPeriod) {
		this.xlnetCachingPeriod = xlnetCachingPeriod;
	}
	
	public void setUserChangeUrl(String userChangeUrl) {
		this.userChangeUrl = userChangeUrl;
	}
	
	public void setAlternativeStorageUserCredentials(UdaCustomJdbcDaoImpl alternativeStorageUserCredentials) {
		if(alternativeStorageUserCredentials.getPositionByUserdataQuery() != null && alternativeStorageUserCredentials.getAuthoritiesByUserdataQuery() != null){
			this.alternativeStorageUserCredentials = alternativeStorageUserCredentials;
		} else{
			//Not being a path to an xml file an exception is raised and does not load  
			UnsatisfiedDependencyException exc = new UnsatisfiedDependencyException("security-config", "PerimetralSecurityWrapperN38Impl", "alternativeStorageUserCredentials", "The PositionByUserdataQuery parameter and the AuthoritiesByUserdataQuery parameter can't be nulls");
			logger.error("The PositionByUserdataQuery parameter and the AuthoritiesByUserdataQuery parameter can't be nulls.", exc);
			throw exc;
		}
	}
	
	public void setAnonymousCredentials(HashMap<String, String> anonymousProfile) {
		this.anonymousProfile = anonymousProfile;
	}
	
	public void setExcludeFilter(ExcludeFilter excludeFilter){
		this.excludeFilter = excludeFilter;
	}
	
}