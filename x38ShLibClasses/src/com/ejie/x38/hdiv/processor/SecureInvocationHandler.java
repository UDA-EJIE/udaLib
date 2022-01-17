package com.ejie.x38.hdiv.processor;

import java.lang.reflect.Method;
import java.util.Arrays;

import org.hdiv.services.EntityStateRecorder;
import org.springframework.hateoas.Link;

import com.ejie.x38.hdiv.controller.model.SecureClassInfo;

import javassist.util.proxy.MethodHandler;

public class SecureInvocationHandler implements MethodHandler {
 
    private final Object invocationTarget;
    private SecureClassInfo[] secureClassInfoList = null;
    private EntityStateRecorder<Link> entityStateRecorder;
    
    public SecureInvocationHandler(Object invocationTarget, EntityStateRecorder<Link> entityStateRecorder, SecureClassInfo... secureClassInfo) {
        this.invocationTarget = invocationTarget;
       	this.secureClassInfoList = secureClassInfo;
       	this.entityStateRecorder = entityStateRecorder;
    }

    private Object invokeProxy(Method method, Object[] args, SecureClassInfo info) throws Throwable {
    	Object value = method.invoke(invocationTarget, args);
    	if(value != null) {
	    	try {
	    		return entityStateRecorder.ofuscate(value, info.getTargetClass(), info.getParamName());
	    	}catch(Throwable e) {
	    		System.out.println("cannot ofuscate value of method");
	    		e.printStackTrace();
	    	}
    	}
    	return value;
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
