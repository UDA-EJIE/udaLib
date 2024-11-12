package com.ejie.x38.rss.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Provider de seguridad encargado de autenticar al usuario que quiere acceder
 * al contenido RSS.
 * 
 * @author UDA
 *
 */
public class RssAuthenticationProvider implements AuthenticationProvider {

	private AuthenticationUserDetailsService<Authentication> myAuthenticatedUserDetailsService;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		UserDetails userDetails = myAuthenticatedUserDetailsService.loadUserDetails(authentication);

		UsernamePasswordAuthenticationToken result = new UsernamePasswordAuthenticationToken(authentication.getPrincipal(), authentication.getCredentials(), userDetails.getAuthorities());

		result.setDetails(authentication.getDetails());

		return result;

	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

	/**
	 * Getter de la propiedad myAuthenticatedUserDetailsService.
	 * 
	 * @return Devuelve el service encargado de obtener el objeto de
	 *         autenticación correspondiente al usuario que se debe autenticar.
	 */
	public AuthenticationUserDetailsService<Authentication> getMyAuthenticatedUserDetailsService() {
		return myAuthenticatedUserDetailsService;
	}

	/**
	 * Setter de la propiedad myAuthenticatedUserDetailsService.
	 * 
	 * @param myAuthenticatedUserDetailsService
	 *            Service encargado de obtener el objeto de autenticación
	 *            correspondiente al usuario que se debe autenticar.
	 */
	public void setMyAuthenticatedUserDetailsService(AuthenticationUserDetailsService<Authentication> myAuthenticatedUserDetailsService) {
		this.myAuthenticatedUserDetailsService = myAuthenticatedUserDetailsService;
	}
}
