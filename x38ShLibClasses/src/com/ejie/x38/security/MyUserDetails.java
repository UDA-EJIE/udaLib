package com.ejie.x38.security;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

public class MyUserDetails implements UserDetails {

	private static final long serialVersionUID = 1L;
	private String password;
	private String username;
	private Collection<GrantedAuthority> authorities;
	private boolean accountNonExpired;
	private boolean accountNonLocked;
	private boolean credentialsNonExpired;
	private boolean enabled;
	private GrantedAuthority[] profiles;

	public MyUserDetails(String username, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired,
			boolean accountNonLocked, GrantedAuthority[] profiles)
			throws IllegalArgumentException {
		if (((username == null) || "".equals(username)) || (password == null)) {
			throw new IllegalArgumentException(
					"Cannot pass null or empty values to constructor");
		}

		this.username = username;
		this.password = password;
		this.enabled = enabled;
		this.accountNonExpired = accountNonExpired;
		this.credentialsNonExpired = credentialsNonExpired;
		this.accountNonLocked = accountNonLocked;
		setProfiles(profiles);
		setAuthorities(profiles);
	}

	public GrantedAuthority[] getProfiles() {
		return profiles;
	}

	protected void setProfiles(GrantedAuthority[] perfiles) {
		Assert.notNull(perfiles, "Cannot pass a null GrantedAuthority array");
		// Ensure array iteration order is predictable (as per UserDetails.getAuthorities() contract and SEC-xxx)
		List<GrantedAuthority> notNullAuthorities = new ArrayList<GrantedAuthority>();
		for (int i = 0; i < perfiles.length; i++) {
			Assert.notNull(
					perfiles[i],
					"Granted perfiles element "
							+ i
							+ " is null - GrantedAuthority[] cannot contain any null elements");
			notNullAuthorities.add(perfiles[i]);
		}
		this.profiles = (GrantedAuthority[]) notNullAuthorities
				.toArray(new GrantedAuthority[notNullAuthorities.size()]);
	}

	@Override
	public Collection<GrantedAuthority> getAuthorities() {
		return authorities;
	}

	/**
	 * Establishes user permissions, checking that none of them will be null.
	 */
	protected void setAuthorities(GrantedAuthority[] authorities) {
		Assert.notNull(authorities, "Cannot pass a null GrantedAuthority array");

		// Ensure array iteration order is predictable (as per UserDetails.getAuthorities() contract and SEC-xxx)
		List<GrantedAuthority> sorter = new ArrayList<GrantedAuthority>();
		for (int i = 0; i < authorities.length; i++) {
			Assert.notNull(
					authorities[i],
					"Granted authority element "
							+ i
							+ " is null - GrantedAuthority[] cannot contain any null elements");
			sorter.add(authorities[i]);
		}

		this.authorities = sorter;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return this.accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	@Override
	public boolean isEnabled() {
		return enabled;
	}

	public boolean equals(Object rhs) {
		if (!(rhs instanceof MyUserDetails) || (rhs == null)) {
			return false;
		}

		MyUserDetails user = (MyUserDetails) rhs;

		// We rely on constructor to guarantee any User has non-null and >0 authorities
		if (user.getProfiles().length != this.getProfiles().length) {
			return false;
		}
		for (int i = 0; i < this.getProfiles().length; i++) {
			if (!this.getProfiles()[i].equals(user.getProfiles()[i])) {
				return false;
			}
		}

		// We rely on constructor to guarantee non-null username and password
		return (this.getPassword().equals(user.getPassword())
				&& this.getUsername().equals(user.getUsername())
				&& (this.isAccountNonExpired() == user.isAccountNonExpired())
				&& (this.isAccountNonLocked() == user.isAccountNonLocked())
				&& (this.isCredentialsNonExpired() == user
						.isCredentialsNonExpired()) && (this.isEnabled() == user
				.isEnabled()));
	}

	public String toString() {
		StringBuffer sb = new StringBuffer();
		sb.append(super.toString()).append(": ");
		sb.append("Username: ").append(this.username).append("; ");
		sb.append("Password: [PROTECTED]; ");
		sb.append("Enabled: ").append(this.enabled).append("; ");
		sb.append("AccountNonExpired: ").append(this.accountNonExpired)
				.append("; ");
		sb.append("credentialsNonExpired: ").append(this.credentialsNonExpired)
				.append("; ");
		sb.append("AccountNonLocked: ").append(this.accountNonLocked)
				.append("; ");

		if (this.getProfiles() != null) {
			sb.append("Granted Profiles: ");

			for (int i = 0; i < this.getProfiles().length; i++) {
				if (i > 0) {
					sb.append(", ");
				}
				sb.append(this.getProfiles()[i].toString());
			}
		} else {
			sb.append("Not granted any profiles");
		}
		sb.append("; ");

		return sb.toString();
	}
}