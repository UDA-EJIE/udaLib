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
package com.ejie.x38.util;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 
 * @author UDA
 *
 */
//Getion asociada al acelerador de codigo de produccion (https => http) en EJIE
public class ManagementUrl {
	
	private static final Logger logger = LoggerFactory.getLogger(ManagementUrl.class);

	//Método que determina si la llamada esta o no acelerada 
	public static boolean isAcceleratedUrl(HttpServletRequest request){
		if (request.getHeader ("N38_URL") != null){
			logger.info("The aplication is being accelerated");
			return true;
		} else {
			logger.info("The aplication isn't being accelerated");
			return false;
		}
	}
	
	//Método que devuelve la url real asociada a la aceleracion  
	public static String getUrl(HttpServletRequest request){
		String url = request.getHeader("N38_URL");
		if (url != null){
			logger.info("N38_URL header: " + url);
			return url;
		} else {
			url = request.getRequestURL().toString();
			logger.info("Request header: " + url);
			return url;
		}
	}
}