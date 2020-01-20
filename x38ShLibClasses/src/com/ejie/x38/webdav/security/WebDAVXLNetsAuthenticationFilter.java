package com.ejie.x38.webdav.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.DatatypeConverter;

import org.springframework.web.filter.GenericFilterBean;

/**
 * Servlet Filter implementation class Y31HttpBasicAuthenticationFilter
 */
public class WebDAVXLNetsAuthenticationFilter extends GenericFilterBean {

	public String paramName = "c";
	
	/**
	 * Default constructor.
	 */
	public WebDAVXLNetsAuthenticationFilter() {
	}

	public void setParamName(String paramName){
		this.paramName = paramName;
	}
	
	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException, ServletException {

		HttpServletRequest httpRequest = (HttpServletRequest) request;
		HttpServletResponse httpResponse = (HttpServletResponse) response;

		String webdavXLNetsParam = httpRequest.getParameter(this.paramName);
		
		WebDAVHttpServletRequestWrapper wrapedRequest = new WebDAVHttpServletRequestWrapper(httpRequest);
		
		
		if (webdavXLNetsParam!=null){
			String param = new String(DatatypeConverter.parseBase64Binary(webdavXLNetsParam));
			
			String[] arrParam = param.split("&");

			for (String s : arrParam) {
				String[] paramKeyValue = s.split("=");
				final Cookie cookie = new Cookie(paramKeyValue[0], paramKeyValue[1]);
				cookie.setSecure(true);
				wrapedRequest.addCookie(cookie);

			}
		}
		
		chain.doFilter(wrapedRequest, httpResponse);
	}
}
