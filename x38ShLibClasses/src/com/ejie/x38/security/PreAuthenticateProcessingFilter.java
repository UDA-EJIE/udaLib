package com.ejie.x38.security;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.preauth.AbstractPreAuthenticatedProcessingFilter;

import com.ejie.x38.control.exception.MethodFailureException;

public class PreAuthenticateProcessingFilter extends
		AbstractPreAuthenticatedProcessingFilter {

	private static final Logger logger = Logger
			.getLogger(PreAuthenticateProcessingFilter.class);

	private PerimetralSecurityWrapper perimetralSecurityWrapper;

	/**
	 * Try to authenticate a pre-authenticated user with Spring Security if the
	 * user has not yet been authenticated.
	 */
	@Override
	public void doFilter(ServletRequest request, ServletResponse response,
			FilterChain chain) throws IOException, ServletException {
		logger.trace("Before AbstractPreAuthenticatedProcessingFilter.doFilter");
		super.doFilter(request, response, chain);
		logger.trace("After AbstractPreAuthenticatedProcessingFilter.doFilter");
	}
	

	@Override
	protected Object getPreAuthenticatedCredentials(HttpServletRequest request) {
		UserCredentials result = null;
		
		String uidSession = getPerimetralSecurityWrapper().getUserConnectedUidSession(request);
		String userName = getPerimetralSecurityWrapper().getUserConnectedUserName(request);
		String position = getPerimetralSecurityWrapper().getUserPosition(request);
		
		if (uidSession != null && userName != null && position != null){
			result= new UserCredentials(request, userName, uidSession, position);
			logger.log(Level.INFO, "PreAuthenticated User Credentials are: [uidSession = "+uidSession+" ] [userName = "+userName+" ] [position = "+position+"]");
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
			logger.log(Level.INFO, "PreAuthenticated Principal is: "+principalUser);
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
				setInvalidateSessionOnPrincipalChange(false);
			} else {
				session.removeAttribute("userChange");
			}
			
			session.removeAttribute("reloadData");
			
			logger.log(Level.INFO, "Proceed to reload the user's credentials");
			
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