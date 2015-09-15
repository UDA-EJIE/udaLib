package com.ejie.x38.control.exception;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.springframework.web.method.HandlerMethod;
import org.springframework.web.method.annotation.ExceptionHandlerMethodResolver;
import org.springframework.web.servlet.mvc.method.annotation.ExceptionHandlerExceptionResolver;
import org.springframework.web.servlet.mvc.method.annotation.ServletInvocableHandlerMethod;

/**
 * 
 * Clase encargada del sobreescribir la gestión de las excepciones permitiendo que estas puedan 
 * ser capturadas fuera del Controller (las gestionará MvcExceptionHandler) mediante la anotación
 * @ExceptionHandler
 * 
 * @author UDA
 *
 */
public class MvcExceptionResolver extends ExceptionHandlerExceptionResolver {

	private List<Object> handlers;
	private List<ExceptionHandlerMethodResolver> methodResolvers;

	/**
	 * Establecer el handler de la excepción (MvcExceptionHandler)
	 * @param handler 
	 */
	public void setExceptionHandlers(List<Object> handlers) {
		this.handlers = handlers;
		List<ExceptionHandlerMethodResolver> methodResolvers = new ArrayList<ExceptionHandlerMethodResolver>();
		for (Object object : handlers){
			methodResolvers.add(new ExceptionHandlerMethodResolver(object.getClass()));
		}
		this.methodResolvers = methodResolvers;
	}
	
	/**
	 * En caso de que el Controller no disponga de método para gestionar la excepción se cede el control
	 * al MvcExceptionHandler para que gestione él la excepción
	 */
	@Override
	protected ServletInvocableHandlerMethod getExceptionHandlerMethod(HandlerMethod handlerMethod, Exception exception) {
		ServletInvocableHandlerMethod result = super.getExceptionHandlerMethod(handlerMethod, exception);
		if (result != null) {
			return result;
		}
		int index = 0;
		for (ExceptionHandlerMethodResolver exceptionHandlerMethodResolver : this.methodResolvers){
			Method method = exceptionHandlerMethodResolver.resolveMethod(exception);
			if (method != null){
				return new ServletInvocableHandlerMethod(handlers.get(index), method);
			}
			index++;
		}
		return null;
		
	}
}