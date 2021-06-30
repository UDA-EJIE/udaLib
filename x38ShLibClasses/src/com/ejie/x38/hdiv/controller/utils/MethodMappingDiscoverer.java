package com.ejie.x38.hdiv.controller.utils;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.springframework.core.MethodParameter;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ejie.x38.hdiv.controller.model.MethodMappingInfo;

public class MethodMappingDiscoverer {

	private final Map<String, MethodMappingInfo> methodMappings = new HashMap<String, MethodMappingInfo>();

	private final RequestMappingHandlerMapping handler;

	public MethodMappingDiscoverer(final RequestMappingHandlerMapping handler) {
		this.handler = handler;
	}

	public void addMethodMappings(final String method, final Set<String> mapping, final Set<RequestMethod> methodCondition, final MethodParameter[] parameters) {
		MethodMappingInfo mappings = methodMappings.get(method);
		if (mappings == null) {
			mappings = new MethodMappingInfo(mapping, methodCondition, parameters);
			methodMappings.put(method, mappings);
		}
		else {
			mappings.getMappings().addAll(mapping);
			mappings.getMethodCondition().addAll(methodCondition);
		}
	}

	public Method getMethodFromMapping(final HttpServletRequest request) {
		HandlerExecutionChain handlerExecutionChain;
		Method method = null;
		try {
			handlerExecutionChain = handler.getHandler(request);

			if (handlerExecutionChain != null) {
				method = ((HandlerMethod) handlerExecutionChain.getHandler()).getMethod();
			}
		}
		catch (Exception e) {
		}
		return method;
	}

	public MethodMappingInfo getMethodMappings(final String method) {
		return methodMappings.get(method);
	}
}
