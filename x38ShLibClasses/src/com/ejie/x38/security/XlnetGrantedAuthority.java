package com.ejie.x38.security;

import org.springframework.security.core.GrantedAuthority;

public class XlnetGrantedAuthority implements GrantedAuthority {

	private static final long serialVersionUID = 1L;
	private String authority;

	public XlnetGrantedAuthority(String authority) {
		super();
		this.authority = authority;
	}

	@Override
	public String getAuthority() {
		return authority;
	}

	public int compareTo(Object paramT) {
		// final int BEFORE = -1;
		final int EQUAL = 0;
		final int AFTER = 1;

		if (paramT.getClass().isInstance("java.lang.String")) {
			String sParamT = (String) paramT;
			if (sParamT.equals(this.authority))
				return EQUAL;
		}
		return AFTER;
	}

	@Override
	public boolean equals(Object paramT) {
		final boolean EQUAL = true;
		final boolean NOTEQUAL = false;

		if (paramT.toString().equals(this.authority))
			return EQUAL;
		else
			return NOTEQUAL;
	}

	public String toString() {
		return authority;
	}
}
