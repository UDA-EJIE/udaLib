package com.ejie.x38.hdiv.controller.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;

import com.ejie.x38.hdiv.annotation.UDALink;
import com.ejie.x38.hdiv.annotation.UDALinkAllower;
import com.ejie.x38.hdiv.controller.model.MethodMappingInfo;
import com.ejie.x38.hdiv.controller.model.UDALinkMappingInfo;

public class MethodLinkDiscoverer {

	private static final Logger LOGGER = LoggerFactory.getLogger(MethodLinkDiscoverer.class);
	
	@Autowired
	private MethodMappingDiscoverer methodMappingDiscoverer;

	private final Map<String, List<UDALinkMappingInfo>> methodLinks = new ConcurrentHashMap<String, List<UDALinkMappingInfo>>();

	public void addMethodLinkInfo(final Method method) {

		String methodName = method.toString();
		Class<?> clazz = method.getDeclaringClass();

		List<UDALinkMappingInfo> links = methodLinks.get(methodName);
		if (links == null) {
			links = processMethodLinkInfo(clazz, method);
			methodLinks.put(methodName, links);
		}
	}

	private List<UDALinkMappingInfo> processMethodLinkInfo(final Class<?> controller, final Method currentMethod) {

		List<UDALinkMappingInfo> allowInfoList = null;

		UDALink udalink = currentMethod.getAnnotation(UDALink.class);

		// TODO: XAS - Cache allowInfoList by udaLink?
		if (udalink != null) {
			UDALinkAllower[] linkAllowers = udalink.linkTo();
			if (linkAllowers != null) {
				allowInfoList = getAllowedInfo(linkAllowers, controller);
			}
		}
		return allowInfoList;
	}

	private List<UDALinkMappingInfo> getAllowedInfo(final UDALinkAllower[] linkAllowers, final Class<?> controller) {
		List<UDALinkMappingInfo> allowInfo = new ArrayList<UDALinkMappingInfo>();

		LOGGER.debug(linkAllowers.length + " link allowers found for controller " + controller.getName());
		for (UDALinkAllower udalinkAllower : linkAllowers) {

			// The allowed link could belong to other controller defined into UDALinkAllower
			LOGGER.debug(" processing allower " + udalinkAllower.name());	
			for (Method m : getControllerMethods(udalinkAllower, controller)) {
				UDALink mlink = m.getAnnotation(UDALink.class);
				LOGGER.debug(" processing method " + m.toString() + " with link " + mlink.name());
				if (mlink != null && mlink.name().equals(udalinkAllower.name())) {
					LOGGER.debug(" processing method " + m.toString() + " mappings");
					MethodMappingInfo mappings = methodMappingDiscoverer.getMethodMappings(m.toString());
					allowInfo.add(new UDALinkMappingInfo(udalinkAllower.name(), udalinkAllower.allower(), mappings));
				}
			}
		}

		return allowInfo;
	}

	private Method[] getControllerMethods(final UDALinkAllower udalinkAllower, final Class<?> controller) {
		if (udalinkAllower.linkClass() != Void.class) {
			return udalinkAllower.linkClass().getMethods();
		}
		else {
			return controller.getMethods();
		}
	}

	public List<UDALinkMappingInfo> getMethodLinkInfo(final HttpServletRequest request) {
		Method currentMethod = methodMappingDiscoverer.getMethodFromMapping(request);
		String key = currentMethod.toString();
		// Result can be null
		List<UDALinkMappingInfo> allowInfoList;
		if (!methodLinks.containsKey(key)) {
			allowInfoList = processMethodLinkInfo(currentMethod.getDeclaringClass(), currentMethod);
			methodLinks.put(key, allowInfoList);
		}
		else {
			allowInfoList = methodLinks.get(key);
		}
		return allowInfoList;
	}

}
