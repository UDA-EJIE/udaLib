/*
* Copyright 2012 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.control.exception;

/**
 * 
 * Excepción del componente IframeXHREmulation
 * 
 * @author UDA
*/
public class IframeXHREmulationException extends RuntimeException{
	protected Throwable throwable;
	private static final long serialVersionUID = 1L;

	public IframeXHREmulationException(String message) {
		super(message);
	}

	public IframeXHREmulationException(String message, Throwable throwable) {
		super(message);
		this.throwable = throwable;
	}

	public IframeXHREmulationException(Throwable cause) {
		super(cause);
	}

	public Throwable getCause() {
		return throwable;
	}
}