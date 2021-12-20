package com.ejie.x38.hdiv.processor;

import java.lang.reflect.Proxy;

import org.springframework.stereotype.Component;

@Component
public class EncriptorResponseLinkProcessor extends ResponseLinkProcesor {

	@Override
	protected void onSecureIdentifiableFound(Object object) {
		setAsProxy(object, "id");
	}

	@Override
	protected void onSecureIdContainerFound(Object object) {
		
	}
	
	private void setAsProxy(Object object, String paramName) {
		Proxy.newProxyInstance(
				object.getClass().getClassLoader(),
		        new Class[]{ object.getClass() },
		        new SecureInvocationHandler(object, paramName)
		);
	}

}
