package com.ejie.x38.control.exception;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.ejie.x38.validation.ValidationManager;

/**
 * 
 * Clase encargada del tratamiento por defecto de las excepciones propagadas por un Controller
 * 
 * @author UDA
 *
 */
@Deprecated
public class MvcExceptionHandler {

	private ReloadableResourceBundleMessageSource messageSource;
	private ValidationManager validationManager;
	
	/**
	 * Constructor recibiendo el objeto para la resolución de literales (i18n)
	 * @param messageSource
	 */
	public MvcExceptionHandler(ReloadableResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
	
	public MvcExceptionHandler(ReloadableResourceBundleMessageSource messageSource, ValidationManager validationManager) {
		this.messageSource = messageSource;
		this.validationManager = validationManager;
	}
	
	
	/**
	 * Gestor por defecto de los errores en las validaciones mediante la anotacion Validated
	 * @param bindException
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@ExceptionHandler(value={MethodArgumentNotValidException.class})
	public ModelAndView handleMethodArgumentNotValidException (MethodArgumentNotValidException methodArgumentNotValidException, HttpServletRequest request, HttpServletResponse response) throws IOException {
		return this.processBindingResult(methodArgumentNotValidException, methodArgumentNotValidException.getBindingResult(), request, response);
	}
	@ExceptionHandler(value={BindException.class})
	public ModelAndView handleBindException (BindException bindException, HttpServletRequest request, HttpServletResponse response) throws IOException {
		return this.processBindingResult(bindException, bindException.getBindingResult(), request, response);
	}
	private ModelAndView processBindingResult (Exception exception, BindingResult bindingResult, HttpServletRequest request, HttpServletResponse response) throws IOException {
		if (request.getHeaders("X-Requested-With").hasMoreElements() && bindingResult.hasFieldErrors()) {
			//AJAX request;
			Map<String, List<String>> errorMap = validationManager.getErrorsAsMap(bindingResult);
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, validationManager.getMessageJSON(errorMap).toString());
			return null;
		}else{
			//Non-AJAX request
			return this.handle(exception, request, response);
		}
	}
	
	/**
	 * Gestor por defecto de los errores de permisos de acceso
	 * @param accessDeniedException
	 * @param request
	 * @param response
	 * @return
	 * @throws IOException
	 */
	@ExceptionHandler(value=AccessDeniedException.class)
	public ModelAndView handleAccessDeniedException (AccessDeniedException accessDeniedException, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Locale locale = LocaleContextHolder.getLocale();
		String exceptionClassName = accessDeniedException.getClass().getSimpleName();
		if (request.getHeaders("X-Requested-With").hasMoreElements()) {
			//AJAX request;
			response.sendError(HttpServletResponse.SC_UNAUTHORIZED, messageSource.getMessage(exceptionClassName, null, accessDeniedException.getMessage(), locale));
			return null;
		} else {
			//Non-AJAX request
			return this.handle(accessDeniedException, request, response);
		}
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
	public ModelAndView handle (Exception exception, HttpServletRequest request, HttpServletResponse response) throws IOException {
		Locale locale = LocaleContextHolder.getLocale();
		String exceptionClassName = exception.getClass().getSimpleName();
		if (request.getHeaders("X-Requested-With").hasMoreElements()) {
			//AJAX request;
			response.sendError(HttpServletResponse.SC_NOT_ACCEPTABLE, messageSource.getMessage(exceptionClassName, null, exception.getMessage(), locale));
			return null;
		} else {
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
}