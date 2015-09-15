package com.ejie.x38.log;

import java.util.Arrays;
import java.util.Hashtable;

import org.apache.log4j.Logger;
import org.aspectj.lang.ProceedingJoinPoint;

public abstract class LoggingAdviceImpl implements LoggingAdvice{

	private static Logger logger = Logger.getLogger("com.ejie.x38.log.LoggingAdviceImpl");
	
	private LoggingManager loggingManager;
	
	public abstract void preLogging (ProceedingJoinPoint call) throws Throwable;
	public abstract void postLogging (ProceedingJoinPoint call, Object ret) throws Throwable;
	
	@Override
	public void preComponentLogCall(ProceedingJoinPoint call, Hashtable<String, String> table) throws Throwable {			
		String message = call.getTarget().getClass().getSimpleName();
		message = message + call.toShortString();
		String aditionalInfo = Arrays.toString(call.getArgs());
		
		table.put(LogConstants.MESSAGE, message);
		table.put(LogConstants.ADITIONALINFO, aditionalInfo);		
		loggingManager.autoLog(table, this.getClass().getName(), logger);
	}

	@Override
	public void postComponentLogCall(ProceedingJoinPoint call, Object ret, Hashtable<String, String> table) throws Throwable {	
		String message = call.getTarget().getClass().getSimpleName();
		message = message + call.toShortString();
		String aditionalInfo = ret!=null ? ret+"":"";
		
		table.put(LogConstants.MESSAGE, message);
		table.put(LogConstants.ADITIONALINFO, aditionalInfo);
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