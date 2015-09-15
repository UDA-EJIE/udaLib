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
package com.ejie.x38.log;

import java.util.Hashtable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;

/**
 * 
 * @author UDA
 *
 */
public interface LoggingAdvice {

	public abstract void preLogging (ProceedingJoinPoint call) throws Throwable;
	
	public abstract void postLogging (ProceedingJoinPoint call, Object ret) throws Throwable;
	
	public void preComponentLogCall(ProceedingJoinPoint call, Hashtable<String, String> table, Logger logger) throws Throwable;

	public void postComponentLogCall(ProceedingJoinPoint call, Object ret, Hashtable<String, String> table, Logger logger) throws Throwable;
}