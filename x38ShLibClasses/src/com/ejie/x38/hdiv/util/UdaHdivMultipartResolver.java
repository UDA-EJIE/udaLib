package com.ejie.x38.hdiv.util;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.web.multipart.HdivCommonsMultipartResolver;

/**
 * Proporciona el soporte de peticiones multipart/ mediante metodo http PUT. Este cambio viene dado por la modificacion realizada en la
 * clase RequestParamMethodArgumentResolver en la versión 3.1.1 de Spring, en la que se elimina la limitación de que las peticiones
 * multipart/ deban de ser realizadas mediante POST.
 * 
 * @author UDA
 * 
 */
public class UdaHdivMultipartResolver extends HdivCommonsMultipartResolver {

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.springframework.web.multipart.commons.CommonsMultipartResolver#isMultipart(javax.servlet.http.HttpServletRequest)
	 */
	@Override
	public boolean isMultipart(final HttpServletRequest request) {
		return (request != null && isMultipartContent(request));
	}

	/**
	 * Determina si la petición es multipart.
	 * 
	 * @param request HttpServletRequest.
	 * @return Devuelve true/false dependiendo de si la petición es multipart.
	 */
	private boolean isMultipartContent(final HttpServletRequest request) {
		String contentType = request.getContentType();
		return (contentType != null && contentType.toLowerCase().startsWith("multipart/"));
	}
}
