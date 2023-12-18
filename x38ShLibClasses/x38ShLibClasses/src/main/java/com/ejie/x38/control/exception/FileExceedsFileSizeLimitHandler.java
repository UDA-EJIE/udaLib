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
package com.ejie.x38.control.exception;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.ejie.x38.control.exception.handler.MvcExceptionHandlerHelper;
import com.ejie.x38.json.JSONObject;
import com.ejie.x38.validation.ValidationManager;

/**
 * Handler utilizado para gestionar la excepción producida por superar el tamaño
 * máximo de envío de un fichero.
 * 
 * @author UDA
 * 
 */
public class FileExceedsFileSizeLimitHandler implements HandlerExceptionResolver{
	
	private static final Logger logger = LoggerFactory.getLogger(FileExceedsFileSizeLimitHandler.class);
	
	@Resource
	private ReloadableResourceBundleMessageSource messageSource;
	
	@Autowired
	private ValidationManager validationManager;
	
	
	
	@Override
	
	@ExceptionHandler(value=MaxUploadSizeExceededException.class)
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {
		if ( ex instanceof MaxUploadSizeExceededException ) {
			try {
				
				Locale locale = LocaleContextHolder.getLocale();
				String messageError = messageSource.getMessage(MaxUploadSizeExceededException.class.getSimpleName(), null, locale);
				Map<String, Object> rupFeedbackMsg = validationManager.getRupFeedbackMsg(messageError, "error");
				JSONObject messageJSON = validationManager.getMessageJSON(null, rupFeedbackMsg, "error");
				String content = messageJSON.toString();
				// Se comprueba si es necesario tratar el error de acuerdo a lo requerido por la emulación de iframe.
				
				return new MvcExceptionHandlerHelper().processException(ex, request, response, content, HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE);
				
				
			} catch (IOException e) {
				logger.error(e.getMessage());
				throw new RuntimeException(e);
			}
		}
		return null;
	}
}
