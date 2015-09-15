package com.ejie.x38.control.exception.handler;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

/**
 * 
 * Clase encargada del tratamiento por defecto de las excepciones propagadas por un Controller
 * relacionadas con problemas de acceso 
 * 
 * @author UDA
 *
 */
public class MvcAccessDeniedExceptionHandler {

	private ReloadableResourceBundleMessageSource messageSource;
	
	/**
	 * 
	 * @param messageSource
	 */
	public MvcAccessDeniedExceptionHandler(ReloadableResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
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
			String content = messageSource.getMessage(exceptionClassName, null, accessDeniedException.getMessage(), locale);
			response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentLength(content.getBytes(Charset.forName(response.getCharacterEncoding())).length);
			response.getWriter().print(content);
			response.flushBuffer();
			return null;
		} else {
			//Non-AJAX request
			return MvcExceptionHandler.handle(accessDeniedException, request, response);
		}
	}
}