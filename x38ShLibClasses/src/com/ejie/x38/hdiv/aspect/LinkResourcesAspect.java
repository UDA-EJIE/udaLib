package com.ejie.x38.hdiv.aspect;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.hdiv.services.LinkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.Resource;
import org.springframework.stereotype.Component;

import com.ejie.x38.hdiv.controller.utils.DinamicLinkProvider;
import com.ejie.x38.hdiv.controller.utils.UDASecureResourceProcesor;

@Aspect
@Component
public class LinkResourcesAspect {

	private static final Logger LOGGER = LoggerFactory.getLogger(LinkResourcesAspect.class);

	private static final int MAX_DEEP = 4;

	@Autowired
	private LinkProvider linkProvider;

	@Around("@annotation(com.ejie.x38.hdiv.annotation.UDALink)")
	public Object processLinks(final ProceedingJoinPoint joinPoint) throws Throwable {

		Object result = joinPoint.proceed();
		try {

			Class<?> controller = joinPoint.getTarget().getClass();

			List<Object> entities = getResources(result, 0);
			UDASecureResourceProcesor.processLinks(entities, controller, (DinamicLinkProvider) linkProvider);

		}
		catch (Throwable e) {
			LOGGER.error("Error processing links with exception:", e);
		}

		return result;

	}

	private List<Object> getResources(final Object result, final int deep) {

		List<Object> resources = new ArrayList<Object>();
		if (result == null || deep > MAX_DEEP) {
			return resources;
		}
		if (result instanceof Resource) {
			resources.add(result);
		}
		else if (result instanceof Iterable) {
			for (Object o : (Iterable<?>) result) {
				resources.addAll(getResources(o, deep + 1));
			}
		}
		else if (!isJRECLass(result.getClass().getName())) {
			try {
				Method[] methods = result.getClass().getDeclaredMethods();
				for (Method method : methods) {
					try {
						if (Modifier.isPublic(method.getModifiers()) && method.getParameterTypes().length == 0
								&& method.getReturnType() != null) {
							resources.addAll(getResources(method.invoke(result), deep + 1));
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
		return resources;
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
