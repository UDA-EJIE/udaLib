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
//			String param = new String(Base64.decodeBase64(webdavXLNetsParam));
			String param = new String(DatatypeConverter.parseBase64Binary(webdavXLNetsParam));
			
			String[] arrParam = param.split("&");
			
			for (int i=0;i<arrParam.length;i++){
				String[] paramKeyValue = arrParam[i].split("=");
//				httpResponse.addCookie(new Cookie(paramKeyValue[0], paramKeyValue[1]));
				wrapedRequest.addCookie(new Cookie(paramKeyValue[0], paramKeyValue[1]));
				
			}
		}
		
		chain.doFilter(wrapedRequest, httpResponse);
	}
}
