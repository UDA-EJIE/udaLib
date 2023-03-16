package com.ejie.x38.hdiv.protection;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

public class UserSessionIdProtectionDataManager implements IdProtectionDataManager{
	
	//Id maps
	private static final String SECURE_ID_MAP_ATTR_NAME = "SECURE_ID_MAP";
	
	//URL and id by class map
	private static final String SERVER_SIDE_MAP_ATTR_NAME = "SERVER_SIDE_MAP"; 
	
	@SuppressWarnings("unchecked")
	public void storeSecureId(Class<?> clazz, String nId) {
		
		HttpSession session = getSession();
		Map<Class<?>, HashSet<String>> secureIdMap = (Map<Class<?>, HashSet<String>>) session.getAttribute(SECURE_ID_MAP_ATTR_NAME);
		if(secureIdMap == null) {
			secureIdMap = new HashMap<Class<?>, HashSet<String>>();
		}
		HashSet<String> secureIds = secureIdMap.get(clazz);
		if(secureIds == null) {
			secureIds = new HashSet<String>();
			secureIdMap.put(clazz, secureIds);
		}
		secureIds.add(nId);
		
		session.setAttribute(SECURE_ID_MAP_ATTR_NAME, secureIdMap);
	}
	
	@SuppressWarnings("unchecked")
	public boolean isAllowedSecureId(Class<?> clazz, String nId) {
		
		HttpServletRequest request = getRequest();
		HttpSession session = request.getSession();
		Map<Class<?>, HashSet<String>> secureIdMap = (Map<Class<?>, HashSet<String>>) session.getAttribute(SECURE_ID_MAP_ATTR_NAME);
		
		if(secureIdMap != null) {
			HashSet<String> secureIds = secureIdMap.get(clazz);
			return (secureIds != null && secureIds.contains(nId) && isAllowedToAction(request, clazz, nId));
		}
		
		return false;
	}
	
	public boolean isAllowedToAction(Class<?> clazz, String nId) {
		return isAllowedToAction(getRequest(), clazz, nId);
	}
	
	@SuppressWarnings("unchecked")
	private boolean isAllowedToAction(HttpServletRequest request, Class<?> clazz, String nId) {
		
		Map<String, Map<Class<?>, HashSet<String>>> serverSideURLMap = (Map<String, Map<Class<?>, HashSet<String>>>) request.getSession().getAttribute(SERVER_SIDE_MAP_ATTR_NAME);
		
		if(serverSideURLMap != null) {
			String requestURI = request.getRequestURI();
			for (Entry<String, Map<Class<?>, HashSet<String>>> url : serverSideURLMap.entrySet()) {
				if (url.getValue() != null && requestURI.matches(url.getKey())) {
					HashSet<String> allowedIds = url.getValue().get(clazz);
					return allowedIds != null && allowedIds.contains(nId);
				}
			}
		}
		
		return false;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public boolean isAllowedAction(HttpServletRequest request) {
		
		Map<String, Map<Class<?>, HashSet<String>>> serverSideURLMap = (Map<String, Map<Class<?>, HashSet<String>>>) request.getSession().getAttribute(SERVER_SIDE_MAP_ATTR_NAME);
		//Concat HttpMethod: to the stored url to protect against verb tampering
		return serverSideURLMap != null && serverSideURLMap.containsKey(request.getRequestURI());
		
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void allowAction(String url) {
		HttpSession session = getSession();
		Map<String, Map<Class<?>, HashSet<String>>> serverSideURLMap = (Map<String, Map<Class<?>, HashSet<String>>>) session.getAttribute(SERVER_SIDE_MAP_ATTR_NAME);
		if(serverSideURLMap == null) {
			serverSideURLMap = new HashMap<String, Map<Class<?>, HashSet<String>>>();
			session.setAttribute(SERVER_SIDE_MAP_ATTR_NAME, serverSideURLMap);
		}
		if(!serverSideURLMap.containsKey(url)) {
			serverSideURLMap.put(url, new HashMap<Class<?>, HashSet<String>>());	
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public void allowId(String url, Class<?> clazz, String nId) {
		
		if(clazz != null && nId != null) {
			HttpSession session = getSession();
			Map<String, Map<Class<?>, HashSet<String>>> serverSideURLMap = (Map<String, Map<Class<?>, HashSet<String>>>) session.getAttribute(SERVER_SIDE_MAP_ATTR_NAME);
			if(serverSideURLMap == null) {
				serverSideURLMap = new HashMap<String, Map<Class<?>, HashSet<String>>>();
				session.setAttribute(SERVER_SIDE_MAP_ATTR_NAME, serverSideURLMap);
			}
			Map<Class<?>, HashSet<String>> allowedClassMap = serverSideURLMap.get(url);
			if(allowedClassMap == null) {
				allowedClassMap = new HashMap<Class<?>, HashSet<String>>();
				serverSideURLMap.put(url, allowedClassMap);
			}
			
			HashSet<String> allowedClassIdSet = allowedClassMap.get(clazz);
			if(allowedClassIdSet == null) {
				allowedClassIdSet = new HashSet<String>();
				allowedClassMap.put(clazz, allowedClassIdSet);
			}
			allowedClassIdSet.add(nId);
		}
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public void remapAction(String url, String toRemapURL) {
		HttpSession session = getSession();
		Map<String, Map<Class<?>, HashSet<String>>> serverSideURLMap = (Map<String, Map<Class<?>, HashSet<String>>>) session.getAttribute(SERVER_SIDE_MAP_ATTR_NAME);
		if(serverSideURLMap == null) {
			serverSideURLMap = new HashMap<String, Map<Class<?>, HashSet<String>>>();
			session.setAttribute(SERVER_SIDE_MAP_ATTR_NAME, serverSideURLMap);
		}
		Map<Class<?>, HashSet<String>> allowedClassMap = serverSideURLMap.get(url);
		if(allowedClassMap != null) {
			serverSideURLMap.put(toRemapURL, allowedClassMap);
			serverSideURLMap.remove(url);//????Do we really need this? It is to save memory
		}
	}
	
	private HttpSession getSession() {
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest().getSession();
	}
	
	private HttpServletRequest getRequest() {
		return ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
	}

}