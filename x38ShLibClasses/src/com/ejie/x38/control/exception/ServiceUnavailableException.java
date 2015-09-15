package com.ejie.x38.control.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(value=HttpStatus.SERVICE_UNAVAILABLE)
public class ServiceUnavailableException extends ControlException{
	
	private static final long serialVersionUID = 1L;
	
	public ServiceUnavailableException(String message) {
		super(message);
	}	
}