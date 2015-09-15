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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.Ordered;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.preauth.PreAuthenticatedAuthenticationToken;
import org.springframework.util.Assert;

/**
 * 
 * @author UDA
 *
 */
public class XlnetAuthenticationProvider implements AuthenticationProvider,
		Ordered, InitializingBean {
	private static final Logger logger = LoggerFactory
			.getLogger(XlnetAuthenticationProvider.class);

	private AuthenticationUserDetailsService myAuthenticatedUserDetailsService;
	private boolean throwExceptionWhenTokenRejected = false;
	private int order = -1; // default: same as non-ordered

	/**
	 * Authenticate the given PreAuthenticatedAuthenticationToken.
	 * If the principal contained in the authentication object is null, the
	 * request will be ignored to allow other providers to authenticate it.
	 */
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		if (authentication != null) {
			logger.debug("authentication: "+ authentication.toString());
		} else {
			logger.debug("authentication is NULL!");
		}

		if (!supports(authentication.getClass())) {
			logger.trace("Authentication class [" + authentication.getClass() + "] isn't supported");
			return null;
		}
		logger.trace("Authentication class [" + authentication.getClass()+ "] is supported.");

		if (authentication.getPrincipal() == null) {
			logger.trace("No pre-authenticated principal found in request.");
			if (throwExceptionWhenTokenRejected) {
				throw new BadCredentialsException(
						"No pre-authenticated principal found in request.");
			}
			return null;
		}

		if (authentication.getCredentials() == null) {
			logger.trace("No pre-authenticated credentials found in request.");

			if (throwExceptionWhenTokenRejected) {
				throw new BadCredentialsException(
						"No pre-authenticated credentials found in request.");
			}
			return null;
		}

		UserDetails userDetails = myAuthenticatedUserDetailsService
				.loadUserDetails(authentication);

		PreAuthenticatedAuthenticationToken result = new PreAuthenticatedAuthenticationToken(
				authentication.getPrincipal(), authentication.getCredentials(),
				userDetails.getAuthorities());

		result.setDetails(authentication.getDetails());

		return result;
	}

	/**
	 * Indicate that this provider only supports
	 * PreAuthenticatedAuthenticationToken (sub)classes.
	 * 
	 */
	@SuppressWarnings("rawtypes")
	public boolean supports(Class authentication) {
		return PreAuthenticatedAuthenticationToken.class
				.isAssignableFrom(authentication);
	}

	public int getOrder() {
		return order;
	}

	public void setOrder(int i) {
		order = i;
	}

	@Override
	public void afterPropertiesSet() throws Exception {
		Assert.notNull(myAuthenticatedUserDetailsService,
				"An AuthenticationUserDetailsService must be set");
	}

	/**
	 * If true, causes the provider to throw a BadCredentialsException if the
	 * presented authentication request is invalid (contains a null principal or
	 * credentials). Otherwise it will just return null. Defaults to false.
	 */
	public void setThrowExceptionWhenTokenRejected(
			boolean throwExceptionWhenTokenRejected) {
		this.throwExceptionWhenTokenRejected = throwExceptionWhenTokenRejected;
	}

	/**
	 * Set the AuthenticatedUserDetailsServices to be used.
	 * 
	 */
	public void setMyAuthenticatedUserDetailsService(
			AuthenticationUserDetailsService myAuthenticatedUserDetailsService) {
		this.myAuthenticatedUserDetailsService = myAuthenticatedUserDetailsService;
	}
}