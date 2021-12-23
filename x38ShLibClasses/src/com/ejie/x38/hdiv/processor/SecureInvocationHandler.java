package com.ejie.x38.hdiv.processor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

import com.hdivsecurity.services.util.HdivHATEOASUtils;

public class SecureInvocationHandler implements InvocationHandler {
 
    private final Object invocationTarget;
    private SecureClassInfo[] secureClassInfoList = null;
    
    public SecureInvocationHandler(Object invocationTarget, SecureClassInfo... secureClassInfo) {
        this.invocationTarget = invocationTarget;
        if(secureClassInfo != null) {
        	this.secureClassInfoList = secureClassInfo;
        }
    }
 
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(String.format("Calling method %s with args: %s",
                method.getName(), Arrays.toString(args)));
        
        if(secureClassInfoList != null) {
        	for(SecureClassInfo info : secureClassInfoList) {
            	if(method.getName().endsWith(info.getParamName())) {
	        		return invokeProxy(method, args, info);
            	}
        	}
        }
        
        return method.invoke(invocationTarget, args);
        
    }
    
    private Object invokeProxy(Method method, Object[] args, SecureClassInfo info) throws Throwable {
    	Object value = method.invoke(invocationTarget, args);
    	try {
    		return HdivHATEOASUtils.ofuscate(value, info.getTargetClass(), info.getParamName());
    	}catch(Throwable e) {
    		return value;
    	}
    }
    
    public static class SecureClassInfo {
    	private String paramName;
    	private Class<?> targetClass;
    	
    	public SecureClassInfo(String paramName, Class<?> targetClass) {
    		this.paramName = paramName;
    		this.targetClass = targetClass;
    	}

		public String getParamName() {
			return paramName;
		}

		public Class<?> getTargetClass() {
			return targetClass;
		}
    	
    }
}
