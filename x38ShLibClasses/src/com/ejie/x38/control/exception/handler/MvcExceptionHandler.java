package com.ejie.x38.control.exception.handler;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * Clase encargada del tratamiento por defecto de las excepciones propagadas por un Controller
 * 
 * @author UDA
 *
 */
public class MvcExceptionHandler {

	private ReloadableResourceBundleMessageSource messageSource;
	
	/**
	 * 
	 * @param messageSource
	 */
	public MvcExceptionHandler(ReloadableResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	/**
	 * Gestor por defecto de Excepciones
	 * @param exception
	 * @param request 
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@ExceptionHandler
	public ModelAndView handleException (Exception exception, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Locale locale = LocaleContextHolder.getLocale();
		String exceptionClassName = exception.getClass().getSimpleName();
		if (request.getHeaders("X-Requested-With").hasMoreElements()) {
			//AJAX request;
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, messageSource.getMessage(exceptionClassName, null, exception.getMessage(), locale));
			return null;
		} else {
			//Non-AJAX request
			return MvcExceptionHandler.handle(exception, request, response);
		}
	}
	
	
	/**
	 * Gestor de las excepciones NO-AJAX para los handlers
	 * @param exception
	 * @param request
	 * @param response
	 * @return
	 */
	public static ModelAndView handle(Exception exception, HttpServletRequest request, HttpServletResponse response){
		//Non-AJAX request
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("error");
		modelAndView.addObject("exception_name", exception.getClass().getName());
		modelAndView.addObject("exception_message", exception.getMessage());
		StringBuilder sbTrace = new StringBuilder();
		for (StackTraceElement trace : exception.getStackTrace()) {
			sbTrace.append(trace.toString()).append("</br>");
		}
		modelAndView.addObject("exception_trace", sbTrace);
		return modelAndView;
	}
}