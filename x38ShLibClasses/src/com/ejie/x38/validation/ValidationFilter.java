/*
* Copyright 2012 E.J.I.E., S.A.
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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Locale;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import com.ejie.x38.util.DateTimeManager;
import com.ejie.x38.util.StackTraceManager;

/**
 * 
 * @author UDA
 *
 */
@Deprecated
public class ValidationFilter extends DelegatingFilterProxy {

	private final static Logger logger = LoggerFactory
		.getLogger(ValidationFilter.class);

	private ValidationManager validationManager;
	private CookieLocaleResolver localeResolver;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;
		
		logger.debug("acces in the Validation filter.");

		if ((req.getHeader("validation") != null)
				&& (req.getHeader("validation").equalsIgnoreCase("true"))) {
			
				logger.info("The request require a data validation");
				
			try {
				ValidationRequestWrapper requestWrapper = new ValidationRequestWrapper(req);
				String data = readFormRequest(requestWrapper);
				String result = null;
					
				if (requestWrapper.getContentType() != null && requestWrapper.getContentType().contains("application/json")){
					int language = localeResolver.getCookieName().length()+1;
					int languageIndex = req.getHeader("Cookie").indexOf(localeResolver.getCookieName()) + language;
					Locale locale = new Locale(req.getHeader("Cookie").substring(languageIndex, languageIndex +2));
					
					result = validationManager.validateObject(req.getHeader("bean"), data, locale);
				}
				
				if (result == null){
					//request data are correct
					logger.info("Request data are correct. It continues processing the call.");
					filterChain.doFilter(requestWrapper, response);
					logger.debug("Exit from the Validation filter.");
				} else {
					logger.info("Request data are not correct.");
					response.setContentType("text/javascript;charset=UTF-8");
					res.setHeader("Cache-Control", "no-cache");
					res.setHeader("Expires", DateTimeManager.getHttpExpiredDate());
					
					if(!result.equals("error!")){
						res.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
						res.getWriter().write(result);
					} else {
						res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
						res.getWriter().write("Error in the validate. The structure or morphology of the data is incorrect, review the data sent.");
					}
					logger.debug("Exit from the Validation filter.");
					res.flushBuffer();
				}
				
			} catch (Exception e) {
				logger.error(StackTraceManager.getStackTrace(e));
				HttpServletResponse resp = (HttpServletResponse) response;
				try {
					resp.sendRedirect(req.getContextPath() + "/error");
				} catch (IOException e1) {
					logger.error(StackTraceManager.getStackTrace(e1));
				}
			}
		} else {
			logger.info("The request not require a data validation");
			filterChain.doFilter(request, response);
			logger.debug("Exit from the Validation filter.");
		}
	}

	private String readFormRequest(HttpServletRequest request) {
		BufferedReader bufferedReader = null;
		InputStreamReader inputStreamReader = null;
		InputStream inputStream = null;
		try {
			inputStream = request.getInputStream();
			String charsetName = request.getCharacterEncoding();
			if (charsetName == null) {
				charsetName = "UTF-8";
			}
			inputStreamReader = new InputStreamReader(inputStream, charsetName);
			bufferedReader = new BufferedReader(inputStreamReader);
			StringBuilder sb = new StringBuilder();
			String line = bufferedReader.readLine();
			while (line != null) {
				sb.append(line + "\n");
				line = bufferedReader.readLine();
			}
			return sb.toString();
		} catch (Exception e) {
			logger.error(StackTraceManager.getStackTrace(e));
			return null;
		} finally {
			try {
				bufferedReader.close();
				inputStreamReader.close();
				inputStream.close();
			} catch (Exception e) {
				logger.error(StackTraceManager.getStackTrace(e));
				return null;
			}
		}

	}

	// Getters & Setters
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