package com.ejie.x38.hdiv.error;

import java.util.List;

import org.hdiv.filter.ValidatorError;

public class ErrorResponse {

	private final String message;

	private final List<ValidatorError> errors;

	public ErrorResponse(final String message, final List<ValidatorError> errors) {
		this.message = message;
		this.errors = errors;
	}

	public String getMessage() {
		return message;
	}

	public List<ValidatorError> getErrors() {
		return errors;
	}

}
