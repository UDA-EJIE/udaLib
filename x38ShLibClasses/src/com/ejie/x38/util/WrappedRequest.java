package com.ejie.x38.util;

import java.util.Collections;
import java.util.Enumeration;
import java.util.Map;
import java.util.TreeMap;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

public class WrappedRequest extends HttpServletRequestWrapper {
    private final Map<String, String[]> modifiableParameters;
    private Map<String, String[]> allParameters = null;

    /**
     * Crea una envoltura sobre la petición que permite incluir parámetros adicionales
     * en el objeto de petición. No lee prematuramente los parámetros de la petición original.
     * 
     * @param request
     * @param additionalParams
     */
    public WrappedRequest(final HttpServletRequest request, final Map<String, String[]> additionalParams) {
        super(request);
        modifiableParameters = new TreeMap<String, String[]>();
        modifiableParameters.putAll(additionalParams);
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
    	if (modifiableParameters.containsKey("REQUEST_METHOD")) {
    		return modifiableParameters.get("REQUEST_METHOD")[0];
    	} else {
    		return ((HttpServletRequest) super.getRequest()).getMethod();
    	}
	}
}
