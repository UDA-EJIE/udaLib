package com.ejie.x38.hdiv.aspect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hdiv.services.LinkProvider;
import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.SecureIdentifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

import com.ejie.x38.hdiv.controller.model.UDALinkResources;
import com.ejie.x38.hdiv.controller.utils.DinamicLinkProvider;
import com.ejie.x38.hdiv.controller.utils.UDASecureResourceProcesor;

@Aspect
@Component
public class LinkResourcesAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(LinkResourcesAspect.class);

	private static final int MAX_DEEP = 8;

	@Autowired
	private LinkProvider linkProvider;

	@Around("@annotation(com.ejie.x38.hdiv.annotation.UDALink)")
	public Object processLinks(final ProceedingJoinPoint joinPoint) throws Throwable {

		Object result = joinPoint.proceed();
		try {

			Class<?> controller = joinPoint.getTarget().getClass();

			UDALinkResources udaLinkResources = new UDALinkResources();
			fillResources(result, 0, udaLinkResources, false);
			UDASecureResourceProcesor.processLinks(udaLinkResources, controller, (DinamicLinkProvider) linkProvider);

		}
		catch (Throwable e) {
			LOGGER.error("Error processing links with exception:", e);
		}

		return result;

	}

	private void fillResources(final Object result, final int deep, UDALinkResources udaLinkResources, boolean isSubEntity) {

		if (result == null || deep > MAX_DEEP) {
			return;
		}
		
		List<Object> resources = isSubEntity ? udaLinkResources.getSubEntities() : udaLinkResources.getEntities();
		if (result instanceof Resource) {
			resources.add(result);
			Object content = ((Resource<?>)result).getContent();
			if(content != null) {
				Field[] fields = content.getClass().getDeclaredFields();
				checkFields(fields, content, deep + 1, udaLinkResources);	
			}
			
		}else if (result instanceof SecureIdentifiable<?> || result instanceof SecureIdContainer) {
			resources.add(result);
			
			Field[] fields = result.getClass().getDeclaredFields();
			checkFields(fields, result, deep + 1, udaLinkResources);
			
		}
		else if (result instanceof Iterable) {
			for (Object o : (Iterable<?>) result) {
				fillResources(o, deep + 1, udaLinkResources, isSubEntity);
			}
		}
		else if (result instanceof Map) {
			for (Object o : ((Map<?, ?>) result).values()) {
				fillResources(o, deep + 1, udaLinkResources, isSubEntity);
			}
		}
		else if (!isJRECLass(result.getClass().getName())) {
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
	}
	
	private void checkFields(Field[] fields, Object object, final int deep, UDALinkResources udaLinkResources) {
		
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

	String[] jrePackages = new String[] { "java.", "com.sun.", "sun.", "oracle.", "org.xml.", "com.oracle." };

	public boolean isJRECLass(final String className) {
		for (String jrePackage : jrePackages) {
			if (className.startsWith(jrePackage)) {
				return true;
			}
		}
		return false;
	}

}
