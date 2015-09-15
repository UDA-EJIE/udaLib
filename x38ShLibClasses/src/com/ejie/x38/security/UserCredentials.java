package com.ejie.x38.security;

import java.io.Serializable;

import javax.servlet.http.HttpServletRequest;

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