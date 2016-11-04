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

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.MappingJsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.beans.factory.UnsatisfiedDependencyException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.context.WebApplicationContext;

import com.ejie.x38.log.LogConstants;

/**
 * 
 * @author UDA
 *
 */
public class PerimetralSecurityWrapperMockImpl implements
		PerimetralSecurityWrapper {
	
	@Autowired
	private WebApplicationContext webApplicationContext;
	
	private static final Logger logger = LoggerFactory
			.getLogger(PerimetralSecurityWrapperMockImpl.class);

	private ArrayList<HashMap<String,Object>> principal;
	private String userChangeUrl;
	private Object specificCredentials = null;
	private String specificCredentialsName = null;
	private boolean destroySessionSecuritySystem = false;

	public String validateSession(HttpServletRequest httpRequest, HttpServletResponse response) throws SecurityException{
		
		Credentials credentials = null; 
		Authentication authentication = null;
		StringBuilder udaMockSessionId = new StringBuilder();
		HttpSession httpSession = httpRequest.getSession(true);
				
		//mock retrieve udaMockUserName cookie
		Cookie requestCookies[] = httpRequest.getCookies ();
		Cookie udaMockUserName = null;			
		
		if (requestCookies != null){
			for (int i = 0; i < requestCookies.length; i++) {
				if (requestCookies[i].getName().equals("udaMockUserName")){
					udaMockUserName = requestCookies[i];
					break;
				}
			}
		}
		
		if (udaMockUserName != null){
			udaMockSessionId.append(udaMockUserName.getValue()); 
			udaMockSessionId.append("-");
			udaMockSessionId.append(httpRequest.getSession(false).getId());
			
			//Getting Authentication credentials
			authentication = SecurityContextHolder.getContext().getAuthentication();
			
			if (authentication != null){
				credentials = (Credentials)authentication.getCredentials();
			}
					
			//If the sessionId changed, disable XLNET caching
			if (credentials != null){
				if(!(credentials.getUdaValidateSessionId().equals(udaMockSessionId.toString()))){
					
					authenticationLogContextClean();
					httpSession.setAttribute("reloadData", "true");
					httpSession.setAttribute("userChange", "true");
											
					if(userChangeUrl != null){
						SecurityContextHolder.clearContext();
						return userChangeUrl;
					}
				}
			}
				
			return "true";
		} else {
			return "false";
		}
	}
	
	public String getUserConnectedUidSession(HttpServletRequest httpRequest) {
		
		if(httpRequest.getSession(false) != null){
			return httpRequest.getSession(false).getId();
		} else {
			return "no session available";
		}
	}

	public String getUserPosition(HttpServletRequest httpRequest){
		
		HashMap<String, Object> user = getUserData(principal, getUserConnectedUserName(httpRequest));
		
		return (String)user.get("position");		
	}
	
	public String getUdaValidateSessionId(HttpServletRequest httpRequest) {
		StringBuilder udaMockSessionId = new StringBuilder();
		
		udaMockSessionId.append(getUserConnectedUserName(httpRequest)); 
		udaMockSessionId.append("-");
		udaMockSessionId.append(httpRequest.getSession(false).getId());
		
		return udaMockSessionId.toString();
	}
	
	public String getUserConnectedUserName(HttpServletRequest httpRequest) {
		
		Cookie requestCookies[] = httpRequest.getCookies ();
		Cookie udaMockUserName = null;			
		
		if (requestCookies != null){
			for (int i = 0; i < requestCookies.length; i++) {
				if (requestCookies[i].getName().equals("udaMockUserName")){
					udaMockUserName = requestCookies[i];
					break;
				}
			}
		}
		
		if(httpRequest.getSession(false).getAttribute("fullName") == null){
			HashMap<String, Object> user = getUserData(principal, udaMockUserName.getValue());
		
			httpRequest.getSession(false).setAttribute("fullName", (String)user.get("fullName"));
		}
		
		return udaMockUserName.getValue();
	}
	
	@SuppressWarnings("unchecked")
	public HashMap<String, String> getUserDataInfo(HttpServletRequest httpRequest, boolean isCertificate){
		//falta la especificacion de datos de las credenciles para el mock
		HashMap<String, Object> user = getUserData(principal, getUserConnectedUserName(httpRequest));
		HashMap<String, String> userData = new HashMap<String, String>();
		
		if(isCertificate && user.get("subjectCert") != null){
			userData = (HashMap<String, String>)user.get("subjectCert");
		}
		
		userData.put("name", (String)user.get("name"));
		userData.put("surname", (String)user.get("surname"));
		userData.put("fullName", (String)user.get("fullName"));
		
		return (userData);
	}

	@SuppressWarnings("unchecked")
	public Vector<String> getUserInstances(HttpServletRequest httpRequest) {
		HashMap<String, Object> user = getUserData(principal, getUserConnectedUserName(httpRequest));
		
		return new Vector<String>((ArrayList<String>)user.get("roles"));	
	}
	
	public String getPolicy(HttpServletRequest httpRequest) {
		HashMap<String, Object> user = getUserData(principal, getUserConnectedUserName(httpRequest));
		
		return (String)user.get("policy");		
	}	
	
	public boolean getIsCertificate(HttpServletRequest httpRequest) {
		HashMap<String, Object> user = getUserData(principal, getUserConnectedUserName(httpRequest));
		
		if (((String)user.get("isCertificate")).equals("false")){
			return false;
		} else {
			return true;
		}
	}
	
	public String getNif(HttpServletRequest httpRequest) {
		HashMap<String, Object> user = getUserData(principal, getUserConnectedUserName(httpRequest));
		
		return (String)user.get("nif");	
	}
	
	public String getURLLogin(String originalURL, boolean ajax) {
		String dataUsers = null;
		ArrayList<HashMap<String, String>> usersNames = new ArrayList<HashMap<String, String>> ();
		Iterator<HashMap<String,Object>> usersIterator = principal.iterator();
		HashMap<String, String> auxObject = new HashMap<String, String>();
		HashMap<String, Object> user;
		
		//Parameters of JSon serialization  
		ObjectMapper mapper = new ObjectMapper();
		StringWriter sw = new StringWriter();
		MappingJsonFactory jsonFactory = new MappingJsonFactory();
		JsonGenerator jsonGenerator;
		
		while ( usersIterator.hasNext() ){
			auxObject = new HashMap<String, String>();
			user = usersIterator.next();			
			auxObject.put("i18nCaption", (String)user.get("fullName"));
			auxObject.put("value", (String)user.get("userName"));
			
			usersNames.add(auxObject);
		}
		
		try {
			jsonGenerator = jsonFactory.createGenerator(sw);
			mapper.writeValue(jsonGenerator, usersNames);
			sw.close();
			dataUsers = sw.getBuffer().toString();
			
			//Deleting the objects of the serialization
			jsonGenerator = null;
			mapper = null;
			jsonFactory = null;
			
		} catch (Exception e) {
			logger.error("Produced a error in the conversion of the mockWrapper usernames. Review your configuration. The response is void.");
			if (!ajax){
				return(webApplicationContext.getServletContext().getContextPath()+"/mockLoginPage?mockUrl="+originalURL+"&userNames=\"\"");
			} else {
				return(webApplicationContext.getServletContext().getContextPath()+"/mockLoginAjaxPage?mockUrl="+originalURL+"&userNames=\"\"");
			}
		}
		
		if (!ajax){
			return(webApplicationContext.getServletContext().getContextPath()+"/mockLoginPage?mockUrl="+originalURL+"&userNames="+dataUsers);
		} else {
			return(webApplicationContext.getServletContext().getContextPath()+"/mockLoginAjaxPage?mockUrl="+originalURL+"&userNames="+dataUsers);
		}
	}

	public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse) {
		
		String uidSession = getUserConnectedUidSession(httpRequest);
		logger.info( "Proceeding to destroy uidSession: "+ uidSession);
		
		//Cleaning SpringSecurity context
		springSecurityContextClean();
		
		//Cleaning of the udaMockUserName cookie
		Cookie requestCookies[] = httpRequest.getCookies ();
		Cookie udaMockUserName = null;			
		
		if (requestCookies != null){
			for (int i = 0; i < requestCookies.length; i++) {
				if (requestCookies[i].getName().equals("udaMockUserName")){
					udaMockUserName = requestCookies[i];
					udaMockUserName.setMaxAge(0);
					udaMockUserName.setPath("/");
					httpResponse.addCookie(udaMockUserName);
					break;
				}
			}
		}
		
		

		logger.info( "Session "+uidSession+" destroyed!");
	}
	
	//Cleaner method of SpringSecurity context
	private void springSecurityContextClean(){
		logger.error("Session is invalid. Proceeding to clean the Security Context Holder.");
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		
		if(authentication != null){
			authentication.setAuthenticated(false);
		}
		SecurityContextHolder.clearContext();
	}
	
	//Returns data of the specific user
	private HashMap<String,Object> getUserData(ArrayList<HashMap<String,Object>> principal, String name){
		
		Iterator<HashMap<String,Object>> usersIterator = principal.iterator();
		HashMap<String, Object> user = null;
		
		while ( usersIterator.hasNext() ){
			user = usersIterator.next();
			if (((String)user.get("userName")).equals(name)){
				break;
			}
		}
		
		return user;
	}
	
	//Cleaner method unregister Authentication Configuration
	private void authenticationLogContextClean(){
		MDC.put(LogConstants.SESSION,"N/A");
		MDC.put(LogConstants.USER,"N/A");
		MDC.put(LogConstants.POSITION,"N/A");
	}
	
	//Getters & Setters
	public Object getSpecificCredentials(){
		return this.specificCredentials;
	}
	
	public Credentials getCredentials(){
		if (specificCredentialsName == null){
			return new UserCredentials();
		} else {
			try {
				return (Credentials)Class.forName(specificCredentialsName).newInstance();
			}catch (Exception e) {
				logger.error("getCredentials(): The object specified to the parameter \"SpecificCredentials\" is not correct. The object has not been instantiated", e);
				SecurityException sec = new SecurityException("getCredentials(): The object specified to the parameter \"SpecificCredentials\" is not correct. The object has not been instantiated", e.getCause());
				throw sec;
			}
		}
	}
	
	public boolean getDestroySessionSecuritySystem(){
		return this.destroySessionSecuritySystem;
	}
	
	public ArrayList<HashMap<String,Object>> getPrincipal() {
		return principal;
	}
	
	public void setPrincipal(ArrayList<HashMap<String,Object>> principal) {
		this.principal = principal;
		
		//Data of User Anonymous
		HashMap<String, Object> userAnonymous = new HashMap<String, Object>(); 
		ArrayList<String> roles = new ArrayList<String>();
		HashMap<String,String> subjectCert = new HashMap<String,String>();
		roles.add("UDAANONYMOUS");
		
		userAnonymous.put("userName", "udaAnonymousUser");
		userAnonymous.put("name", "uda");
		userAnonymous.put("surName", "Anonymous User");
		userAnonymous.put("fullName", "Uda Anonymous User");
		userAnonymous.put("nif", "00000000a");
		userAnonymous.put("policy", "udaAnonymousPolicy");
		userAnonymous.put("position", "udaAnonymousPosition");
		userAnonymous.put("isCertificate", "no");
		userAnonymous.put("subjectCert",subjectCert);
		userAnonymous.put("roles", roles);
		
		this.principal.add(userAnonymous);
					
	}
	
	public String getUserChangeUrl() {
		return this.userChangeUrl;
	}
	
	public void setUserChangeUrl(String userChangeUrl) {
		this.userChangeUrl = userChangeUrl;
	}
	
	public void setSpecificCredentials(Object credentials){
		Object specificCredentials = credentials; 
		
		try{
			if(specificCredentials instanceof String){
				specificCredentials = Class.forName((String)credentials).newInstance();
			}
			if(specificCredentials instanceof Credentials){
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
		
	public void setDestroySessionSecuritySystem(boolean destroySessionSecuritySystem){
	}
}