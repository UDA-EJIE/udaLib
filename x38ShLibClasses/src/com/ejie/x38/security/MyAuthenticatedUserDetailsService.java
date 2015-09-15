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

import java.util.Vector;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

/**
 * 
 * @author UDA
 *
 */
public class MyAuthenticatedUserDetailsService implements
		AuthenticationUserDetailsService<Authentication> {

	private static final Logger logger = LoggerFactory
			.getLogger(MyAuthenticatedUserDetailsService.class);
	private PerimetralSecurityWrapper perimetralSecurityWrapper;

	@Override
	public UserDetails loadUserDetails(Authentication token)
			throws UsernameNotFoundException {
		Assert.notNull(token.getCredentials());
		logger.trace("User's token is: "+token.toString());
		logger.trace("User's credentials are "+token.getCredentials().toString());

		UserCredentials userCredentials = (UserCredentials) token
				.getCredentials();
		UserDetails userDetails = null;
		if (userCredentials != null) {
			Vector<String> vectorProfilesXLNET = userCredentials.getUserProfiles();
			XlnetGrantedAuthority[] profiles = null;
			if (vectorProfilesXLNET != null) {
				profiles = new XlnetGrantedAuthority[vectorProfilesXLNET.size()];
				for (int contadorAuthXLNET = 0; contadorAuthXLNET < vectorProfilesXLNET
						.size(); contadorAuthXLNET++) {
					String authority = "ROLE_"
							+ vectorProfilesXLNET.get(contadorAuthXLNET);
					profiles[contadorAuthXLNET] = new XlnetGrantedAuthority(
							authority);
				}
			}
			userDetails = createuserDetails(token, profiles);
		}
		logger.trace("UserDetails is: "+userDetails.toString());
		
		return userDetails;
	}

	protected MyUserDetails createuserDetails(Authentication token,
			GrantedAuthority[] profiles) {
		return new MyUserDetails(token.getName(), // UserName
				"N/A", // Password
				true, // Enabled
				true, // AccountNonExpired
				true, // CredentialsNonExpired
				true, // Account Not Locked
				profiles); // Profiles.
	}
	
	//Getters & Setters
	public PerimetralSecurityWrapper getPerimetralSecurityWrapper() {
		return perimetralSecurityWrapper;
	}

	public void setPerimetralSecurityWrapper(
			PerimetralSecurityWrapper perimetralSecurityWrapper) {
		this.perimetralSecurityWrapper = perimetralSecurityWrapper;
	}
}