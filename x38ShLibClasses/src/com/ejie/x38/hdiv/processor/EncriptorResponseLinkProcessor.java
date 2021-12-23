package com.ejie.x38.hdiv.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import org.hdiv.services.TrustAssertion;
import org.springframework.stereotype.Component;

import com.ejie.x38.hdiv.processor.SecureInvocationHandler.SecureClassInfo;

@Component
public class EncriptorResponseLinkProcessor extends ResponseLinkProcesor {

	@Override
	protected Object updateOnSecureIdentifiableFound(Object object) {
		return setAsProxy(object, new SecureClassInfo("id", object.getClass()));
	}

	@Override
	protected Object updateOnSecureIdContainerFound(Object object) {
		return setAsProxy(object, getIdentificatorInfo(object).toArray(new SecureClassInfo[0]));
	}
	
	private Object setAsProxy(Object object, SecureClassInfo... secureClassInfo) {
		return Proxy.newProxyInstance(
				object.getClass().getClassLoader(),
		        new Class[]{ object.getClass() },
		        new SecureInvocationHandler(object, secureClassInfo)
		);
	}
	
	private List<SecureClassInfo> getIdentificatorInfo(Object object) {
		
		List<SecureClassInfo> secureInfoList = new ArrayList<SecureClassInfo>();
		
		for(Field field  : object.getClass().getDeclaredFields()) {
			TrustAssertion trustAssertion = field.getAnnotation(TrustAssertion.class);
		    if (trustAssertion != null && trustAssertion.idFor() != null) {
		    	secureInfoList.add(new SecureClassInfo(field.getName(), trustAssertion.idFor()));
		    }
		}
		
		return secureInfoList;
		
	}

}
