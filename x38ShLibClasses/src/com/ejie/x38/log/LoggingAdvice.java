package com.ejie.x38.log;

import java.util.Hashtable;

import org.aspectj.lang.ProceedingJoinPoint;

public interface LoggingAdvice {

	public abstract void preLogging (ProceedingJoinPoint call) throws Throwable;
	
	public abstract void postLogging (ProceedingJoinPoint call, Object ret) throws Throwable;
	
	public void preComponentLogCall(ProceedingJoinPoint call, Hashtable<String, String> table) throws Throwable;

	public void postComponentLogCall(ProceedingJoinPoint call, Object ret, Hashtable<String, String> table) throws Throwable;
}