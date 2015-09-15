package com.ejie.x38.control.exception.handler;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.ModelAndView;

import com.ejie.x38.validation.ValidationManager;

/**
 * 
 * Clase encargada del tratamiento por defecto de las excepciones propagadas por un Controller
 * relacionadas con problemas de validación
 * 
 * @author UDA
 *
 */
public class MvcValidationExceptionHandler {

	private ValidationManager validationManager;
	
	/**
	 * 
	 * @param validationManager
	 */
	public MvcValidationExceptionHandler(ValidationManager validationManager) {
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
			return MvcExceptionHandler.handle(exception, request, response);
		}
	}
}