package com.ejie.x38.hdiv.interceptor;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hdiv.services.LinkProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ejie.x38.hdiv.controller.model.IdentifiableModelWrapper;
import com.ejie.x38.hdiv.processor.EncriptorResponseLinkProcessor;

@Component
public class SecureModelAndViewInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecureModelAndViewInterceptor.class);

	@Autowired
	@Lazy
	private LinkProvider<?> linkProvider;
	
	@Autowired
	private EncriptorResponseLinkProcessor responseLinkProcessor;
	
	public void postHandle(
		HttpServletRequest request, HttpServletResponse response, 
		Object handler, ModelAndView modelAndView)
		throws Exception {
		
		if(modelAndView ==null || modelAndView.getModel() == null) {
			return;
		}
	
		for(Entry<String, Object > modelObject : modelAndView.getModel().entrySet()) {
			
			try {
				Object value = modelObject.getValue();
				if(value instanceof IdentifiableModelWrapper || value instanceof Iterable || value instanceof Map) {
					modelAndView.addObject(modelObject.getKey(), responseLinkProcessor.checkResponseToLinks(modelObject.getValue(), handler.getClass(), linkProvider));
				}
			}
			catch (Throwable e) {
				LOGGER.error("Error processing links of object {} with exception:", modelObject.getKey(), e);
			}
		}
	}
	
}
