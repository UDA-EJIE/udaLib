package com.ejie.x38.webdav.security;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class WebDAVHttpServletRequestWrapper extends HttpServletRequestWrapper{

	public List<Cookie> extraCookies;
	
	public WebDAVHttpServletRequestWrapper(
			HttpServletRequest paramHttpServletRequest) {
		super(paramHttpServletRequest);
		extraCookies = new ArrayList<Cookie>();
		Cookie[] cookies = paramHttpServletRequest.getCookies();
		
		if (cookies !=null && cookies.length>0){
			List<Cookie> asList = Arrays.asList();
			extraCookies.addAll(asList);
		}
	}
	
	
	public void addCookie(Cookie cookie){
		extraCookies.add(cookie);
	}


	@Override
	public Cookie[] getCookies() {
		Cookie[] cookieArray = new Cookie[extraCookies.size()];
		return this.extraCookies.toArray(cookieArray);
	}
	
	
}