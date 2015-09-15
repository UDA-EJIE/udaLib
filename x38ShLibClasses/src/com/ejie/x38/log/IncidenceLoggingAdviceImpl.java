package com.ejie.x38.log;

import java.util.Hashtable;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

import com.ejie.x38.util.StackTraceManager;
import com.ejie.x38.util.TableManager;

public class IncidenceLoggingAdviceImpl implements IncidenceLoggingAdvice{

	private static final Logger logger = Logger.getLogger(IncidenceLoggingAdviceImpl.class);
	
	private LoggingManager loggingManager;
	
	public void logIncidence (Object target, Exception exception){
		Hashtable<String, String> table = TableManager.initTable();
		table.put(LogConstants.CRITICALITY, Level.ERROR.toString());
		table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.INCIDENCESUBSYSTEM);
		table.put(LogConstants.MESSAGE, StackTraceManager.getStackTrace(exception));
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
