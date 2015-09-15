package com.ejie.x38.control.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.METHOD_FAILURE)
public class MethodFailureException extends ControlException{
	
	private static final long serialVersionUID = 1L;
	
	public MethodFailureException(String message) {
		super(message);
	}
}