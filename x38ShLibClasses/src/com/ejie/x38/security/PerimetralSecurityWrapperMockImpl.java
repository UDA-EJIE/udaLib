package com.ejie.x38.security;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Logger;

public class PerimetralSecurityWrapperMockImpl implements
		PerimetralSecurityWrapper {

	private static final Logger logger = Logger
			.getLogger(PerimetralSecurityWrapperMockImpl.class);

	private String principal;
	
	private Vector<String> roles;
	
	private String uidSession;
	
	@Override
	public String getUserConnectedUidSession(HttpServletRequest httpRequest) {
		return uidSession;
	}

	@Override
	public String getUserPosition(HttpServletRequest httpRequest){
		return "myPosition";
	}
	
	@Override
	public String getUserConnectedUserName(HttpServletRequest httpRequest) {
		String userName = null;
		
		//This utility enables the use of multiple users during stress tests
		String idLog = httpRequest.getParameter("idLog");
		if (idLog != null && !idLog.equals(""))
		{
			logger.debug(idLog == null ? "No obtenido parámetro idLog por request"
					: "Obtenido idLog por Request: " + idLog);
			userName = idLog == null ? userName : idLog;
		}else{
			userName = principal;
		}
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