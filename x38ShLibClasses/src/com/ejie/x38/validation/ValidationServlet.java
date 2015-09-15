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
package com.ejie.x38.validation;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import com.ejie.x38.util.DateTimeManager;
import com.ejie.x38.util.StackTraceManager;

/**
 * 
 * @author UDA
 *
 */
public class ValidationServlet implements HttpRequestHandler {

	private ValidationManager validationManager;
	private CookieLocaleResolver localeResolver;
	
	private static final long serialVersionUID = 1L;
	private final static Logger logger =  LoggerFactory.getLogger(ValidationServlet.class);

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)throws ServletException, IOException {		
		
		logger.info("The request is a validation. Validation begins.");
		
		try {
			int language = localeResolver.getCookieName().length()+1;
			int languageIndex = request.getHeader("Cookie").indexOf(localeResolver.getCookieName()) + language;
			Locale locale = new Locale(request.getHeader("Cookie").substring(languageIndex, languageIndex +2));//(request.getHeader("Cookie").indexOf(";"))));
			String result = validationManager.validateProperty(request.getParameter("bean"), request.getParameter("property"), request.getParameter("value"), locale);
			
			response.setContentType("text/javascript;charset=UTF-8");
			response.setHeader("Cache-Control", "no-cache");
			response.setHeader("Expires", DateTimeManager.getHttpExpiredDate());
			
			if (result == null){
	            response.setStatus(HttpServletResponse.SC_OK);		
			}else if (result!=null && !result.equals("error!")){
				response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
				response.getWriter().write(result);
			}else{
				throw new RuntimeException("error!");
			}
		} catch (Exception e) {
			logger.error(StackTraceManager.getStackTrace(e));
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            response.getWriter().write("Error in the validate. The structure or morphology of the data is incorrect, review the data sent.");
		}
		
		logger.debug("Exit from the Validation Servlet.");
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