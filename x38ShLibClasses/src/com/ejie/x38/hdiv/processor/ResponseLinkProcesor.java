package com.ejie.x38.hdiv.processor;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.List;
import java.util.Map;

import org.hdiv.services.LinkProvider;
import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.SecureIdentifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Resource;

import com.ejie.x38.hdiv.controller.model.UDALinkResources;
import com.ejie.x38.hdiv.controller.utils.DinamicLinkProvider;
import com.ejie.x38.hdiv.controller.utils.UDASecureResourceProcesor;

public abstract class ResponseLinkProcesor {

	private static final Logger LOGGER = LoggerFactory.getLogger(ResponseLinkProcesor.class);

	private static final int MAX_DEEP = 8;
	
	private static final String[] JRE_PACKAGES = new String[] { "java.", "com.sun.", "sun.", "oracle.", "org.xml.", "com.oracle." };


	public Object checkResponseToLinks(final Object object, Class<?> controller, LinkProvider<?> linkProvider) throws Throwable {

			UDALinkResources udaLinkResources = new UDALinkResources();
			Object processed = fillResources(object, 0, udaLinkResources, false);
			UDASecureResourceProcesor.processLinks(udaLinkResources, controller, (DinamicLinkProvider) linkProvider);
			return processed;
	}

	private Object fillResources( Object result, final int deep, UDALinkResources udaLinkResources, boolean isSubEntity) {

		if (result == null || deep > MAX_DEEP) {
			return result;
		}
		
		List<Object> resources = isSubEntity ? udaLinkResources.getSubEntities() : udaLinkResources.getEntities();
		if (result instanceof Resource) {
			resources.add(result);
			Object content = ((Resource<?>)result).getContent();
			if(content != null) {
				checkFields(content, deep + 1, udaLinkResources);	
			}
			
		}else if (result instanceof SecureIdentifiable<?> ) {
			
			resources.add(result);
			checkFields(result, deep + 1, udaLinkResources);
			result = updateOnSecureIdentifiableFound(result);
			
		}else if (result instanceof SecureIdContainer) {
			resources.add(result);
			checkFields(result, deep + 1, udaLinkResources);
			result = updateOnSecureIdContainerFound(result);
			
		}else if (result instanceof Iterable) {
			for (Object o : (Iterable<?>) result) {
				fillResources(o, deep + 1, udaLinkResources, isSubEntity);
			}
		}else if (result instanceof Map) {
			for (Object o : ((Map<?, ?>) result).values()) {
				fillResources(o, deep + 1, udaLinkResources, isSubEntity);
			}
		}
		else if (!isJRECLass(result.getClass().getName())) {
			//TODO: This case should be deleted due to an unneeded action
			//Test it just in case
			try {
				Method[] methods = result.getClass().getDeclaredMethods();
				for (Method method : methods) {
					try {
						if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 0
								&& method.getReturnType() != null) {
							fillResources(method.invoke(result), deep + 1, udaLinkResources, isSubEntity);
						}
					}
					catch (Exception e) {
					}
				}
			}
			catch (Exception e) {
				LOGGER.error("Error getting methods of class:" + result.getClass().getName(), e);
			}
		}
		
		return result;
	}
	
	private void checkFields(Object object, final int deep, UDALinkResources udaLinkResources) {
		
		Field[] fields = object.getClass().getDeclaredFields();
		for (Field field : fields) {
			try {
				if( !Modifier.isStatic(field.getModifiers()) && (Resource.class.isAssignableFrom(field.getDeclaringClass()) || SecureIdentifiable.class.isAssignableFrom(field.getDeclaringClass()) || SecureIdContainer.class.isAssignableFrom(field.getDeclaringClass()))) {
					field.setAccessible(true);
					fillResources(field.get(object), deep, udaLinkResources, true);
				}
			}
			catch (Exception e) {
				LOGGER.error("Error getting field " + field.getName() + " of class:" + object.getClass().getName(), e);
			}
		}
		
	}
	
	public boolean isJRECLass(final String className) {
		for (String jrePackage : JRE_PACKAGES) {
			if (className.startsWith(jrePackage)) {
				return true;
			}
		}
		return false;
	}
	
	protected abstract Object updateOnSecureIdentifiableFound(Object object);
	
	protected abstract Object updateOnSecureIdContainerFound(Object object);

}
