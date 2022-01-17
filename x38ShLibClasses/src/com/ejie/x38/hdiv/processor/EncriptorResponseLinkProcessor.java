package com.ejie.x38.hdiv.processor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.services.EntityStateRecorder;
import org.hdiv.services.LinkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import com.ejie.x38.hdiv.controller.model.IdentifiableModelWrapper;
import com.ejie.x38.hdiv.controller.model.SecureClassInfo;
import com.ejie.x38.hdiv.controller.model.UDALinkResources;

import javassist.util.proxy.ProxyFactory;

@Component
public class EncriptorResponseLinkProcessor extends ResponseLinkProcesor {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EncriptorResponseLinkProcessor.class);
	
	private static final String GET_ID = "getId";
	
	@Autowired
	@Lazy
	private EntityStateRecorder<Link> entityStateRecorder;

	public Object checkResponseToLinks(final Object object, Class<?> controller, LinkProvider<?> linkProvider) throws Throwable {

		Object processed = fillResources(object, 0, null, false, null);
		return processed;
	}
	
	@SuppressWarnings("unchecked")
	protected Object fillResources( Object result, final int deep, UDALinkResources udaLinkResources, boolean isSubEntity, Integer parentIndex) {

		if (result == null) {
			return result;
		}
		
		if (result instanceof IdentifiableModelWrapper<?> ) {
			SecureClassInfo identifiableInfo = new SecureClassInfo( ((IdentifiableModelWrapper<?>) result).getIdentifiableParamName(), GET_ID, ((IdentifiableModelWrapper<?>) result).getTarget());
			result = updateOnIdentifiableModelFound((IdentifiableModelWrapper<?>)result, identifiableInfo);
		}else if (result instanceof Iterable) {
			List<Object> objects = new ArrayList<Object>();
			for (Object o : (Iterable<?>) result) {
				objects.add(fillResources(o, deep + 1, udaLinkResources, isSubEntity, parentIndex));
			}
			if(result instanceof Collection) {
				try {
					((Collection<Object>) result).clear();
					((Collection<Object>) result).addAll(objects);
				}catch(Exception e) {
					LOGGER.error("Response objects cannot be reasigned");
				}
			}
		}else if (result instanceof Map) {
			for (Entry<Object, Object> entry : ((Map<Object, Object>) result).entrySet()) {
				((Map<Object, Object>) result).put(entry.getKey(), fillResources(entry.getValue(), deep + 1, udaLinkResources, isSubEntity, parentIndex));
			}
		}
		
		return result;
	}

	private Object updateOnIdentifiableModelFound(IdentifiableModelWrapper<?> wrapper, SecureClassInfo identifiableInfo) {
		try {
			//Proxy only valued objects
			String idValue = wrapper.getId();
	    	if(idValue != null) {
	    		
	    		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
				Resource<Object> resource = UDASecureResourceProcesor.getAllowedEntityResource(wrapper.getEntity(), request);
				UDASecureResourceProcesor.addEntityLink(resource.getLinks(), resource.getContent(), identifiableInfo.getParamName(), idValue, identifiableInfo.getTargetClass(), request);
				
	    		return setAsProxy(wrapper, identifiableInfo);
	    	}
		}catch(Exception e) {
		}
		return wrapper;
	}

	private Object setAsProxy(IdentifiableModelWrapper<?> wrapper, SecureClassInfo... secureClassInfo) {
		
		Class<?> originalClass = wrapper.getClass();
	    ProxyFactory factory = new ProxyFactory();
	
	    factory.setSuperclass(originalClass);
	
	    factory.setHandler(new SecureInvocationHandler(wrapper, entityStateRecorder, secureClassInfo));
	    Class<?> proxyClass = factory.createClass();
		try {
		    return proxyClass.newInstance();
		}catch(Exception e) {
			return wrapper;
		}
	}

}
