package com.ejie.x38.hdiv.filter;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpStatus;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.core.DefaultParameterNameDiscoverer;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerExecutionChain;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.ejie.hdiv.config.HDIVConfig;
import com.ejie.hdiv.config.multipart.IMultipartConfig;
import com.ejie.hdiv.context.RequestContextFactory;
import com.ejie.hdiv.context.RequestContextHolder;
import com.ejie.hdiv.exception.HDIVException;
import com.ejie.hdiv.filter.IValidationHelper;
import com.ejie.hdiv.filter.ValidationContext;
import com.ejie.hdiv.filter.ValidationContextFactory;
import com.ejie.hdiv.filter.ValidatorErrorHandler;
import com.ejie.hdiv.filter.ValidatorFilter;
import com.ejie.hdiv.init.RequestInitializer;
import com.ejie.hdiv.logs.IUserData;
import com.ejie.hdiv.logs.Logger;
import com.ejie.hdiv.services.NoEntity;
import com.ejie.hdiv.services.TrustAssertion;
import com.ejie.hdiv.util.HDIVUtil;
import com.ejie.x38.hdiv.protection.IdProtectionDataManager;
import com.ejie.x38.hdiv.util.ObfuscatorUtils;
import com.ejie.x38.hdiv.util.Utils;
import com.ejie.x38.hdiv.util.Method2;

public class EjieValidatorFilter extends ValidatorFilter {

	private static final org.slf4j.Logger LOGGER = LoggerFactory.getLogger(EjieValidatorFilter.class);
	
	private RequestMappingHandlerMapping requestMappingHandlerMapping;
	private DefaultParameterNameDiscoverer parameterNameDiscoverer = new DefaultParameterNameDiscoverer();
	private IdProtectionDataManager idProtectionDataManager;
	private RequestContextFactory requestContextFactory;
	
	@Override
	protected void initFilterBean() throws ServletException {
		super.initFilterBean();
		
		ApplicationContext context = HDIVUtil.findWebApplicationContext(getServletContext());
		requestMappingHandlerMapping = context.getBean(RequestMappingHandlerMapping.class);
		idProtectionDataManager = context.getBean(IdProtectionDataManager.class);
	}
	
	private void initDependencies(final HttpServletRequest request) {
		if (validationContextFactory == null) {
			synchronized (this) {
				if (hdivConfig == null) {
					ServletContext servletContext = getServletContext();
					ApplicationContext context = HDIVUtil.findWebApplicationContext(servletContext);

					hdivConfig = context.getBean(HDIVConfig.class);
					validationHelper = context.getBean(IValidationHelper.class);

					String[] names = context.getBeanNamesForType(IMultipartConfig.class);
					if (names.length > 1) {
						throw new HDIVException("More than one bean of type 'multipartConfig' is defined.");
					}
					if (names.length == 1) {
						multipartConfig = context.getBean(IMultipartConfig.class);
					}
					else {
						/**
						 * Final try
						 */
						try {
							List<IMultipartConfig> configs = HDIVUtil.findBeansInWebApplicationContext(IMultipartConfig.class);
							if (!configs.isEmpty()) {
								multipartConfig = configs.get(0);
							}
							else {
								// For applications without Multipart requests
								multipartConfig = null;
							}
						}
						catch (Exception e) {
							// TODO: handle exception
						}
					}
					requestContextFactory = context.getBean(RequestContextFactory.class);
					userData = context.getBean(IUserData.class);
					logger = context.getBean(Logger.class);
					errorHandler = context.getBean(ValidatorErrorHandler.class);
					requestInitializer = context.getBean(RequestInitializer.class);
					validationContextFactory = context.getBean(ValidationContextFactory.class);
					HDIVUtil.checkCustomImage(request);
				}
			}
		}
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
		initDependencies(request);
		RequestContextHolder ctx = requestContextFactory.create(requestInitializer, request, response, getServletContext());
		ValidationContext context = validationContextFactory.newInstance(ctx, validationHelper, hdivConfig.isUrlObfuscation());
		Map<String, String> deobfuscatedVariables = new HashMap<String, String>();
		if (!isStartPage(ctx, context.getTarget())) {
			deobfuscatedVariables = checkPathVariables(request, response);			
		}
		String mapping = getForwardMapping(request, response, deobfuscatedVariables);
		EjieRequestWrapper wRequest = new EjieRequestWrapper(request);
		super.doFilterInternal(wRequest, response, new EjieFilterChain(filterChain).setMapping(mapping));
		wRequest.cleanup();
	}
	
	private HttpServletRequest wrapRequest(HttpServletRequest request) {
		return new DeobfuscatorRequest(request);
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
					String[] parameterNames = parameterNameDiscoverer.getParameterNames(method);
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
	
	/**
	 * Check if the current request is a start page.
	 *
	 * @param request HttpServletRequest to validate
	 * @param target Part of the url that represents the target action
	 * @return true if it is a start page
	 */
	protected boolean isStartPage(final RequestContextHolder request, final String target) {
		return hdivConfig.isStartPage(target, Method2.secureValueOf(request.getMethod()));
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
