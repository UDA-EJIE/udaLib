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
package com.ejie.x38.security;

import java.io.Serializable;
import java.util.Vector;

/**
 * 
 * @author UDA
 *
 */
public class UserCredentials implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private String nif;
	private String policy;
	private String userName;
	private String position;
	private String uidSession;
	private boolean isCertificate;
	private String udaValidateSessionId;
	private Vector<String> userProfiles;

	public UserCredentials(Vector<String> userProfiles, String userName, String nif,
			String uidSession, String position, String udaValidateSessionId, String policy, boolean isCertificate) {
		super();
		this.nif = nif;
		this.userName = userName;
		this.uidSession = uidSession;
		this.position = position;
		this.udaValidateSessionId = udaValidateSessionId;
		this.userProfiles = userProfiles;
		this.isCertificate = isCertificate;
		this.policy = policy;
	}

	public String toString() {

		StringBuffer strBuffer = new StringBuffer();

		strBuffer.append("UserCredentials [");
		strBuffer.append("userName=").append(userName).append(";");
		strBuffer.append("uidSession=").append(uidSession).append(";");
		strBuffer.append("position=").append(position).append(";");
		
		if(udaValidateSessionId != null){
			strBuffer.append("udaXLNetsSessionId=").append("Object NOT NULL (info protected)").append(";");
		} else {
			strBuffer.append("udaXLNetsSessionId=").append("NULL").append(";");
		}
		
		if (userProfiles != null){
			if (userProfiles.size() > 0){
				strBuffer.append("userProfiles=").append("Object NOT NULL. Its size is ").append(userProfiles.size()).append(" (info protected)").append(";");
			} else {
				strBuffer.append("userProfiles=").append("The user doesn't have permissions").append(";");
			}
		} else {
			strBuffer.append("userProfiles=").append("NULL").append(";");
		}
		
		strBuffer.append("]");

		return strBuffer.toString();
	}
	
	//Getters & Setters
	
	public String getNif() {
		return this.nif;
	}

	public void setNif(String nif) {
		this.nif = nif;
	}
	
	public String getPolicy() {
		return this.policy;
	}

	public void setPolicy(String policy) {
		this.policy = policy;
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
	
	public String getUidSession() {
		return uidSession;
	}

	public void setUidSession(String uidSession) {
		this.uidSession = uidSession;
	}
	
	public boolean getIsCertificate() {
		return this.isCertificate;
	}

	public void setIsCertificate(boolean isCertificate) {
		this.isCertificate = isCertificate;
	}
	
	public String getUdaValidateSessionId() {
		return udaValidateSessionId;
	}

	public void setUdaValidateSessionId(String udaXLNetsSessionId) {
		this.udaValidateSessionId = udaXLNetsSessionId;
	}
	
	public Vector<String> getUserProfiles() {
		return userProfiles;
	}

	public void setUserProfiles(Vector<String> userProfiles) {
		this.userProfiles = userProfiles;
	}
}