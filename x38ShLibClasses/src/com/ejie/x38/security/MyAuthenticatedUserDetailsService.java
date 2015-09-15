package com.ejie.x38.security;

import java.util.Vector;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.AuthenticationUserDetailsService;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.util.Assert;

public class MyAuthenticatedUserDetailsService implements
		AuthenticationUserDetailsService {

	private static final Logger logger = Logger
			.getLogger(MyAuthenticatedUserDetailsService.class);
	private PerimetralSecurityWrapper perimetralSecurityWrapper;

	@Override
	public UserDetails loadUserDetails(Authentication token)
			throws UsernameNotFoundException {
		Assert.notNull(token.getCredentials());
		logger.log(Level.TRACE, "User's token is: "+token.toString());
		logger.log(Level.TRACE, "User's credentials are "+token.getCredentials().toString());

		UserCredentials userCredentials = (UserCredentials) token
				.getCredentials();
		UserDetails userDetails = null;
		if (userCredentials != null) {
			Vector<String> vectorProfilesXLNET = getPerimetralSecurityWrapper()
					.getUserInstances(userCredentials.getHttpRequest());
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
		logger.log(Level.TRACE, "UserDetails is: "+userDetails.toString());
		
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