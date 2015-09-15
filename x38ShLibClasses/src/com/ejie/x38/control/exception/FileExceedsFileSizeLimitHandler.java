package com.ejie.x38.control.exception;

import java.io.IOException;
import java.util.Locale;
import java.util.Map;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.ModelAndView;

import com.ejie.x38.json.JSONObject;
import com.ejie.x38.validation.ValidationManager;

public class FileExceedsFileSizeLimitHandler implements org.springframework.web.servlet.HandlerExceptionResolver{
	
	@Resource
	private ReloadableResourceBundleMessageSource messageSource;
	
	@Autowired
	private ValidationManager validationManager;
	
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response,
			Object handler, Exception ex) {
		if ( ex instanceof MaxUploadSizeExceededException ) {
			try {
				
				Locale locale = LocaleContextHolder.getLocale();
				String messageError = messageSource.getMessage(MaxUploadSizeExceededException.class.getSimpleName(), null, locale);
				
				Map<String, Object> rupFeedbackMsg = validationManager.getRupFeedbackMsg(messageError, "error");
				JSONObject messageJSON = validationManager.getMessageJSON(null, rupFeedbackMsg, "error");
				
				response.sendError(HttpServletResponse.SC_REQUEST_ENTITY_TOO_LARGE, messageJSON.toString());
			} catch (IOException e) {
				e.printStackTrace();
			}
			return null;
		}
		return null;
	}
}
