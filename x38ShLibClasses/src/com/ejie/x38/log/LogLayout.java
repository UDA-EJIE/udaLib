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

import java.util.HashMap;
import java.util.Hashtable;

import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.classic.spi.ThrowableProxy;
import ch.qos.logback.core.LayoutBase;
import ch.qos.logback.core.LogbackException;

import com.ejie.x38.log.security.CurrentUserManager;
import com.ejie.x38.security.Credentials;
import com.ejie.x38.util.DateTimeManager;
import com.ejie.x38.util.StackTraceManager;
import com.ejie.x38.util.TableManager;
import com.ejie.x38.util.ThreadStorageManager;

/**
 * 
 * @author UDA
 *
 */
public class LogLayout extends LayoutBase<ILoggingEvent>{

	private String appCode = null;
	private String instance = null;
	
	public void setAppCode(String appCode) {
	    this.appCode = appCode;
	  }

	public void setInstance(String instance) {
	    this.instance = instance;
	  }
	
	@SuppressWarnings("unchecked")
	public String doLayout(ILoggingEvent event) throws LogbackException{
		Credentials Credentials = null;
		Hashtable<String, String> table;
		Object[] argArray = event.getArgumentArray();
		HashMap<String, String> argUdaObjec = new HashMap<String, String>();
		
		try{
			
			//Get internal log info
			if(event.getArgumentArray() != null){
				argArray = event.getArgumentArray();
				if (argArray[0].getClass() == java.util.HashMap.class ){
					argUdaObjec = (HashMap<String, String>) argArray[0];
				}
			}
			
			//Initializing data container table
			table = TableManager.initTable();
			
			//General data collection (data not dependent)
			table.put(LogConstants.DATETIME, DateTimeManager.getDateTime());
			table.put(LogConstants.APPCODE, appCode!=null ? appCode:"");
			table.put(LogConstants.THREAD, ThreadStorageManager.getCurrentThreadId()+"");
			table.put(LogConstants.LOGGERCLASS, event.getLoggerName());
			table.put(LogConstants.SERVERINSTANCE, instance!=null ? instance:"");
			table.put(LogConstants.CRITICALITY, event.getLevel()!=null ? event.getLevel()+"":"");
			table.put(LogConstants.MESSAGE, event.getFormattedMessage()!=null ? event.getFormattedMessage()+"":"");
			
			//Assigning info of subsystema functional
			if(argUdaObjec.get(LogConstants.INTERFUNCTIONALSUBSYSTEM) == null){
				if (MDC.get(LogConstants.FUNCTIONALSUBSYSTEM) == null){
					if(event.getLoggerName().startsWith("com.ejie.") && event.getLoggerName().contains(".service.")){
						table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.LOGICSUBSYSTEM);
					} else if(event.getLoggerName().startsWith("com.ejie.") && event.getLoggerName().contains(".control.")){
						table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.WEBSUBSYSTEM);
					} else if(event.getLoggerName().startsWith("com.ejie.") && event.getLoggerName().contains(".dao.")){
						table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.DATASUBSYSTEM);
					} else {
						table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.TRACESUBSYSTEM);
					}
				} else {
					table.put(LogConstants.FUNCTIONALSUBSYSTEM, MDC.get(LogConstants.FUNCTIONALSUBSYSTEM));
				}
			} else {
				table.put(LogConstants.FUNCTIONALSUBSYSTEM, argUdaObjec.get(LogConstants.INTERFUNCTIONALSUBSYSTEM));
			}
			
			//Http access-dependent data
			if((MDC.get(LogConstants.NOINTERNALACCES) != null)&&((MDC.get(LogConstants.NOINTERNALACCES).equals(LogConstants.ACCESSTYPEHTTP))||(MDC.get(LogConstants.NOINTERNALACCES).equals(LogConstants.ACCESSTYPEEJB)))){
					
				if(MDC.get(LogConstants.USER) != null) {
					table.put(LogConstants.USER, MDC.get(LogConstants.USER));
					table.put(LogConstants.SESSION, MDC.get(LogConstants.SESSION));
					table.put(LogConstants.POSITION, MDC.get(LogConstants.POSITION));
				} else {
					//Get the data of the user credentials.
					try{
						Credentials = (Credentials) SecurityContextHolder.getContext().getAuthentication().getCredentials();
					} catch(Exception e) {
						if (!(e instanceof java.lang.NullPointerException)){
							throw new LogbackException("System error logs. Error accessing the security context.",e);
						}
					}
					
					if (Credentials != null){
						//system log of the application with security context
						table.put(LogConstants.USER, CurrentUserManager.getCurrentUsername());
						table.put(LogConstants.SESSION, CurrentUserManager.getCurrentUserN38UidSesion(Credentials));
						table.put(LogConstants.POSITION, CurrentUserManager.getPosition(Credentials));
						
					} else {
						//System log of the application without security context
						table.put(LogConstants.USER, "N/A");
						table.put(LogConstants.SESSION, "N/A");
						table.put(LogConstants.POSITION, "N/A");
					}
				}
				
				//IpAddres
				table.put(LogConstants.IPADDRESS, MDC.get("IPClient"));
			}
			
			//Additional info
			if (argUdaObjec.get(LogConstants.INTERADITIONALINFO) == null){
				if (MDC.get(LogConstants.ADITIONALINFO) != null){
					table.put(LogConstants.ADITIONALINFO,MDC.get(LogConstants.ADITIONALINFO));
				} else {
					if (event.getThrowableProxy() != null) {
						ThrowableProxy throwableProxy = (ThrowableProxy) event.getThrowableProxy();
						table.put(LogConstants.ADITIONALINFO,StackTraceManager.getStackTrace(throwableProxy.getThrowable()));
					} else if (argArray != null && argArray[0].getClass() == String.class){
						table.put(LogConstants.ADITIONALINFO, (String)argArray[0]);
					}
				}
			} else {
				table.put(LogConstants.ADITIONALINFO,argUdaObjec.get(LogConstants.INTERADITIONALINFO));
			}
			
			//Generates the output message
			StringBuffer sbuf = new StringBuffer(1023);
			sbuf.append(LogConstants.INITSEPARATOR);
			int i = 0;
			for(String param:LogConstants.parameters){
				sbuf.append(table.get(param)!=null?table.get(param):"");
				if (++i<LogConstants.parameters.length){
					sbuf.append(LogConstants.FIELDSEPARATOR);
				}
			}
			sbuf.append(LogConstants.ENDSEPARATOR);
			sbuf.append("\r\n");
			return sbuf.toString();
				
		} catch (LogbackException lbe){
			throw lbe;
		} catch (Exception e) {
			throw new LogbackException("System error logs. Error creating the log trace.",e);
		}
	}
}