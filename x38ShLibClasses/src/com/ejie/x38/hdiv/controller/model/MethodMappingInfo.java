package com.ejie.x38.hdiv.controller.model;

import java.util.HashSet;
import java.util.Set;

import org.springframework.web.bind.annotation.RequestMethod;

public class MethodMappingInfo {

	private final Set<RequestMethod> methodCondition;

	private final Set<String> mappings;

	public MethodMappingInfo(final Set<String> mappings, final Set<RequestMethod> methodCondition) {
		this.mappings = mappings;
		if (methodCondition == null) {
			this.methodCondition = new HashSet<RequestMethod>();
		}
		else {
			this.methodCondition = methodCondition;
		}
	}

	public Set<String> getMappings() {
		return mappings;
	}

	public Set<RequestMethod> getMethodCondition() {
		return methodCondition;
	}

}
