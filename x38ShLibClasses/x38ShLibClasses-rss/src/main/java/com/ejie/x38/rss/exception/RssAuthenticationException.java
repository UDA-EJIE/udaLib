package com.ejie.x38.rss.exception;

import org.springframework.security.core.AuthenticationException;

/**
 * Error producido en el proceso de autenticación del usuario que accede 
 * al contenido del feed RSS. 
 * 
 * @author UDA
 *
 */
public class RssAuthenticationException extends AuthenticationException {


	public RssAuthenticationException(String message, Throwable cause) {
		super(message, cause);
	}

	public RssAuthenticationException(String message) {
		super(message);
	}

}
