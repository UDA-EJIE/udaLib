package com.ejie.x38.hdiv.processor;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.services.EntityStateRecorder;
import org.hdiv.util.HDIVUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Resource;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.util.UriComponents;
import org.springframework.web.util.UriComponentsBuilder;

import com.ejie.x38.hdiv.controller.model.LinkInfo;
import com.ejie.x38.hdiv.controller.model.MappingInfo;
import com.ejie.x38.hdiv.controller.model.ReferencedObject;
import com.ejie.x38.hdiv.controller.model.SecureClassInfo;
import com.ejie.x38.hdiv.controller.model.UDALinkMappingInfo;
import com.ejie.x38.hdiv.controller.model.UDALinkResources;
import com.ejie.x38.hdiv.controller.utils.DinamicLinkProvider;
import com.ejie.x38.hdiv.controller.utils.MethodLinkDiscoverer;
import com.hdivsecurity.services.affordance.MethodAwareLink;

public class UDASecureResourceProcesor {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDASecureResourceProcesor.class);

	private static MethodLinkDiscoverer methodLinkDiscoverer;

	private static EntityStateRecorder<Link> entityStateRecorder;
	
	public static void registerMethodLinkDiscoverer(final MethodLinkDiscoverer methodLinkDiscoverer) {
		UDASecureResourceProcesor.methodLinkDiscoverer = methodLinkDiscoverer;
	}
	
	public static void registerEntityStateRecorder(final EntityStateRecorder<Link> entityStateRecorder) {
		UDASecureResourceProcesor.entityStateRecorder = entityStateRecorder;
	}

	public static Resource<Object> asResource(final Object entity, final Class<?> controller) {
		List<Resource<Object>> resources = asResources(Arrays.asList(entity), controller);
		if (resources != null) {
			return resources.get(0);
		}
		return null;
	}

	public static List<Resource<Object>> asResources(final List<Object> entities, final Class<?> controller) {
		return processLinks(entities, null, controller, null);
	}
	
	public static List<Resource<Object>> processLinks(final UDALinkResources udaLinkResources, final Class<?> controller,
			final DinamicLinkProvider linkProvider) {
		return processLinks(udaLinkResources.getEntities(), udaLinkResources.getSubEntities(), controller, linkProvider);
		
	}
	
	static Resource<Object> getAllowedEntityResource(Object entity, HttpServletRequest request) {
		List<UDALinkMappingInfo> allowInfoList = methodLinkDiscoverer.getMethodLinkInfo(request);
		
		if (allowInfoList != null) {
			Map<String, Map<String, Map<Class<?>,Method>>> urlTemplatesMap = new HashMap<String, Map<String, Map<Class<?>,Method>>>();
			String requestStr = getBaseUrl(request).toString();
			List<Resource<Object>> resources = processEntity(entity, request, allowInfoList, requestStr, urlTemplatesMap, false);
			if(!resources.isEmpty() ) {
				return resources.get(0);
			}
		}
		return new Resource<Object>(entity, new ArrayList<Link>());
	}

	private static List<Resource<Object>> processLinks(final List<Object> entities, final List<ReferencedObject> subEntities, final Class<?> controller,
			final DinamicLinkProvider linkProvider) {
		
		LOGGER.debug("Processing links to {} entities and {} subentities", entities.size(), subEntities.size());

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		List<UDALinkMappingInfo> allowInfoList = methodLinkDiscoverer.getMethodLinkInfo(request);

		List<Resource<Object>> resources = new ArrayList<Resource<Object>>();

		if (allowInfoList != null) {
			String requestStr = getBaseUrl(request).toString();
			if (entities != null && !entities.isEmpty()) {
				resources = processEntities(entities, false, request, allowInfoList, requestStr);
			}
			if (subEntities != null && !subEntities.isEmpty()) {
				Map<String, Map<String, Map<Class<?>,Method>>> urlTemplatesMap = new HashMap<String, Map<String, Map<Class<?>,Method>>>();
				// add links to parent resource
				for (ReferencedObject entity : subEntities) {
					Object parent = entities.get(Integer.valueOf(entity.getRef()));
					if(parent instanceof Resource) {
						for(Resource<Object> subentityResurce : processEntity(entity.getEntity(), request, allowInfoList, requestStr, urlTemplatesMap, true)) {
							// is this line needed?
							//((Resource<Object>)parent).add(subentityResurce.getLinks());
							for(SecureClassInfo secureClassInfo : entity.getSecureClassInfo()){
								try {
									Object entityObject =  entity.getEntity();
									//Note: targeted class can be different from entity class 
									Method method = entityObject.getClass().getDeclaredMethod(secureClassInfo.getMethodName());
									addEntityLink(subentityResurce.getLinks(), entityObject, secureClassInfo.getParamName(), String.valueOf(method.invoke(entityObject)), secureClassInfo.getTargetClass(), request);
								}catch(Exception e) {
									LOGGER.error("Cannot add links to entity", e);
								}	
							}
						}
					}
				}
			}
			if (linkProvider != null) {
				processStaticLinks(request, allowInfoList, requestStr, linkProvider);
			}
		}

		return resources;

	}
	
	static void addEntityLink(final List<Link> links, final Object entityObject,
			final String propertyName,final String idValue, Class<?> targetedClass, HttpServletRequest request) throws Exception {

		if(links != null && !links.isEmpty() && idValue != null && entityStateRecorder != null) {
			entityStateRecorder.registerEntity(links, targetedClass, idValue, propertyName, HDIVUtil.getRequestContext(request));
			
		}
	}
	
	private static StringBuilder getBaseUrl(final HttpServletRequest request) {

		StringBuilder url = new StringBuilder();
		String scheme = request.getScheme();
		int port = request.getServerPort();
		if (port < 0) {
			port = 80; // Work around java.net.URL bug
		}

		url.append(scheme);
		url.append("://");
		url.append(request.getServerName());
		if (scheme.equals("http") && port != 80 || scheme.equals("https") && port != 443) {
			url.append(':');
			url.append(port);
		}

		url.append(request.getContextPath());
		
		return url;
	}

	private static <T> void processStaticLinks(final HttpServletRequest request, final List<UDALinkMappingInfo> allowInfoList,
			final String requestStr, final DinamicLinkProvider linkProvider) {
		List<Link> links = new ArrayList<Link>();
		for (UDALinkMappingInfo udaMappingInfo : allowInfoList) {
			// Check allower for the link
			MappingInfo<?> staticMappingInfo = udaMappingInfo.getStaticMappingInfo();
			if (allowedForEntity(null, udaMappingInfo.getName(), staticMappingInfo, request)) {
				Link link;
				for (String linkUrl : staticMappingInfo.getMappings()) {
					link = new Link(requestStr + linkUrl, udaMappingInfo.getName());
					LOGGER.debug("Allowed static link: " + link);
					links.add(new MethodAwareLink(link,
							udaMappingInfo.getMethodForLinkCondition()));
				}
			}
		}
		linkProvider.addLinks(links, request);
	}

	private static List<Resource<Object>> processEntities(final List<Object> entities, final boolean isSubEntity, final HttpServletRequest request,
			final List<UDALinkMappingInfo> allowInfoList, final String requestStr) {

		List<Resource<Object>> resources = new ArrayList<Resource<Object>>();

		if (entities != null && !entities.isEmpty()) {

			if (allowInfoList != null) {

				Map<String, Map<String, Map<Class<?>,Method>>> urlTemplatesMap = new HashMap<String, Map<String, Map<Class<?>,Method>>>();
				// add links to resources
				for (Object entity : entities) {
					resources.addAll(processEntity(entity, request, allowInfoList, requestStr, urlTemplatesMap, isSubEntity));
				}
			}

		}

		return resources;
	}

	private static List<Resource<Object>> processEntity(final Object entity, final HttpServletRequest request,
			final List<UDALinkMappingInfo> allowInfoList, final String requestStr, final Map<String, Map<String, Map<Class<?>,Method>>> urlTemplatesMap, final boolean isSubEntity) {

		List<Resource<Object>> resources = new ArrayList<Resource<Object>>();

		// add links to resources
		Object entityToProcces;
		if (entity instanceof Resource) {
			if (((Resource<?>) entity).getLinks() != null && !((Resource<?>) entity).getLinks().isEmpty()) {
				return resources;
			}
			entityToProcces = ((Resource<?>) entity).getContent();
		}
		else {
			entityToProcces = entity;
		}

		List<Link> entityLinks = new ArrayList<Link>();

		for (UDALinkMappingInfo allowInfo : allowInfoList) {
			
			if(!allowInfo.isAllowSubEntities() && isSubEntity) {
				LOGGER.debug("Sub entity found but not allowed for : " + allowInfo.getName());
			}else {
				// Check allower for this certain entity
				entityLinks.addAll(getAllowedEntityLinks(entityToProcces, allowInfo, request, requestStr, urlTemplatesMap));
			}
		}

		if (entity instanceof Resource) {
			((Resource<?>) entity).add(entityLinks);
		}
		else {
			resources.add(new Resource<Object>(entity, entityLinks));
		}

		return resources;
	}

	private static List<Link> getAllowedEntityLinks(final Object entityToProcces, final UDALinkMappingInfo allowInfo,
			final HttpServletRequest request, final String requestStr, final Map<String, Map<String, Map<Class<?>,Method>>> urlTemplatesMap) {

		List<Link> entityLinks = new ArrayList<Link>();

		if (allowedForEntity(entityToProcces, allowInfo.getName(), allowInfo.getEntityMappingInfo(), request)) {
			// Dynamic links
			// Replace mapping with entity values
			for (String mapping : allowInfo.getEntityMappingInfo().getMappings()) {
				// Replace each segment by entity value;
				try {
					Link link = new Link(revolveURL(mapping, entityToProcces, urlTemplatesMap, requestStr, true), allowInfo.getName());
					LOGGER.debug("Allowed link to entity: " + link);
					entityLinks.add(
							new MethodAwareLink(link,
									allowInfo.getMethodForLinkCondition()));
				}catch(NoSuchMethodException e){
					//Nothing to do
				}
			}
			
			// Static links
			for (String mapping : allowInfo.getStaticMappingInfo().getMappings()) {
				//Need to resolve some urls that has NON id parameter as path variable
				try {
					Link link = new Link(revolveURL(mapping, entityToProcces, urlTemplatesMap, requestStr, false), allowInfo.getName());
					LOGGER.debug("Allowed static link: " + link);
					entityLinks.add(
							new MethodAwareLink(link, allowInfo.getMethodForLinkCondition()));
				}catch(NoSuchMethodException e){
					//Nothing to do
					//revolveURL does not thow exception at this point
				}
			}
		}
		
		LOGGER.debug("AllowedEntityLinks count for {} : {}", allowInfo.getName(), entityLinks.size());
		return entityLinks;

	}
	
	private static String revolveURL(String mapping, final Object entityToProcces, final Map<String, Map<String, Map<Class<?>,Method>>> urlTemplatesMap, String requestStr, boolean throwExc) throws NoSuchMethodException {
		LOGGER.debug("Evaluate mapping: " + mapping);
		Map<String, Object> templateValuesMap = new HashMap<String, Object>();

		Map<String, Map<Class<?>,Method>> segments = urlTemplatesMap.get(mapping);
		UriComponents uriComponents = UriComponentsBuilder.fromUriString(mapping).build();
		if (segments == null) {
			segments = new HashMap<String,Map<Class<?>,Method>>();
			for (String segment : uriComponents.getPathSegments()) {
				// no need to check if segment ends with '}'. We assume that case
				if (segment.startsWith("{")) {
					// it is a template segment. Replace it with the entity value
					int index = segment.indexOf(":");
					if (index > 0) {
						segment = segment.substring(1, index);
					}
					else {
						segment = segment.substring(1, segment.length() - 1);
					}
				
					templateValuesMap.put(segment, getTemplateValuesFromEntity(entityToProcces, addSegmentMethodIfNeeded(entityToProcces, segment , segments), segment, throwExc));
				}
			}
			urlTemplatesMap.put(mapping, segments);
		}
		else {
			
			templateValuesMap = getEntityValueMapFromTemplates(entityToProcces, segments, templateValuesMap, throwExc);
		}

		// Replace each segment by entity value;
		return requestStr + uriComponents.expand(templateValuesMap).getPath();
	}
	
	private static Method addSegmentMethodIfNeeded(final Object entityToProcces, String segment , Map<String, Map<Class<?>,Method>> segments) {
		Class<?> clazz = entityToProcces.getClass();
		
		Map<Class<?>,Method> segmentMethod = segments.get(segment);
		if(segmentMethod == null) {
			segmentMethod = new HashMap<Class<?>,Method>();
			segments.put(segment, segmentMethod);
		}
		
		Method method = segmentMethod.get(clazz);
		
		if(method == null) {
			method = getSegmentMethod(entityToProcces.getClass(), segment);
			LOGGER.debug("use " + method + " to evaluate segment " + segment);
			segmentMethod.put(clazz, method);
		}
		
		return method;
		
	}

	private static Method getSegmentMethod(final Class<?> entityClass, final String segment) {
		try {
			return entityClass.getMethod("get" + segment.substring(0, 1).toUpperCase() + segment.substring(1));
		}
		catch (Exception e) {
			LOGGER.debug("No getter method found for " + segment + " attribute of class " + entityClass.getName());
			return null;
		}
	}

	private static <T> Map<String, Object> getEntityValueMapFromTemplates(final Object entity, final Map<String, Map<Class<?>,Method>> segments,
			final Map<String, Object> map, boolean throwExc) throws NoSuchMethodException {

		for (String segment : new ArrayList<String>(segments.keySet())) {
			Method method = addSegmentMethodIfNeeded(entity, segment , segments);
			map.put(segment, getTemplateValuesFromEntity(entity, method, segment, throwExc));
		}

		return map;
	}

	private static <T> Object getTemplateValuesFromEntity(final Object entity, final Method segmentMethod, final String segmentName, boolean throwExc) throws NoSuchMethodException {

		Object value = null;
		try {
			if (segmentMethod != null) {
				value = segmentMethod.invoke(entity);
			}
			else {
				value = "{" + segmentName + "}";
			}
		}
		catch (Exception e) {
			
			LOGGER.error("No method " + segmentMethod.getName() + " found for entity class " + entity.getClass());
			value = "{" + segmentName + "}";
			if(throwExc) {
				throw new NoSuchMethodException((String)value);
			}
		}
		return value;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> boolean allowedForEntity(final Object entity, final String name, final MappingInfo<?> mappingInfo,
			final HttpServletRequest request) {
		if (mappingInfo == null) {
			return false;
		}
		else if (mappingInfo.getAllowerMethod() == null) {
			return true;
		}
		else {
			try {
				return mappingInfo.getAllowerMethod().test(new LinkInfo(name, entity, request));
			}
			catch (Exception e) {
				LOGGER.error("Cannot call " + mappingInfo.getAllowerMethod());
				return false;
			}
		}
	}

}
