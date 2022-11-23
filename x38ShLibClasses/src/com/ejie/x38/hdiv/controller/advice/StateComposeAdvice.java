package com.ejie.x38.hdiv.controller.advice;

import java.lang.reflect.Field;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;

import org.hdiv.config.HDIVConfig;
import org.hdiv.dataComposer.IDataComposer;
import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.SecureIdentifiable;
import org.hdiv.util.HDIVUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.hateoas.Resource;
import org.springframework.http.MediaType;
import org.springframework.http.converter.json.MappingJacksonValue;
import org.springframework.http.server.ServerHttpRequest;
import org.springframework.http.server.ServerHttpResponse;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.servlet.mvc.method.annotation.AbstractMappingJacksonResponseBodyAdvice;

import com.ejie.x38.hdiv.datacomposer.EjieDataComposerMemory;
import com.ejie.x38.hdiv.util.Constants;
import com.ejie.x38.hdiv.util.IdentifiableFieldDiscoverer;
import com.ejie.x38.hdiv.util.ObfuscatorUtils;

@ControllerAdvice
public class StateComposeAdvice extends AbstractMappingJacksonResponseBodyAdvice {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(StateComposeAdvice.class);
	
	@Autowired
	private HDIVConfig config;
	
	private boolean isModifyRequest(HttpServletRequest request) {
		return request.getParameter(config.getModifyStateParameterName()) != null;
		
	}

	@SuppressWarnings({ "unchecked", "deprecation" })
	@Override
	protected void beforeBodyWriteInternal(final MappingJacksonValue bodyContainer, final MediaType contentType,
			final MethodParameter returnType, final ServerHttpRequest request, final ServerHttpResponse response) {

		ServletServerHttpRequest req = (ServletServerHttpRequest) request;
		HttpServletRequest origReq = req.getServletRequest();
		
		if (isModifyRequest(origReq)){
			
			IDataComposer dataComposer = HDIVUtil.getDataComposer(origReq);
			if (dataComposer == null && !(dataComposer instanceof EjieDataComposerMemory)) {
				// Url not processed by Hdiv Filter
				return;
			}
			
			String updateField = (String) origReq.getAttribute(Constants.MODIFY_HDIV_STATE_FORM_FIELD_NAME);
			if (!StringUtils.hasText(updateField)) {
				return;
			}
		
			if (bodyContainer.getValue() != null) {
				List<?> values = (List<?>) bodyContainer.getValue();
	
				Field field = null;
				
				boolean isFirst = true;
				for (Object option : values) {
					Object suggestInstance;
					Class<?> valueClass = null;
					try {
						
						if (option instanceof Resource<?>) {
							suggestInstance = ((Resource<?>)option).getContent();
						}else {
							suggestInstance = option;
						}
					
						Object value = null;
						if (!(suggestInstance instanceof SecureIdContainer)) {
							return;
						}
						if(suggestInstance instanceof SecureIdentifiable<?>) {
							value = ((SecureIdentifiable<?>)suggestInstance).getId();
							valueClass = suggestInstance.getClass();
						}else if(field == null) {
							Map<Class<?>,Field> secureFieldMap = IdentifiableFieldDiscoverer.getClassIdentifiableField((Class<SecureIdContainer>) suggestInstance.getClass());
							if(secureFieldMap != null) {
								if(secureFieldMap.size() == 1) {
									Entry<Class<?>,Field> entry = secureFieldMap.entrySet().iterator().next();
									field = entry.getValue();
									valueClass = entry.getKey();
								}else {
									field = secureFieldMap.get(suggestInstance.getClass());
									valueClass = suggestInstance.getClass();
								}
								value = field.get(suggestInstance);
							}
						}else {
							value = field.get(suggestInstance);
						}
						if(value != null) {
							if(isFirst) {
								((EjieDataComposerMemory)dataComposer).resetAndCompose(updateField, ObfuscatorUtils.obfuscate(String.valueOf(value), valueClass), false);
								isFirst = false;
							}else {
								dataComposer.compose(updateField, ObfuscatorUtils.obfuscate(String.valueOf(value), valueClass), false);
							}
						}
					}catch(Exception e) {
						LOGGER.error("Cannot add value from " + option + " to State", e);
					}
				}
			}	
		}
	}
}