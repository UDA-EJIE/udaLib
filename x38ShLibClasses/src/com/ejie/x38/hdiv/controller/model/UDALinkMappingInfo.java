package com.ejie.x38.hdiv.controller.model;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.hdiv.ee.comcore.discovery.resolve.NoEntity;
import org.hdiv.services.TrustAssertion;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMethod;

public class UDALinkMappingInfo {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDALinkMappingInfo.class);

	private final String name;

	private MappingInfo<?> entityMappingInfo;

	private MappingInfo<?> staticMappingInfo;

	private Set<RequestMethod> methodCondition;

	public UDALinkMappingInfo(final String name, final Class<?> allower, final MethodMappingInfo mappings) {
		this.name = name;
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

		Set<String> staticMapping = new HashSet<String>();
		Set<String> entityMapping = new HashSet<String>();

		for (String mapping : mappings.getMappings()) {
			int pathVariableCount = StringUtils.countOccurrencesOf("mapping", "{");
			if (pathVariableCount > 0 && getParametersNoEntityAnnotationCount(mappings.getParameters()) < pathVariableCount) {
				// Template mapping
				entityMapping.add(mapping);
			}
			else {
				// Static mapping
				staticMapping.add(mapping);
			}
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

		entityMappingInfo = new MappingInfo(allowMethod, entityMapping);
		staticMappingInfo = new MappingInfo(allowMethod, staticMapping);
		methodCondition = mappings.getMethodCondition();
	}
	
	private int getParametersNoEntityAnnotationCount(final MethodParameter[] parameters) {
		int count = 0;
		for(MethodParameter param: parameters ) {
			TrustAssertion annotation = param.getParameterAnnotation(TrustAssertion.class);
			if(annotation != null && annotation.idFor() == NoEntity.class) {
				count++;
			}
		}
		return count;
	}

}
