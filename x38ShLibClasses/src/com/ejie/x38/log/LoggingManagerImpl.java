package com.ejie.x38.log;

import java.util.Hashtable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.NDC;
import org.apache.log4j.spi.LoggingEvent;

public class LoggingManagerImpl implements LoggingManager {
	
	public void autoLog(Hashtable<String, String> table, String fqnClassName, Logger logger){
		NDC.push("UDA");
		LoggingEvent loggingEvent = new LoggingEvent(fqnClassName, logger, System.currentTimeMillis(), Level.toLevel(table.get(LogConstants.CRITICALITY)), table, null);		
		logger.callAppenders(loggingEvent);
	}
	
	public void logIncidences(String fqnClassName, Logger logger, Hashtable<String, String> table){
		NDC.push("UDA");
		LoggingEvent loggingEvent = new LoggingEvent(fqnClassName, logger, System.currentTimeMillis(), Level.toLevel(table.get(LogConstants.CRITICALITY)), table, null);		
		logger.callAppenders(loggingEvent);
	}
}