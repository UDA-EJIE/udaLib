package com.ejie.x38.hdiv.controller.utils;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

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
import com.ejie.x38.hdiv.controller.model.UDALinkMappingInfo;
import com.hdivsecurity.services.affordance.MethodAwareLink;
import com.hdivsecurity.services.util.HdivHATEOASUtils;

public class UDASecureResourceProcesor {

	private static final Logger LOGGER = LoggerFactory.getLogger(UDASecureResourceProcesor.class);

	private static MethodLinkDiscoverer methodLinkDiscoverer;

	public static void registerMethodLinkDiscoverer(final MethodLinkDiscoverer methodLinkDiscoverer) {
		UDASecureResourceProcesor.methodLinkDiscoverer = methodLinkDiscoverer;
	}

	@SuppressWarnings("unchecked")
	public static <T> Resource<T> asResource(final T entity, final Class<?> controller) {
		List<Resource<T>> resources = asResources(Arrays.asList(entity), controller);
		if (resources != null) {
			return resources.get(0);
		}
		return null;
	}

	public static <T> List<Resource<T>> asResources(final List<T> entities, final Class<?> controller) {
		return processLinks(entities, controller, null);
	}

	public static <T> List<Resource<T>> processLinks(final List<T> entities, final Class<?> controller,
			final DinamicLinkProvider linkProvider) {

		HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.currentRequestAttributes()).getRequest();
		List<UDALinkMappingInfo> allowInfoList = methodLinkDiscoverer.getMethodLinkInfo(request);

		List<Resource<T>> resources = new ArrayList<Resource<T>>();

		if (allowInfoList != null) {
			String requestStr = getBaseUrl(request).toString();
			if (entities != null && entities.size() > 0) {
				resources = processEntities(entities, request, allowInfoList, requestStr);
			}
			if (linkProvider != null) {
				processStaticLinks(request, allowInfoList, requestStr, linkProvider);
			}
		}

		return resources;

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

	private static <T> List<Resource<T>> processEntities(final List<T> entities, final HttpServletRequest request,
			final List<UDALinkMappingInfo> allowInfoList, final String requestStr) {

		List<Resource<T>> resources = new ArrayList<Resource<T>>();

		if (entities != null && entities.size() > 0) {

			if (allowInfoList != null) {

				Map<String, Map<String, Method>> urlTemplatesMap = new HashMap<String, Map<String, Method>>();
				// add links to resources
				for (T entity : entities) {
					resources.addAll(processEntity(entity, request, allowInfoList, requestStr, urlTemplatesMap));
				}
			}

		}

		return resources;
	}

	private static <T> List<Resource<T>> processEntity(final T entity, final HttpServletRequest request,
			final List<UDALinkMappingInfo> allowInfoList, final String requestStr, final Map<String, Map<String, Method>> urlTemplatesMap) {

		List<Resource<T>> resources = new ArrayList<Resource<T>>();

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
			// Check allower for this certain entity
			entityLinks.addAll(getAllowedEntityLinks(entityToProcces, allowInfo, request, requestStr, urlTemplatesMap));
		}

		if (entity instanceof Resource) {
			((Resource<?>) entity).add(entityLinks);
		}
		else {
			resources.add(new Resource<T>(entity, entityLinks));
		}

		return resources;
	}

	private static List<Link> getAllowedEntityLinks(final Object entityToProcces, final UDALinkMappingInfo allowInfo,
			final HttpServletRequest request, final String requestStr, final Map<String, Map<String, Method>> urlTemplatesMap) {

		List<Link> entityLinks = new ArrayList<Link>();

		if (allowedForEntity(entityToProcces, allowInfo.getName(), allowInfo.getEntityMappingInfo(), request)) {
			// Dynamic links
			// Replace mapping with entity values
			for (String mapping : allowInfo.getEntityMappingInfo().getMappings()) {

				Map<String, Object> templateValuesMap = new HashMap<String, Object>();

				Map<String, Method> segments = urlTemplatesMap.get(mapping);
				UriComponents uriComponents = UriComponentsBuilder.fromUriString(mapping).build();
				if (segments == null) {
					segments = new HashMap<String, Method>();
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

							Method segmentMethod = getSegmentMethod(entityToProcces.getClass(), segment);
							templateValuesMap.put(segment, getTemplateValuesFromEntity(entityToProcces, segmentMethod, segment));

							segments.put(segment, segmentMethod);
						}
					}
					urlTemplatesMap.put(mapping, segments);
				}
				else {
					templateValuesMap = getEntityValueMapFromTemplates(entityToProcces, segments, templateValuesMap);
				}

				// Replace each segment by entity value;
				Link link = new Link(requestStr + uriComponents.expand(templateValuesMap).getPath(), allowInfo.getName());
				LOGGER.debug("Allowed link to entity: " + link);
				entityLinks.add(
						new MethodAwareLink(link,
								allowInfo.getMethodForLinkCondition()));
			}
			
			// Static links
			for (String mapping : allowInfo.getStaticMappingInfo().getMappings()) {
				Link link = new Link(requestStr + mapping, allowInfo.getName());
				LOGGER.debug("Allowed static link: " + link);
				entityLinks.add(
						new MethodAwareLink(link, allowInfo.getMethodForLinkCondition()));
			}
		}
		return entityLinks;

	}

	private static Method getSegmentMethod(final Class<?> entityClass, final String segment) {
		try {
			return entityClass.getMethod("get" + segment.substring(0, 1).toUpperCase() + segment.substring(1));
		}
		catch (Exception e) {
			LOGGER.error("No getter method found for " + segment + " attribute of class " + entityClass.getName());
			return null;
		}
	}

	private static <T> Map<String, Object> getEntityValueMapFromTemplates(final T entity, final Map<String, Method> segments,
			final Map<String, Object> map) {

		for (Entry<String, Method> segmentInfo : segments.entrySet()) {
			map.put(segmentInfo.getKey(), getTemplateValuesFromEntity(entity, segmentInfo.getValue(), segmentInfo.getKey()));
		}

		return map;
	}

	private static <T> Object getTemplateValuesFromEntity(final T entity, final Method segmentMethod, final String segmentName) {

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
			value = "{" + segmentName + "}";
			LOGGER.error("No method " + segmentMethod.getName() + " found for entity class " + entity.getClass());
		}
		return value;

	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private static <T> boolean allowedForEntity(final T entity, final String name, final MappingInfo<?> mappingInfo,
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
