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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.ext.XLogger.Level;

import com.ejie.x38.util.StackTraceManager;
import com.ejie.x38.util.TableManager;

/**
 * 
 * @author UDA
 *
 */
public class IncidenceLoggingAdviceImpl implements IncidenceLoggingAdvice{

	private static final Logger logger =  LoggerFactory.getLogger(IncidenceLoggingAdviceImpl.class);
	
	private LoggingManager loggingManager;
	
	public void logIncidence (Object target, Exception exception){
		Hashtable<String, String> table = TableManager.initTable();
		table.put(LogConstants.CRITICALITY, Level.ERROR.toString());
		table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.INCIDENCESUBSYSTEM);
		table.put(LogConstants.ADITIONALINFO, StackTraceManager.getStackTrace(exception));
		table.put(LogConstants.MESSAGE, exception.getMessage());
		loggingManager.logIncidences(target.getClass().getName(), logger, table);
	}

	//Getters & Setters
	public LoggingManager getLoggingManager() {
		return loggingManager;
	}

	public void setLoggingManager(LoggingManager loggingManager) {
		this.loggingManager = loggingManager;
	}
}
