package com.ejie.x38.hdiv.filter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletRequestWrapper;

import com.ejie.x38.hdiv.util.ObfuscatorUtils;

public class DeobfuscatorRequest extends HttpServletRequestWrapper {

	public DeobfuscatorRequest(HttpServletRequest request) {
		super(request);
	}

	@Override
	public String getParameter(String name) {
		String value = super.getParameter(name);
		if(value != null && ObfuscatorUtils.isObfuscatedId(value) ) {
			return ObfuscatorUtils.deobfuscate(value);
		}
		return value;
	}

	@Override
	public String[] getParameterValues(String name) {
		String[] values = super.getParameterValues(name);
		for(int i = 0; i<values.length; i++) {
			if(ObfuscatorUtils.isObfuscatedId(values[i]) ) {
				values[i] = ObfuscatorUtils.deobfuscate(values[i]);
			}
		}
		return values;
	}

}
