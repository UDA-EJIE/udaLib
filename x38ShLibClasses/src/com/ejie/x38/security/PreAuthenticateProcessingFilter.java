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

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.ejie.x38.control.exception.MethodFailureException;
import com.ejie.x38.log.LogConstants;

/**
 * 
 * @author UDA
 *
 */
public class PreAuthenticateProcessingFilter extends
		AbstractPreAuthenticatedProcessingFilter {

	private static final Logger logger = LoggerFactory
			.getLogger(PreAuthenticateProcessingFilter.class);

	private PerimetralSecurityWrapper perimetralSecurityWrapper;

	/**
	 * Try to authenticate a pre-authenticated user with Spring Security if the
	 * user has not yet been authenticated.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		logger.info("the request is entering in the security system");
		super.doFilter(request, response, chain);
		logger.info("the request is exiting of the security system");
	}
	

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		UserCredentials result = null;
		
		String uidSession = getPerimetralSecurityWrapper().getUserConnectedUidSession(request);
		String userName = getPerimetralSecurityWrapper().getUserConnectedUserName(request);
		String position = getPerimetralSecurityWrapper().getUserPosition(request);
		
		MDC.put(LogConstants.SESSION,uidSession);
		MDC.put(LogConstants.USER,userName);
		MDC.put(LogConstants.POSITION,position);
		
		if (uidSession != null && userName != null && position != null){
			result= new UserCredentials(request, userName, uidSession, position);
			logger.info( "The incoming user's Credentials are loading. The data of its credentials is: [uidSession = "+uidSession+" ] [userName = "+userName+" ] [position = "+position+"]");
		} else {
			StringBuilder exceptionString = new StringBuilder();
			exceptionString.append("There was an  unexpected error in method: \"");
			exceptionString.append(Thread.currentThread().getStackTrace()[1].getMethodName());
			exceptionString.append("\" filter: \"");
			exceptionString.append(this.getFilterName());
			exceptionString.append("\".");
			exceptionString.append(" (The value of uidSession or userName or position is null)");
			throw new MethodFailureException(exceptionString.toString());
		}
		
		return result;
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		String principalUser = null;
		
		HttpSession session = request.getSession(false);
		
		principalUser = this.perimetralSecurityWrapper.getUserConnectedUserName(request);
		
		if(principalUser != null){
			logger.info( "The incoming user is: "+principalUser);
		} else {
			StringBuilder exceptionString = new StringBuilder();
			exceptionString.append("There was an  unexpected error in method: \"");
			exceptionString.append(Thread.currentThread().getStackTrace()[1].getMethodName());
			exceptionString.append("\" filter: \"");
			exceptionString.append(this.getFilterName());
			exceptionString.append("\".");
			throw new MethodFailureException(exceptionString.toString());
		}
		
		if(SecurityContextHolder.getContext().getAuthentication() != null && request.getSession(false) !=null && request.getSession(false).getAttribute("reloadData") !=null && request.getSession(false).getAttribute("reloadData").equals("true")){
			
			//if the user changes then the user session is invalidate  
			if (session.getAttribute("userChange") == null){
				logger.info( "The cache of user's credentials is expired. Proceeds to recharge the user's credentials");
				setInvalidateSessionOnPrincipalChange(false);
			} else {
				logger.info( "The incoming user and the authenticated user are not equal. Proceed to load the new user's credentials");
				session.removeAttribute("userChange");
			}
			
			session.removeAttribute("reloadData");
			
			logger.info("Proceed to reload the user's credentials");
			
			return null;
		} else {
			setInvalidateSessionOnPrincipalChange(true);
		}
		
		return principalUser;
	}

	// Getters & Setters
	public PerimetralSecurityWrapper getPerimetralSecurityWrapper() {
		return perimetralSecurityWrapper;
	}

	public void setPerimetralSecurityWrapper(
			PerimetralSecurityWrapper perimetralSecurityWrapper) {
		this.perimetralSecurityWrapper = perimetralSecurityWrapper;
	}
}