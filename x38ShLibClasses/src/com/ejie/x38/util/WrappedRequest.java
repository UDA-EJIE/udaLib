/*
* Copyright 2022 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* https://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class WrappedRequest extends HttpServletRequestWrapper {
	private final String requestMethod;
	private final Map<String, String[]> modifiableParameters;
	private Map<String, String[]> allParameters = null;

	/**
	 * Crea una envoltura sobre la petición que permite incluir parámetros
	 * adicionales en el objeto de petición. No lee prematuramente los parámetros de
	 * la petición original.
	 * 
	 * @param request          Petición original.
	 * @param additionalParams Parámetros adicionales a ingresar en la petición.
	 */
	public WrappedRequest(final HttpServletRequest request, final Map<String, String[]> additionalParams) {
		super(request);
		modifiableParameters = new TreeMap<String, String[]>();
		modifiableParameters.putAll(additionalParams);
		requestMethod = ((HttpServletRequest) super.getRequest()).getMethod();
	}

	/**
	 * Crea una envoltura sobre la petición que permite incluir parámetros
	 * adicionales en el objeto de petición además de modificar el método. No lee
	 * prematuramente los parámetros de la petición original.
	 * 
	 * @param request          Petición original.
	 * @param additionalParams Parámetros adicionales a ingresar en la petición.
	 * @param method           Método a definir en la petición.
	 */
	public WrappedRequest(final HttpServletRequest request, final Map<String, String[]> additionalParams, final String method) {
		super(request);
		modifiableParameters = new TreeMap<String, String[]>();
		modifiableParameters.putAll(additionalParams);
		requestMethod = method;
	}

	@Override
	public String getParameter(final String name) {
		String[] strings = getParameterMap().get(name);
		if (strings != null) {
			return strings[0];
		}
		return super.getParameter(name);
	}

	@Override
	@SuppressWarnings("unchecked")
	public Map<String, String[]> getParameterMap() {
		if (allParameters == null) {
			allParameters = new TreeMap<String, String[]>();
			allParameters.putAll(super.getParameterMap());
			allParameters.putAll(modifiableParameters);
		}
		// Devuelve una colección no modificable porque se debe mantener el contrato.
		return Collections.unmodifiableMap(allParameters);
	}

	@Override
	public Enumeration<String> getParameterNames() {
		return Collections.enumeration(getParameterMap().keySet());
	}

	@Override
	public String[] getParameterValues(final String name) {
		return getParameterMap().get(name);
	}

	@Override
	public String getMethod() {
		return requestMethod;
	}
}
