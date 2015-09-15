package com.ejie.x38.rss.security;

import javax.servlet.http.HttpServletRequest;

import n38a.exe.N38APISesion;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.w3c.dom.Document;
import org.w3c.dom.Node;

import com.ejie.x38.rss.exception.RssAuthenticationException;

public class RssAuthenticationProvider implements AuthenticationProvider {

	private AuthenticationUserDetailsService<Authentication> myAuthenticatedUserDetailsService;
	
	  @Override
	  public Authentication authenticate(Authentication authentication)
	      throws AuthenticationException {

	    UsernamePasswordAuthenticationToken auth = (UsernamePasswordAuthenticationToken) authentication;
	    String principal = (String) auth.getPrincipal();
	    String credential = (String) auth.getCredentials();

	    RequestAttributes requestAttributes = RequestContextHolder.getRequestAttributes();
	    HttpServletRequest request = ((ServletRequestAttributes)requestAttributes).getRequest();
	    
	    
		if (this.isValidCredentials(principal, credential, request)) {
			UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(
					principal, credential);
			return result;
		}

		throw new BadCredentialsException("Bad Authentication");

	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);

	}
	  
	private boolean isValidCredentials(String user, String password,
			HttpServletRequest httpRequest) {

		Document document = (Document) httpRequest.getSession().getAttribute(
				"Authorization-x");
		if (document != null) {
			return true;
		}

		return isValidUser(user, password, httpRequest);
	}

	private boolean isValidUser(String user, String password,
			HttpServletRequest httpRequest) {
		boolean validUser = false;

		try {

			N38APISesion n38APISession = new N38APISesion(httpRequest);
			Document document = n38APISession.n38APISesionCrearUP(user,
					password);

			this.checkSecuritySesionValid(document);
			
			if (document != null) {
				httpRequest.getSession().setAttribute("Authorization-x", document);
				validUser = true;
			}
			
		} catch (RuntimeException e) {
			e.printStackTrace();
		}
		return validUser;
	}

	private void checkSecuritySesionValid(Node nodo)
			throws RssAuthenticationException {
		RssXLNetsAutenticationHelper.checkSecuritySesionValid(nodo);
	}

	public AuthenticationUserDetailsService<Authentication> getMyAuthenticatedUserDetailsService() {
		return myAuthenticatedUserDetailsService;
	}

	public void setMyAuthenticatedUserDetailsService(
			AuthenticationUserDetailsService<Authentication> myAuthenticatedUserDetailsService) {
		this.myAuthenticatedUserDetailsService = myAuthenticatedUserDetailsService;
	}
}
