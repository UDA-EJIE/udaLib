package com.ejie.x38.validation;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import com.ejie.x38.util.StackTraceManager;

public class ValidationServlet implements HttpRequestHandler {

	private ValidationManager validationManager;
	private CookieLocaleResolver localeResolver;
	
	private static final long serialVersionUID = 1L;
	private final static Logger logger = Logger.getLogger(ValidationServlet.class);

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {		
		try {
			int language = localeResolver.getCookieName().length()+1;
			int languageIndex = request.getHeader("Cookie").indexOf(localeResolver.getCookieName()) + language;
			Locale locale = new Locale(request.getHeader("Cookie").substring(languageIndex, languageIndex +2));//(request.getHeader("Cookie").indexOf(";"))));
			String result = validationManager.validateProperty(request.getParameter("bean"), request.getParameter("property"), request.getParameter("value"), locale);
			if (result!=null && !result.equals("error!")){
				response.setContentType("text/javascript;charset=UTF-8");
				response.setHeader("Cache-Control", "no-cache");
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
				response.getWriter().write(result);
				response.flushBuffer();
			}else if (result == null){
				response.setContentType("text/javascript;charset=UTF-8");
	            response.setHeader("Pragma", "cache");
	            response.setHeader("Expires", "0");
	            response.setHeader("Cache-Control", "private");
	            response.setStatus(HttpServletResponse.SC_OK);			
			}else{
				throw new RuntimeException("error!");
			}
		} catch (Exception e) {
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
			response.setContentType("text/javascript;charset=UTF-8");
            response.setHeader("Pragma", "cache");
            response.setHeader("Expires", "0");
            response.setHeader("Cache-Control", "private");
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
		}
	}

	//Getters & Setters
	public ValidationManager getValidationManager() {
		return validationManager;
	}

	public void setValidationManager(ValidationManager validationManager) {
		this.validationManager = validationManager;
	}

	public void setLocaleResolver(CookieLocaleResolver localeResolver) {
		this.localeResolver = localeResolver;
	}	
}