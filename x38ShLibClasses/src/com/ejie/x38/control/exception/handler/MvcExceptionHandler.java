package com.ejie.x38.control.exception.handler;

import java.io.IOException;
import java.nio.charset.Charset;
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
			String content = messageSource.getMessage(exceptionClassName, null, exception.getMessage(), locale);
			response.setStatus(HttpServletResponse.SC_NOT_ACCEPTABLE);
			response.setContentLength(content.getBytes(Charset.forName(response.getCharacterEncoding())).length);
			response.getWriter().print(content);
			response.flushBuffer();
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