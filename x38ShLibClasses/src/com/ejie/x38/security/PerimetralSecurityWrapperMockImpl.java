/*
* Copyright 2011 E.J.I.E., S.A.
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

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author UDA
 *
 */
public class PerimetralSecurityWrapperMockImpl implements
		PerimetralSecurityWrapper {

	private static final Logger logger = LoggerFactory
			.getLogger(PerimetralSecurityWrapperMockImpl.class);

	private String principal;
	
	private Vector<String> roles;
	
	private String uidSession;
	
	@Override
	public String getUserConnectedUidSession(HttpServletRequest httpRequest) {
		httpRequest.getSession(false).setAttribute("UidSession", uidSession);
		return uidSession;
	}

	@Override
	public String getUserPosition(HttpServletRequest httpRequest){
		httpRequest.getSession(false).setAttribute("Position", "myPosition");
		return "userPosition";
	}
	
	@Override
	public String getUserConnectedUserName(HttpServletRequest httpRequest) {
		String userName = null;
		
		//This utility enables the use of multiple users during stress tests
		String idLog = httpRequest.getParameter("idLog");
		if (idLog != null && !idLog.equals("")){
			logger.debug("Obtained the userName of the request: " + idLog);
			logger.info("The incoming user \""+idLog+"\" is already authenticated in the security system");
			userName = idLog;
		}else{
			logger.debug("Not obtained the userName of the request");
			logger.info("Accessing to the userName...");
			userName = principal;
		}
		
		//If the session does not exist, disable XLNET caching
		if(httpRequest.getSession(false)==null){
			httpRequest.getSession(true);
		}
		httpRequest.getSession(false).setAttribute("UserName", userName);
		return userName;
	}

	@Override
	public Vector<String> getUserInstances(HttpServletRequest httpRequest) {
		return roles;
	}
	
	@Override
	public String getURLLogin(String originalURL) {
		return null;
	}

	@Override
	public void logout(HttpServletRequest httpRequest) {	
	}	
	
	//Getters & Setters
	public String getPrincipal() {
		return principal;
	}

	public void setPrincipal(String principal) {
		this.principal = principal;
	}

	public Vector<String> getRoles() {
		return roles;
	}

	public void setRoles(Vector<String> roles) {
		this.roles = roles;
	}

	public String getUidSession() {
		return uidSession;
	}

	public void setUidSession(String uidSession) {
		this.uidSession = uidSession;
	}
}