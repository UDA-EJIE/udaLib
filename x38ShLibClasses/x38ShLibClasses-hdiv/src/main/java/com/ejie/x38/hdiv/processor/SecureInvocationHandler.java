package com.ejie.x38.hdiv.processor;

import java.lang.reflect.Method;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.ejie.x38.hdiv.controller.model.SecureClassInfo;
import com.ejie.x38.hdiv.util.ObfuscatorUtils;

import javassist.util.proxy.MethodHandler;

public class SecureInvocationHandler implements MethodHandler {
 
	private static final Logger LOGGER = LoggerFactory.getLogger(SecureInvocationHandler.class);
	
    private final Object invocationTarget;
    private SecureClassInfo[] secureClassInfoList = null;
    
    public SecureInvocationHandler(Object invocationTarget, SecureClassInfo... secureClassInfo) {
        this.invocationTarget = invocationTarget;
       	this.secureClassInfoList = secureClassInfo;
    }

    private Object invokeProxy(Method method, Object[] args, SecureClassInfo info) throws Throwable {
    	Object value = method.invoke(invocationTarget, args);
    	if(value != null) {
	    	try {
	    		return ObfuscatorUtils.obfuscate(String.valueOf(value), info.getTargetClass());
	    	}catch(Throwable e) {
	    		LOGGER.error("cannot ofuscate value of method", e);
	    	}
    	}
    	return value;
    }

	@Override
	public Object invoke(Object proxy, Method method, Method proceed, Object[] args) throws Throwable {
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
