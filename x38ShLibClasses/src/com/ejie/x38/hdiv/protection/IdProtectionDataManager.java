package com.ejie.x38.hdiv.protection;

import javax.servlet.http.HttpServletRequest;

public interface IdProtectionDataManager {
	
	public void storeSecureId(Class<?> clazz, String nId);
	
	public boolean isAllowedSecureId(Class<?> clazz, String securedId);
	
	public boolean isAllowedToAction(Class<?> clazz, String nId);
	
	public boolean isAllowedAction(HttpServletRequest request);
	
	public void allowAction(String url);

	public void allowId(String url, Class<?> clazz, String nId);
	
	public void remapAction(String url, String toRemapURL);

}