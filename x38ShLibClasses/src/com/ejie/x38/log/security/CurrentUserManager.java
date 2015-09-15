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
package com.ejie.x38.log.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ejie.x38.security.UserCredentials;
import com.ejie.x38.util.StackTraceManager;

/**
 *
 * Proporciona ciertos datos referentes a la seguridad y el entorno al sistema de Logging.
 * 
 * @author UDA
 * 
 */
public class CurrentUserManager{

	private static final long serialVersionUID = 1398165309740472090L;

	private static final Logger logger =  LoggerFactory.getLogger(CurrentUserManager.class);
	
	public static String getCurrentUserN38UidSesion(UserCredentials userCredentials) {
		String uidSesion = " ";
		try{
			if (userCredentials != null) {
				uidSesion = userCredentials.getUidSession();				
			}
		}catch(Exception e){
			logger.error(StackTraceManager.getStackTrace(e));
		}
		return uidSesion;
	}

	public static String getCurrentUsername() {
		String userName = " ";
		try{
			if(SecurityContextHolder.getContext().getAuthentication()!=null){
				userName = SecurityContextHolder.getContext().getAuthentication().getName();
			}	
		}catch(Exception e){
			logger.error(StackTraceManager.getStackTrace(e));
		}
		return userName;
	}
	
	public static String getPosition(UserCredentials userCredentials){
		if (userCredentials!= null){
			return userCredentials.getPosition();
		}else{
			return " ";
		}
	}	
}