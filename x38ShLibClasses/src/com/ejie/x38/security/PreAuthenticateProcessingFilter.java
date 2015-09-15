/*
* Copyright 2012 E.J.I.E., S.A.
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
import java.util.Vector;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

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

		String valid = getPerimetralSecurityWrapper().validateSession((HttpServletRequest)request, (HttpServletResponse) response);

		if(valid.equals("true")){
			super.doFilter(request, response, chain);
			logger.info("the request is exiting of the security system");
		} else if(valid.equals("false")) {
			chain.doFilter(request, response);
			logger.info("the request is exiting of the security system");
		} else {
			((HttpServletResponse) response).sendRedirect(valid);
			return;
		}
	}
	

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		UserCredentials result = null;
		
		String uidSession = getPerimetralSecurityWrapper().getUserConnectedUidSession(request);
		String userName = getPerimetralSecurityWrapper().getUserConnectedUserName(request);
		String position = getPerimetralSecurityWrapper().getUserPosition(request);
		Vector <String> UserInstances = getPerimetralSecurityWrapper().getUserInstances(request);
		String udaValidateSessionId = getPerimetralSecurityWrapper().getUdaValidateSessionId(request);
		String policy = getPerimetralSecurityWrapper().getPolicy(request);
		boolean certificate = getPerimetralSecurityWrapper().getIsCertificate(request);
		String nif = getPerimetralSecurityWrapper().getNif(request);
		
		result= new UserCredentials(UserInstances, userName, nif, uidSession, position, udaValidateSessionId, policy, certificate);
		logger.info( "The incoming user's Credentials are loading. The data of its credentials is: [uidSession = "+uidSession+" ] [userName = "+userName+" ] [position = "+position+"]");
				
		MDC.put(LogConstants.SESSION,uidSession);
		MDC.put(LogConstants.USER,userName);
		MDC.put(LogConstants.POSITION,position);
		
		return result;
	}

	@Override
	protected Object getPreAuthenticatedPrincipal(HttpServletRequest request) {
		String principalUser = null;
			
		HttpSession session = request.getSession(false);
		
		principalUser = this.perimetralSecurityWrapper.getUserConnectedUserName(request);
		
		logger.info( "The incoming user is: "+principalUser);
		
		if(session != null && session.getAttribute("reloadData") !=null && session.getAttribute("reloadData").equals("true")){
			
			//if the user changes then the user session is invalidate  
			if (session.getAttribute("userChange") == null){
				logger.info("The cache of user's credentials is expired. Proceeds to recharge the user's credentials");
				setInvalidateSessionOnPrincipalChange(false);
			} else {
				logger.info("The incoming user and the authenticated user are not equal. Proceed to load the new user's credentials");
				session.removeAttribute("userChange");
			}
			
			session.removeAttribute("reloadData");
			
			logger.info("Proceed to reload the user's credentials");
						
			//return null;
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