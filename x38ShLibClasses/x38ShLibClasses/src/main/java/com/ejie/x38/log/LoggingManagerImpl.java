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

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Hashtable;

import org.slf4j.Logger;

/**
 * 
 * @author UDA
 *
 */
public class LoggingManagerImpl implements LoggingManager{
	
	public void autoLog(Hashtable<String, String> table, String fqnClassName, Logger logger){
		
		HashMap<String, String> info = new HashMap<String, String>(2);
		
		info.put(LogConstants.INTERFUNCTIONALSUBSYSTEM, table.get(LogConstants.FUNCTIONALSUBSYSTEM));
		info.put(LogConstants.INTERADITIONALINFO, table.get(LogConstants.ADITIONALINFO));
		
		try {
			Class<?> logClass = logger.getClass();
	        Method meth = logClass.getMethod(table.get(LogConstants.CRITICALITY).toLowerCase(), String.class, Object.class);
	        meth.invoke(logger, table.get(LogConstants.MESSAGE), info);
	    } catch (Exception e) {
			//Problems with reflection method 'autoLog' 
			logger.error("Problems with reflection method 'autoLog'.", e);
		} 
	}
	
	public void logIncidences(String fqnClassName, Logger logger, Hashtable<String, String> table){
		
		HashMap<String, String> info = new HashMap<String, String>(2);
		
		info.put(LogConstants.INTERFUNCTIONALSUBSYSTEM, table.get(LogConstants.FUNCTIONALSUBSYSTEM));
		info.put(LogConstants.INTERADITIONALINFO, table.get(LogConstants.ADITIONALINFO));
		
		try {
			Class<?> logClass = logger.getClass();
	        Method meth = logClass.getMethod(table.get(LogConstants.CRITICALITY).toLowerCase(), String.class, Object.class);
	        meth.invoke(logger, table.get(LogConstants.MESSAGE), info);
	    }
		catch (Throwable e) {
			//Problems with reflection method logIncidences 
			logger.error("Problems with reflection method 'logIncidences'.", e);
		}
	}
}