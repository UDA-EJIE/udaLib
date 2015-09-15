package com.ejie.x38.log.security;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;

import com.ejie.x38.security.UserCredentials;
import com.ejie.x38.util.StackTraceManager;

/**
 * 
 * @author UDA
 *
 * Proporciona ciertos datos referentes a la seguridad y el entorno al sistema de Logging.
 */
public class CurrentUserManager{

	private static final long serialVersionUID = 1398165309740472090L;

	private static final Logger logger = Logger.getLogger(CurrentUserManager.class);
	
	public static String getCurrentUserN38UidSesion(UserCredentials userCredentials) {
		String uidSesion = new String(" ");
		try{
			if (userCredentials != null)
			{
				uidSesion = userCredentials.getUidSession();				
			} else {
				uidSesion = " ";
			}
			
		}catch(Exception e){
			logger.log(Level.ERROR, StackTraceManager.getStackTrace(e));
			return " ";
		}
		return uidSesion;
	}

	public static String getCurrentUsername() {
		String userName = new String(" ");
		try{
			if(SecurityContextHolder.getContext().getAuthentication()!=null){
				userName = SecurityContextHolder.getContext().getAuthentication().getName();
			}	
		}catch(Exception e){
			logger.log(Level.ERROR,StackTraceManager.getStackTrace(e));
		}
		return userName;
	}
	
	public static String getUserIpAddress(UserCredentials userCredentials){
		if (userCredentials!= null){
			return userCredentials.getHttpRequest().getRemoteAddr();
		}else{
			return "127.0.0.1";
		}
	}
	
	public static String getPosition(UserCredentials userCredentials){
		if (userCredentials!= null){
			return userCredentials.getPosition();
		}else{
			return " ";
		}
	}	
}