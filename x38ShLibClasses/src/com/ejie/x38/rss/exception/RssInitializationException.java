package com.ejie.x38.rss.exception;


/**
 * Error producido en el proceso de inicialización del componente RSS.
 * 
 * @author UDA
 *
 */
public class RssInitializationException extends RuntimeException {

	public RssInitializationException() {
		super();
	}

	public RssInitializationException(String message, Throwable cause) {
		super(message, cause);
	}

	public RssInitializationException(String message) {
		super(message);
	}

	public RssInitializationException(Throwable cause) {
		super(cause);
	}
}
