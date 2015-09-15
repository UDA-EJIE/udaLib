package com.ejie.x38.log;

import java.util.Hashtable;

import org.apache.log4j.Logger;

public interface LoggingManager {
	
	/**
	 * Log. Se utiliza automáticamente por el aspecto de log.
	 * 
	 * @param args Argumentos a Loguear.
	 */
	public void autoLog(Hashtable<String, String> args, String fqnClassName, Logger logger);
	
	public void logIncidences(String fqnClassName, Logger logger, Hashtable<String, String> table);
}