package com.ejie.x38.hdiv.processor;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Arrays;

public class SecureInvocationHandler implements InvocationHandler {
 
    private final Object invocationTarget;
    private String identifiableParam = null;
    
    public SecureInvocationHandler(Object invocationTarget, String identifiableParam) {
        this.invocationTarget = invocationTarget;
        this.identifiableParam = identifiableParam;
    }
 
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(String.format("Calling method %s with args: %s",
                method.getName(), Arrays.toString(args)));
        
        if(identifiableParam != null && method.getName().endsWith(identifiableParam)) {
        	
        	System.out.println("XAS-->identifiable param: " + identifiableParam);
        	return method.invoke(invocationTarget, args);
        }else {
        	return method.invoke(invocationTarget, args);
        }
    }

}
