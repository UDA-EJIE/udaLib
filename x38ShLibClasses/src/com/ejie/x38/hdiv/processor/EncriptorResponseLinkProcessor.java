package com.ejie.x38.hdiv.processor;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.services.LinkProvider;
import org.hdiv.services.TrustAssertion;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ejie.x38.hdiv.controller.model.UDALinkResources;
import com.ejie.x38.hdiv.processor.SecureInvocationHandler.SecureClassInfo;

import javassist.util.proxy.ProxyFactory;

@Component
public class EncriptorResponseLinkProcessor extends ResponseLinkProcesor {
	
	private static final String ID = "id";
	private static final String GET = "get";
	private static final String GET_ID = "getId";

	@Override
	public Object checkResponseToLinks(final Object object, Class<?> controller, LinkProvider<?> linkProvider) throws Throwable {

		UDALinkResources udaLinkResources = new UDALinkResources();
		Object processed = fillResources(object, 0, udaLinkResources, false, null);
		//UDASecureResourceProcesor.processLinks(udaLinkResources, controller, (DinamicLinkProvider) linkProvider);
		return processed;
	}

	@Override
	protected Object updateOnSecureIdentifiableFound(Object object) {
		try {
			Object value = object.getClass().getDeclaredMethod(GET_ID);
	    	//Proxy only valued objects
	    	if(value != null) {
	    		
	    		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
				Resource<Object> resource = UDASecureResourceProcesor.getAllowedEntityResource(object, request);
				UDASecureResourceProcesor.addEntityLink(resource.getLinks(), resource.getContent(), ID, GET_ID, request);
				
	    		return setAsProxy(object, new SecureClassInfo(ID, GET_ID, object.getClass()));
	    	}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return object;
	}

	@Override
	protected Object updateOnSecureIdContainerFound(Object object) {
		System.out.println("updateOnSecureIdContainerFound-->"+object);
		try {
			List<SecureClassInfo> secureClassInfoList = getIdentificatorInfo(object);
		
			if(secureClassInfoList != null && !secureClassInfoList.isEmpty()) {
				HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
				Resource<Object> resource = UDASecureResourceProcesor.getAllowedEntityResource(object, request);
				for(SecureClassInfo info : secureClassInfoList) {
					UDASecureResourceProcesor.addEntityLink(resource.getLinks(), resource.getContent(), info.getParamName(), info.getMethodName(), request);
				}
				return setAsProxy(object, secureClassInfoList.toArray(new SecureClassInfo[0]));	
			}
		}catch(Exception e) {
			e.printStackTrace();
		}
		return object;
		
	}

	private Object setAsProxy(Object object, SecureClassInfo... secureClassInfo) {
		System.out.println("New proxy instance for "+ object);
		Class<?> originalClass = object.getClass();
	    ProxyFactory factory = new ProxyFactory();
	
	    factory.setSuperclass(originalClass);
	
	    factory.setHandler(new SecureInvocationHandler(object, secureClassInfo));
	    Class<?> proxyClass = factory.createClass();
		try {
		    return proxyClass.newInstance();
		}catch(Exception e) {
			return object;
		}
	}
	
	private List<SecureClassInfo> getIdentificatorInfo(Object object) throws Exception {
		
		List<SecureClassInfo> secureInfoList = new ArrayList<SecureClassInfo>();
		
		for(Field field  : object.getClass().getDeclaredFields()) {
			TrustAssertion trustAssertion = field.getAnnotation(TrustAssertion.class);
		    if (trustAssertion != null && trustAssertion.idFor() != null) {
		    	String propertyName = field.getName();
		    	String methodName = GET+ propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1);
		    	Object value = object.getClass().getDeclaredMethod(GET+ propertyName.substring(0, 1).toUpperCase() + propertyName.substring(1));
		    	//Proxy only valued objects
		    	if(value != null) {
		    		secureInfoList.add(new SecureClassInfo(field.getName(), methodName,  trustAssertion.idFor()));
		    	}
		    }
		}
		
		return secureInfoList;
		
	}
	
	@Override
	protected void onOtherType( Object result, final int deep, UDALinkResources udaLinkResources, boolean isSubEntity, Integer parentIndex) {
		//Do nothing
	}

}
