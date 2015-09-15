package com.ejie.x38.control;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.ModelAndView;

import com.ejie.x38.util.StackTraceManager;

/**
 * @author UDA
 * 
 * Valido si se quiere implementar un sistema de captura de excepciones centralizado.
 * Actualmente, no se utiliza.
 */
public class ExceptionResolver implements HandlerExceptionResolver{

	@Override
	public ModelAndView resolveException(HttpServletRequest request, HttpServletResponse response, Object handler, Exception exception) {
		ModelAndView mav = new ModelAndView();
		mav.setViewName("controllerErrors");
		mav.addObject("errors", exception.getMessage());
		mav.addObject("stackTrace", StackTraceManager.getStackTrace(exception));
		return mav;
	}
}