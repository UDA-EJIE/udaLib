package com.ejie.x38.hdiv.controller.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMethod;

public class MethodMappingInfo {

	private final Set<RequestMethod> methodCondition;

	private final Set<String> mappings;
	
	private final MethodParameter[] parameters;

	public MethodMappingInfo(final Set<String> mappings, final Set<RequestMethod> methodCondition, final MethodParameter[] parameters) {
		this.mappings = mappings;
		if (methodCondition == null) {
			this.methodCondition = new HashSet<RequestMethod>();
		}
		else {
			this.methodCondition = methodCondition;
		}
		if (parameters == null) {
			this.parameters = new MethodParameter[0];
		}
		else {
			this.parameters = parameters;
		}
	}

	public Set<String> getMappings() {
		return mappings;
	}

	public Set<RequestMethod> getMethodCondition() {
		return methodCondition;
	}

	public MethodParameter[] getParameters() {
		return parameters;
	}
	
	

}
