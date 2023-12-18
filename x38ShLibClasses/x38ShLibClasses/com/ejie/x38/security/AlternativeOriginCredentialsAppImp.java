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

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

/**
 * 
 * @author UDA
 *
 */
public class AlternativeOriginCredentialsAppImp implements AlternativeOriginCredentialsApp{
	
	private List<String> appCodes;
	private boolean existAditionalsAppCodes = false;
	
	
	public AlternativeOriginCredentialsAppImp(List<String> appCodes){
		existAditionalsAppCodes = true;
		this.appCodes = appCodes;
	}
	
	public AlternativeOriginCredentialsAppImp(String appCode){
		existAditionalsAppCodes = true;
		this.appCodes = new ArrayList<String>();
		this.appCodes.add(appCode);
	}
	
	public List<String> getAppCodes(HttpServletRequest httpRequest){
		return this.appCodes;
	}
	
	public boolean existAditionalsAppCodes(HttpServletRequest httpRequest){
		return existAditionalsAppCodes;
	}
}