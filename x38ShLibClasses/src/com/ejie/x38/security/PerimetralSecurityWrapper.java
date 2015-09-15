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

import java.io.IOException;
import java.util.Vector;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * 
 * @author UDA
 *
 */
public interface PerimetralSecurityWrapper {
	
	public String validateSession(HttpServletRequest httpRequest, HttpServletResponse response) throws IOException;
	
	public String getUserConnectedUserName(HttpServletRequest httpRequest);

	public String getUserConnectedUidSession(HttpServletRequest httpRequest);
	
	public String getUdaValidateSessionId(HttpServletRequest httpRequest);
	
	public String getUserPosition(HttpServletRequest httpRequest);

	public String getURLLogin(String originalURL, boolean ajax);

	public String getPolicy(HttpServletRequest httpRequest);
	
	public boolean getIsCertificate(HttpServletRequest httpRequest);
	
	public Vector<String> getUserInstances(HttpServletRequest httpRequest);

	public void logout(HttpServletRequest httpRequest, HttpServletResponse httpResponse);
	
	public String getNif(HttpServletRequest httpRequest);
}