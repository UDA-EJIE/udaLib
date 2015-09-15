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

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author UDA
 *
 */
public interface Credentials extends Serializable {

	//Functions to manage the SubjectCert's data  
	public String toString();	
	public void loadCredentialsData(PerimetralSecurityWrapper perimetralSecurityWrapper, HttpServletRequest request);
	
	//Getters & Setters
	public String getNif();
	public void setNif(String nif);
	
	public String getPolicy();
	public void setPolicy(String policy);

	public String getUserName();
	public void setUserName(String userName);
	
	public String getFullName();
	public void setfullName(String fullName);
	
	public String getSurname();
	public void setSurname(String surname);
	
	public String getName();
	public void setName(String name);
	
	public String getPosition();
	public void setPosition(String position);
	
	public String getUidSession();
	public void setUidSession(String uidSession);
	
	public boolean getIsCertificate();
	public void setIsCertificate(boolean isCertificate);
	
	public String getUdaValidateSessionId();
	public void setUdaValidateSessionId(String udaXLNetsSessionId);
	
	public Vector<String> getUserProfiles();
	public void setUserProfiles(Vector<String> userProfiles);
	
	public boolean getDestroySessionSecuritySystem();
	public void setDestroySessionSecuritySystem(boolean destroySessionSecuritySystem);
	
	//Functions to manage the SubjectCert's data  
	public String getSubjectCert(String data);
	public boolean containsSubjectCert(String id);
	public void deleteSubjectCert(String property);
	public void setSubjectCert(String property, String value);
	
	
}