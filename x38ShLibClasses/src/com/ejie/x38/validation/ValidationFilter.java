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

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.web.filter.DelegatingFilterProxy;
import org.springframework.web.servlet.i18n.CookieLocaleResolver;

import com.ejie.x38.util.StackTraceManager;

public class ValidationFilter extends DelegatingFilterProxy {

	private final static Logger logger = Logger
			.getLogger(ValidationFilter.class);

	private ValidationManager validationManager;
	private CookieLocaleResolver localeResolver;

	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain filterChain) throws ServletException, IOException {
		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse res = (HttpServletResponse) response;

		if ((req.getHeader("validation") != null)
				&& (req.getHeader("validation").equalsIgnoreCase("true"))) {
			try {
				ValidationRequestWrapper requestWrapper = new ValidationRequestWrapper(
						req);
				String data = readFormRequest(new ValidationRequestWrapper(
						requestWrapper));
				int language = localeResolver.getCookieName().length()+1;
				//Locale locale = new Locale(requestWrapper.getHeader("Cookie").substring(language,(requestWrapper.getHeader("Cookie").indexOf(";"))));
				int languageIndex = requestWrapper.getHeader("Cookie").indexOf(localeResolver.getCookieName()) + language;
				Locale locale = new Locale(requestWrapper.getHeader("Cookie").substring(languageIndex, languageIndex +2));
				String result = validationManager.validateObject(
						req.getHeader("bean"), data, locale);
				if (result != null && !result.equals("error!")) {
					response.setContentType("text/javascript;charset=UTF-8");
					res.setHeader("Cache-Control", "no-cache");
					res.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
					res.getWriter().write(result);
					res.flushBuffer();
					logger.log(Level.TRACE, "Response sent");
				} else if (result == null) {
					filterChain.doFilter(requestWrapper, response);
					logger.log(Level.TRACE, "Response sent");
				} else if (result.equals("error!")) {
					response.setContentType("text/javascript;charset=UTF-8");
					res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
					res.setHeader("Pragma", "cache");
					res.setHeader("Expires", "0");
					res.setHeader("Cache-Control", "private");
					res.getWriter().write("Incorrect data.");
					res.flushBuffer();					
				}
			} catch (Exception e) {
				logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
				HttpServletResponse resp = (HttpServletResponse) response;
				try {
					resp.sendRedirect(req.getContextPath() + "/error");
				} catch (IOException e1) {
					logger.log(Level.ERROR, StackTraceManager.getStackTrace(e1));
				}
			}
		} else {
			filterChain.doFilter(request, response);
			logger.log(Level.TRACE, "Response sent");
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
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
			return null;
		} finally {
			try {
				bufferedReader.close();
				inputStreamReader.close();
				inputStream.close();
			} catch (Exception e) {
				logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
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