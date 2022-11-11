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
package com.ejie.x38.security;

import java.lang.reflect.InvocationTargetException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.LogoutHandler;
import org.springframework.util.Assert;

/**
 * 
 * @author UDA
 *
 */
public class MyLogoutHandler implements LogoutHandler {
	private boolean invalidateHttpSession;
	private boolean invalidateUserSession;
	private PerimetralSecurityWrapper perimetralSecurityWrapper;
	static Logger logger =  LoggerFactory.getLogger(MyLogoutHandler.class);
	
	public PerimetralSecurityWrapper getPerimetralSecurityWrapper() {
		return perimetralSecurityWrapper;
	}

	public void setPerimetralSecurityWrapper(
			PerimetralSecurityWrapper perimetralSecurityWrapper) {
		this.perimetralSecurityWrapper = perimetralSecurityWrapper;
	}

	@Override
	public void logout(HttpServletRequest request,
			HttpServletResponse response, Authentication authentication) {
		
		HttpSession httpSession = request.getSession(false);
		
		//Clear Spring Security Context
		logger.info("XLNET session is invalid. Proceeding to clean the Security Context Holder.");
		
		if(authentication != null){
			authentication.setAuthenticated(false);
		}
		SecurityContextHolder.clearContext();
		
		if(httpSession != null && (httpSession.getAttribute("SPRING_SECURITY_CONTEXT") != null)){
			httpSession.removeAttribute("SPRING_SECURITY_CONTEXT");
		}
		
		logger.info( "SecurityContextHolder cleared!");
		
		//Destroy XLNET session
		if(this.invalidateUserSession){
			Assert.notNull(request, "HttpServletRequest required");			
			getPerimetralSecurityWrapper().logout(request, response);
			try {
				Object credentials = authentication.getCredentials();
				Object uidSession = credentials.getClass().getMethod("getUidSession").invoke(credentials);
				logger.info("XLNetS {} session destroyed!", (String) uidSession);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
		}

		//Invalidate HTTP session
		if (httpSession != null && this.invalidateHttpSession) {
			
			//Cleaning the User Session of Weblogic
			try{ 
				logger.info("Session " +httpSession.getId()+ " invalidated!");
				httpSession.invalidate();
			} catch (IllegalStateException e) {
				logger.info( "The user session isn't valid, it is not necessary delete it");
			}
		}
	}

	public boolean isInvalidateHttpSession() {
		return invalidateHttpSession;
	}

	public void setInvalidateHttpSession(boolean invalidateHttpSession) {
		this.invalidateHttpSession = invalidateHttpSession;
	}

	public boolean isInvalidateUserSession() {
		return invalidateUserSession;
	}

	public void setInvalidateUserSession(boolean invalidateUserSession) {
		this.perimetralSecurityWrapper.setDestroySessionSecuritySystem(invalidateUserSession);			 
		this.invalidateUserSession = invalidateUserSession;
	}
}