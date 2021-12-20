package com.ejie.x38.hdiv.processor;

import java.lang.reflect.Proxy;

import org.springframework.stereotype.Component;

@Component
public class EncriptorResponseLinkProcessor extends ResponseLinkProcesor {

	@Override
	protected Object updateOnSecureIdentifiableFound(Object object) {
		return setAsProxy(object, "id");
	}

	@Override
	protected Object updateOnSecureIdContainerFound(Object object) {
		return null;
	}
	
	private Object setAsProxy(Object object, String paramName) {
		return Proxy.newProxyInstance(
				object.getClass().getClassLoader(),
		        new Class[]{ object.getClass() },
		        new SecureInvocationHandler(object, paramName)
		);
	}

}
