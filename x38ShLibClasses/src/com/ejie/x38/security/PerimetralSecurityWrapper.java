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

import java.util.Vector;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author UDA
 *
 */
public interface PerimetralSecurityWrapper {

	public abstract String getUserConnectedUserName(HttpServletRequest httpRequest);

	public abstract String getUserConnectedUidSession(HttpServletRequest httpRequest);
	
	public String getUserPosition(HttpServletRequest httpRequest);

	public String getURLLogin(String originalURL);

	public Vector<String> getUserInstances(HttpServletRequest httpRequest);

	public void logout(HttpServletRequest httpRequest);
}