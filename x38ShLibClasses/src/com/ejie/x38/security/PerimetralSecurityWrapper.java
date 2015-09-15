package com.ejie.x38.security;

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

public interface PerimetralSecurityWrapper {

	public abstract String getUserConnectedUserName(HttpServletRequest httpRequest);

	public abstract String getUserConnectedUidSession(HttpServletRequest httpRequest);
	
	public String getUserPosition(HttpServletRequest httpRequest);

	public String getURLLogin(String originalURL);

	public Vector<String> getUserInstances(HttpServletRequest httpRequest);

	public void logout(HttpServletRequest httpRequest);
}