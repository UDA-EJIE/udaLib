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

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * 
 * @author UDA
 *
 */
public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint,
		Ordered {
	private static final Logger logger = LoggerFactory
			.getLogger(MyAuthenticationEntryPoint.class);

	private int order = Integer.MAX_VALUE;

	private PerimetralSecurityWrapper perimetralSecurityWrapper;

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		if (authException != null)
			logger.debug("Authentication Exception: "+ authException.getMessage());
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		logger.info("XLNET Session isn't valid or not created!");
		String url = getPerimetralSecurityWrapper().getURLLogin(httpRequest.getRequestURL().toString());
		logger.info("Redirecting to next URL:" + url);
		httpResponse.sendRedirect(url);

		
	}

	@Override
	public int getOrder() {
		return order;
	}

	// Getters & Setters
	public PerimetralSecurityWrapper getPerimetralSecurityWrapper() {
		return perimetralSecurityWrapper;
	}

	public void setPerimetralSecurityWrapper(
			PerimetralSecurityWrapper perimetralSecurityWrapper) {
		this.perimetralSecurityWrapper = perimetralSecurityWrapper;
	}
}