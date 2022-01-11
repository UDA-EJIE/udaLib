package com.ejie.x38.hdiv.processor;

import java.lang.reflect.Method;
import java.util.Arrays;

import com.hdivsecurity.services.util.HdivHATEOASUtils;

import javassist.util.proxy.MethodHandler;

public class SecureInvocationHandler implements MethodHandler {
 
    private final Object invocationTarget;
    private SecureClassInfo[] secureClassInfoList = null;
    
    public SecureInvocationHandler(Object invocationTarget, SecureClassInfo... secureClassInfo) {
        this.invocationTarget = invocationTarget;
        if(secureClassInfo != null) {
        	this.secureClassInfoList = secureClassInfo;
        }
    }

    private Object invokeProxy(Method method, Object[] args, SecureClassInfo info) throws Throwable {
    	Object value = method.invoke(invocationTarget, args);
    	if(value != null) {
	    	try {
	    		return HdivHATEOASUtils.ofuscate(value, info.getTargetClass(), info.getParamName());
	    	}catch(Throwable e) {
	    		System.out.println("cannot ofuscate value of method");
	    	}
    	}
    	return value;
    }
    
    public static class SecureClassInfo {
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

	@Override
	public Object invoke(Object proxy, Method method, Method proceed, Object[] args) throws Throwable {
		System.out.println(String.format("Calling method %s with args: %s",
                method.getName(), Arrays.toString(args)));
        
        if(secureClassInfoList != null) {
        	for(SecureClassInfo info : secureClassInfoList) {
            	if(method.getName().equals(info.getMethodName())) {
	        		return invokeProxy(method, args, info);
            	}
        	}
        }
        
        return method.invoke(invocationTarget, args);
	}
}
