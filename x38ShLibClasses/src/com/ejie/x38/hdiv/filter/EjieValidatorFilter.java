package com.ejie.x38.hdiv.filter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.hdiv.filter.ValidatorFilter;
import org.hdiv.services.NoEntity;
import org.hdiv.services.TrustAssertion;
import org.hdiv.util.HDIVUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ejie.x38.hdiv.protection.IdProtectionDataManager;
import com.ejie.x38.hdiv.util.ObfuscatorUtils;
import com.ejie.x38.hdiv.util.Utils;

public class EjieValidatorFilter extends ValidatorFilter {

	private static final Logger LOGGER = LoggerFactory.getLogger(EjieValidatorFilter.class);
	
	private RequestMappingHandlerMapping requestMappingHandlerMapping;
	private DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
	private IdProtectionDataManager idProtectionDataManager;
	
	@Override
	protected void initFilterBean() throws ServletException {
		super.initFilterBean();
		
		ApplicationContext context = HDIVUtil.findWebApplicationContext(getServletContext());
		requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
		idProtectionDataManager = context.getBean(IdProtectionDataManager.class);

	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		
		Map<String, String> deobfuscatedVariables = checkPathVariables(request, response);
		String mapping = getForwardMapping(request, response, deobfuscatedVariables);
		super.doFilterInternal(request, response, new EjieFilterChain(filterChain).setMapping(mapping));	
	}
	
	private HttpServletRequest wrapRequest(HttpServletRequest request) {
		if(isFormRequest(request.getContentType())) {
			return new DeobfuscatorRequest(request);
		}
		return request;
	}
	
	private boolean isFormRequest(String contentType) {
		MediaType requestContentType = contentType == null ? null : MediaType.valueOf(contentType);
		return MediaType.APPLICATION_FORM_URLENCODED.isCompatibleWith(requestContentType) || MediaType.MULTIPART_FORM_DATA.isCompatibleWith(requestContentType) ;
	}
	
	@SuppressWarnings("unchecked")
	private Map<String, String> checkPathVariables(HttpServletRequest request, HttpServletResponse response) {
		
		Map<String, String> deobfuscatedVariables = new HashMap<String, String>();
		try {
			HandlerExecutionChain handlerChain = requestMappingHandlerMapping.getHandler(request);
			if (handlerChain != null) {
				
				Method method = ((HandlerMethod) handlerChain.getHandler()).getMethod();
				
				Map<String, String> uriTemplateVars = (Map<String, String>) request.getAttribute(HandlerMapping.URI_TEMPLATE_VARIABLES_ATTRIBUTE);
				Annotation [][] annotations = method.getParameterAnnotations();
				
				for(Entry<String, String> pathValue : uriTemplateVars.entrySet()) {
					String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);;
					if(parameterNames != null) {
						for (int i = 0; i<parameterNames.length; i++) {
							String param = parameterNames[i];	
							PathVariable pathAnnotation = (PathVariable) Utils.findOneFromAnnotations(annotations[i], PathVariable.class);
							if(pathAnnotation != null && (pathValue.getKey().equals( param) || pathValue.getKey().equals(pathAnnotation.value()) )) {
								TrustAssertion trustAssertion = (TrustAssertion) Utils.findOneFromAnnotations(annotations[i], TrustAssertion.class);
								if(trustAssertion == null) {
									//Throw exception
									response.setStatus(HttpStatus.SC_UNAUTHORIZED);
								}else {
									//Check obfuscated value type and param type match
									Class<?> pathVariableClass = trustAssertion.idFor();
									if(pathVariableClass != null  && ObfuscatorUtils.getClass(pathValue.getValue()) == pathVariableClass) {
										deobfuscatedVariables.put(pathValue.getValue(), ObfuscatorUtils.deobfuscate(pathValue.getValue()));
									}else if(pathVariableClass != NoEntity.class){
										//Throw exception
										response.setStatus(HttpStatus.SC_UNAUTHORIZED);
									}
								}
							}
						}
					}
				}
			}
		}
		catch (Exception e) {
			LOGGER.error("Exception processing path variables. ", e);
		}
		return deobfuscatedVariables;
	}
	
	private String getForwardMapping(HttpServletRequest request, HttpServletResponse response, Map<String, String> deobfuscatedVariables) throws ServletException, IOException {
		String mapping = null;
		if(!deobfuscatedVariables.isEmpty()) {
			String currentUri = request.getRequestURI();
			mapping = getDeobfuscatedMapping(deobfuscatedVariables, currentUri.substring(request.getContextPath().length()));
			idProtectionDataManager.remapAction(request.getContextPath() + mapping, currentUri);
		}
		return mapping;
	}
	
	private String getDeobfuscatedMapping(Map<String, String> deobfuscatedVariables, String mapping) {
		String deobfuscatedMapping = mapping;
		for(Entry<String, String> variable : deobfuscatedVariables.entrySet()) {
			deobfuscatedMapping = deobfuscatedMapping.replace(variable.getKey(), variable.getValue());
		}
		return deobfuscatedMapping;
	}
	
	public class EjieFilterChain implements javax.servlet.FilterChain{
		
		private final FilterChain filterChain;
		
		private String mapping = null;
		
		public EjieFilterChain(FilterChain filterChain) {
			this.filterChain = filterChain;
		}
		
		public EjieFilterChain setMapping(String mapping) {
			this.mapping = mapping;
			return this;
		}

		@Override
		public void doFilter(ServletRequest request, ServletResponse response) throws IOException, ServletException {
			if(mapping != null) {
				request.getRequestDispatcher(mapping).forward(request, response);
				mapping = null;
			}else {
				filterChain.doFilter(wrapRequest((HttpServletRequest)request), response);
			}
		}
		
	}
	
}
