package com.ejie.x38.pif;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;


public class PifSpringServlet implements HttpRequestHandler  {

	private static final Logger logger = LoggerFactory.getLogger(PifSpringServlet.class);
	
	@Override
	public void handleRequest(HttpServletRequest httpRequest, HttpServletResponse httpResponse)
			throws ServletException, IOException {
		logger.debug("PifSpringServlet - Inicio");
		
		PifServletHelper pifServletHelper = new PifServletHelper();
		pifServletHelper.processRequest(httpRequest, httpResponse);
		
		logger.debug("PifSpringServlet - Fin");
	}
}
