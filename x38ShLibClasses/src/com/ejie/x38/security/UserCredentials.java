/*
* Copyright 2011 E.J.I.E., S.A.
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
package com.ejie.x38.security;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author UDA
 *
 */
public class UserCredentials implements Serializable {

	private static final long serialVersionUID = 1L;
	private String userName;
	private String uidSession;
	private String position;
	private transient HttpServletRequest httpRequest;

	public UserCredentials(HttpServletRequest httpRequest, String userName,
			String uidSession, String position) {
		super();
		this.userName = userName;
		this.uidSession = uidSession;
		this.position = position;
		this.httpRequest = httpRequest;
	}

	public String toString() {

		StringBuffer strBuffer = new StringBuffer();

		strBuffer.append("UserCredentials [");
		strBuffer.append("userName=").append(userName).append(";");
		strBuffer.append("uidSession=").append(uidSession).append(";");
		strBuffer.append("position=").append(position).append(";");
		if (httpRequest != null) strBuffer.append("httpRequest=").append(httpRequest.toString()).append(";");
		else strBuffer.append("httpRequest=NULL;");

		strBuffer.append("]");

		return strBuffer.toString();
	}

	public HttpServletRequest getHttpRequest() {
		return httpRequest;
	}

	public void setHttpRequest(HttpServletRequest httpRequest) {
		this.httpRequest = httpRequest;
	}

	public String getUidSession() {
		return uidSession;
	}

	public void setUidSession(String uidSession) {
		this.uidSession = uidSession;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getPosition() {
		return position;
	}

	public void setPosition(String position) {
		this.position = position;
	}
}