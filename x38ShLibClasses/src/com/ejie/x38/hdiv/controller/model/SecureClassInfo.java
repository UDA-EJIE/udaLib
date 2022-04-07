package com.ejie.x38.hdiv.controller.model;

public class SecureClassInfo {

	private String paramName;
	private String methodName;
	private Class<?> targetClass;
	
	public SecureClassInfo(String paramName, String methodName, Class<?> targetClass) {
		this.paramName = paramName;
		this.methodName = methodName;
		this.targetClass = targetClass;
	}

	public String getParamName() {
		return paramName;
	}
	
	public String getMethodName() {
		return methodName;
	}

	public Class<?> getTargetClass() {
		return targetClass;
	}
}
