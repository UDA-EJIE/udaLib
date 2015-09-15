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

import org.springframework.security.core.GrantedAuthority;

/**
 * 
 * @author UDA
 *
 */
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
