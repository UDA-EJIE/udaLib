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

import java.util.Arrays;
import java.util.Hashtable;

import org.aspectj.lang.ProceedingJoinPoint;
import org.slf4j.Logger;

/**
 * 
 * @author UDA
 *
 */
public abstract class LoggingAdviceImpl implements LoggingAdvice{

	private LoggingManager loggingManager;
	
	public abstract void preLogging (ProceedingJoinPoint call) throws Throwable;
	public abstract void postLogging (ProceedingJoinPoint call, Object ret) throws Throwable;
	
	@Override
	public void preComponentLogCall(ProceedingJoinPoint call, Hashtable<String, String> table, Logger logger) throws Throwable {			
		StringBuilder message = new StringBuilder(call.getTarget().getClass().getSimpleName());
		message.append(call.toShortString());
		String aditionalInfo = Arrays.toString(call.getArgs());
		
		table.put(LogConstants.MESSAGE, message.toString());
		table.put(LogConstants.ADITIONALINFO, aditionalInfo);		
		loggingManager.autoLog(table, this.getClass().getName(), logger);
	}

	@Override
	public void postComponentLogCall(ProceedingJoinPoint call, Object ret, Hashtable<String, String> table, Logger logger) throws Throwable {	
		StringBuilder message = new StringBuilder(call.getTarget().getClass().getSimpleName());
		String aditionalInfo = "";
		
		message.append(call.toShortString());
		message.append(" - finished");
		
		if (logger.isTraceEnabled() && ret != null){
			aditionalInfo = ret+"";
		}
		
		table.put(LogConstants.ADITIONALINFO, aditionalInfo);			
		table.put(LogConstants.MESSAGE, message.toString());
		
		loggingManager.autoLog(table, this.getClass().getName(), logger);
	}
	
	//Getters & Setters
	public LoggingManager getLoggingManager() {
		return loggingManager;
	}

	public void setLoggingManager(LoggingManager loggingManager) {
		this.loggingManager = loggingManager;
	}	
}