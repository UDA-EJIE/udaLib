package com.ejie.x38.control.exception;

public class ControlException extends RuntimeException{
	protected Throwable throwable;
	private static final long serialVersionUID = 1L;

	public ControlException(String message) {
		super(message);
	}

	public ControlException(String message, Throwable throwable) {
		super(message);
		this.throwable = throwable;
	}

	public Throwable getCause() {
		return throwable;
	}
}