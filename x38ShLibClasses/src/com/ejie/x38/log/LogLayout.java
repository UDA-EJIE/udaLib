package com.ejie.x38.log;

import java.util.Hashtable;

import org.apache.log4j.Layout;
import org.apache.log4j.Level;
import org.apache.log4j.NDC;
import org.apache.log4j.spi.LoggingEvent;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ejie.x38.log.security.CurrentUserManager;
import com.ejie.x38.security.UserCredentials;
import com.ejie.x38.util.DateTimeManager;
import com.ejie.x38.util.StaticsContainer;
import com.ejie.x38.util.TableManager;
import com.ejie.x38.util.ThreadStorageManager;

public class LogLayout extends Layout{
	
	@SuppressWarnings("unchecked")
	public String format(LoggingEvent event) {
		UserCredentials userCredentials = null;
		Hashtable<String, String> table;
		try{
			try{
				userCredentials = (UserCredentials) SecurityContextHolder.getContext().getAuthentication().getCredentials();
			}catch(Exception e){
				//Nothing to do
			}
			if(event.getNDC()!= null && event.getNDC().equals("UDA")){
				table = (Hashtable<String, String>) (event.getMessage()!=null ? (Hashtable<String, String>) event.getMessage():new Hashtable<String,String>());			
			}else{
				table = TableManager.initTable();
				table.put(LogConstants.MESSAGE, event.getMessage()!=null ? event.getMessage()+"":"");
				table.put(LogConstants.CRITICALITY, event.getLevel()!=null ? event.getLevel()+"":"");
				table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.TRACESUBSYSTEM);
			}
			if(event.getLoggerName().startsWith("com.ejie.") && event.getLoggerName().contains(".service.")){
				table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.LOGICSUBSYSTEM);
			}
			else if(event.getLoggerName().startsWith("com.ejie.") && event.getLoggerName().contains(".control.")){
				table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.WEBSUBSYSTEM);
			}
			else if(event.getLoggerName().startsWith("com.ejie.") && event.getLoggerName().contains(".dao.")){
				table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.DATASUBSYSTEM);
			}
			else if ((table.get(LogConstants.FUNCTIONALSUBSYSTEM)== null) || (table.get(LogConstants.FUNCTIONALSUBSYSTEM).equals(""))){
				table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.TRACESUBSYSTEM);
			}
			table.put(LogConstants.DATETIME, DateTimeManager.getDateTime());
			table.put(LogConstants.APPCODE, StaticsContainer.webAppName!=null ? StaticsContainer.webAppName:"");
			table.put(LogConstants.THREAD, ThreadStorageManager.getCurrentThreadId()+"");
			table.put(LogConstants.LOGGERCLASS, event.getLoggerName());
			table.put(LogConstants.SERVERINSTANCE, StaticsContainer.weblogicInstance!=null ? StaticsContainer.weblogicInstance:"");
		}catch(Exception e){
			table = TableManager.initTable();
			if (event!=null){
				table.put(LogConstants.MESSAGE, event.getMessage()!=null ? event.getMessage()+"":"");
				table.put(LogConstants.CRITICALITY, event.getLevel()!=null ? event.getLevel()+"":"");
				table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.TRACESUBSYSTEM);
			}else{
				table.put(LogConstants.MESSAGE, "Unknown Error");
				table.put(LogConstants.CRITICALITY, Level.FATAL.toString());
				table.put(LogConstants.FUNCTIONALSUBSYSTEM, LogConstants.TRACESUBSYSTEM);
			}
		}finally{			
			NDC.pop();
		}		
		table.put(LogConstants.USER, CurrentUserManager.getCurrentUsername());
		table.put(LogConstants.SESSION, CurrentUserManager.getCurrentUserN38UidSesion(userCredentials));
		table.put(LogConstants.IPADDRESS, CurrentUserManager.getUserIpAddress(userCredentials));
		table.put(LogConstants.POSITION, CurrentUserManager.getPosition(userCredentials));
		
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
	}

		@Override
		public void activateOptions() {
		}

		@Override
		public boolean ignoresThrowable() {
			return false;
		} 
}