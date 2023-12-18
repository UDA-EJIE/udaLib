package com.ejie.x38.hdiv.controller.model;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ejie.hdiv.services.NoEntity;
import com.ejie.hdiv.services.TrustAssertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.util.UriComponentsBuilder;

public class UDALinkMappingInfo {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDALinkMappingInfo.class);

	private final String name;
	
	private final boolean allowSubEntities;

	private MappingInfo<?> entityMappingInfo;

	private MappingInfo<?> staticMappingInfo;

	private Set<RequestMethod> methodCondition;

	public UDALinkMappingInfo(final String name, final boolean allowSubEntities, final Class<?> allower, final MethodMappingInfo mappings) {
		this.name = name;
		this.allowSubEntities = allowSubEntities;
		setMappingInfo(allower, mappings);
	}

	public MappingInfo<?> getEntityMappingInfo() {
		return entityMappingInfo;
	}

	public MappingInfo<?> getStaticMappingInfo() {
		return staticMappingInfo;
	}

	public String getName() {
		return name;
	}
	
	public boolean isAllowSubEntities() {
		return allowSubEntities;
	}

	public Set<RequestMethod> getMethodCondition() {
		return methodCondition;
	}

	public List<RequestMethod> getMethodForLinkCondition() {
		if (methodCondition != null && !methodCondition.isEmpty()) {
			return new ArrayList<RequestMethod>(methodCondition);
		}
		return Arrays.asList(RequestMethod.values());
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void setMappingInfo(final Class<?> allower, final MethodMappingInfo mappings) {

		if(mappings == null) {
			LOGGER.debug("No mappings found for allower " + allower.getName());
			return;
		}

		Set<String> staticMapping = new HashSet<String>();
		Set<String> entityMapping = new HashSet<String>();
		Set<String> noEntityParams = new HashSet<String>();
		
		for (String mapping : mappings.getMappings()) {
			int pathVariableCount = StringUtils.countOccurrencesOf(mapping, "{");
			noEntityParams.addAll(getParametersNoEntityAnnotation(mappings.getParameters(), mapping));
			if (pathVariableCount > 0 && noEntityParams.size() < pathVariableCount) {
				// Template mapping
				entityMapping.add(mapping);
			}
			else {
				// Static mapping
				staticMapping.add(mapping);
			}
			LOGGER.debug("Mapping found for link " + name + " and allower " + allower.getName() + " --> " + mapping);
		}

		LinkPredicate<?> allowMethod = null;
		// Gets allower method once per link
		if (allower != Void.class) {
			if (LinkPredicate.class.isAssignableFrom(allower)) {
				try {
					allowMethod = (LinkPredicate<?>) allower.newInstance();
				}
				catch (Exception e) {
					LOGGER.error("Error getting allower method for class : " + allower);
				}
			}
			else {
				try {
					for (Method m : allower.getMethods()) {
						if (m.getReturnType() == LinkPredicate.class && Modifier.isStatic(m.getModifiers())) {
							allowMethod = (LinkPredicate<?>) m.invoke(null);
						}
					}
				}
				catch (Exception e) {
					LOGGER.error("Error getting invoking allower method for class : " + allower);
				}
			}
		}

		entityMappingInfo = new MappingInfo(allowMethod, entityMapping, noEntityParams);
		staticMappingInfo = new MappingInfo(allowMethod, staticMapping, noEntityParams);
		methodCondition = mappings.getMethodCondition();
	}
	
	private List<String> getPathVariableNamesFromMapping(String mapping) {
		List<String> pathVariableNames = new ArrayList<String>();
		for (String segment : UriComponentsBuilder.fromUriString(mapping).build().getPathSegments()) {
			if (segment.startsWith("{")) {
				pathVariableNames.add(segment.substring(1, segment.length() - 1));
			}
		}
		return pathVariableNames;
	}
	
	private Set<String> getParametersNoEntityAnnotation(final MethodParameter[] parameters, final String mapping) {
		Set<String> noEntityParams = new HashSet<String>();
		List<String> pathVariableNames = getPathVariableNamesFromMapping(mapping);
		
		int count = 0;
		for (int i = 0;i < parameters.length;i++) {
			MethodParameter param = parameters[i];
			TrustAssertion annotation = param.getParameterAnnotation(TrustAssertion.class);
			if (annotation != null) {
				if(annotation.idFor() == NoEntity.class) {
					noEntityParams.add(pathVariableNames.get(count));
				}
				count++;
			}
		}
		return noEntityParams;
	}

}
