package com.ejie.x38.security;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.springframework.core.Ordered;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

public class MyAuthenticationEntryPoint implements AuthenticationEntryPoint,
		Ordered {
	private static final Logger logger = Logger
			.getLogger(MyAuthenticationEntryPoint.class);

	private int order = Integer.MAX_VALUE;

	private PerimetralSecurityWrapper perimetralSecurityWrapper;

	@Override
	public void commence(HttpServletRequest request,
			HttpServletResponse response, AuthenticationException authException)
			throws IOException, ServletException {
		if (authException != null)
			logger.trace("Authentication Exception: "+ authException.getMessage());
		
		HttpServletResponse httpResponse = (HttpServletResponse) response;
		HttpServletRequest httpRequest = (HttpServletRequest) request;
		
		logger.debug("XLNET Session isn't valid!");
		String url = getPerimetralSecurityWrapper().getURLLogin(httpRequest.getRequestURL().toString());
		logger.debug("Redirecting to next URL:" + url);
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