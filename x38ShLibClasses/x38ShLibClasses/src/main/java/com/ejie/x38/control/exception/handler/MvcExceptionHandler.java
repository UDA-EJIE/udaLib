package com.ejie.x38.control.exception.handler;

import java.io.IOException;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.ejie.x38.util.StaticsContainer;

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
		String content = messageSource.getMessage(exceptionClassName, null, exception.getMessage(), locale);
		
		return new MvcExceptionHandlerHelper().processException(exception, request, response, content, HttpServletResponse.SC_NOT_ACCEPTABLE);

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

		modelAndView.addObject("exception_name", StaticsContainer.isDetailedError() ? exception.getClass().getName() : StaticsContainer.getDetailedErrorMessageHidden());
		modelAndView.addObject("exception_message", StaticsContainer.isDetailedError() ? exception.getMessage() : StaticsContainer.getDetailedErrorMessageHidden());
		if (StaticsContainer.isDetailedError()) {
			StringBuilder sbTrace = new StringBuilder();
			for (StackTraceElement trace : exception.getStackTrace()) {
				sbTrace.append(trace.toString()).append("</br>");
			}
			modelAndView.addObject("exception_trace", sbTrace);
		} else {
			modelAndView.addObject("exception_trace", StaticsContainer.getDetailedErrorMessageHidden());
		}

		return modelAndView;
	}
	
	/**
	 * Gestor de las excepciones por acceso denegado.
	 * @param exception
	 * @param request
	 * @param response
	 * @return
	 */
	public static ModelAndView handleAccessDenied(Exception exception, HttpServletRequest request, HttpServletResponse response){
		request.setAttribute("SPRING_SECURITY_403_EXCEPTION", exception);
		response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		
		ModelAndView modelAndView = new ModelAndView();
		modelAndView.setViewName("accessDenied");
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