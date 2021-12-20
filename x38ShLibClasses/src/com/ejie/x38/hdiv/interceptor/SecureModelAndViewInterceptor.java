package com.ejie.x38.hdiv.interceptor;

import java.lang.reflect.Field;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.hdiv.services.LinkProvider;
import org.hdiv.services.SecureIdContainer;
import org.hdiv.services.SecureIdentifiable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

import com.ejie.x38.hdiv.aspect.LinkResourcesAspect;
import com.ejie.x38.hdiv.processor.EncriptorResponseLinkProcessor;

@Component
public class SecureModelAndViewInterceptor extends HandlerInterceptorAdapter {

	private static final Logger LOGGER = LoggerFactory.getLogger(SecureModelAndViewInterceptor.class);

	@Autowired
	private LinkProvider linkProvider;
	
	@Autowired
	private EncriptorResponseLinkProcessor responseLinkProcessor;
	
	public void postHandle(
		HttpServletRequest request, HttpServletResponse response, 
		Object handler, ModelAndView modelAndView)
		throws Exception {
	
		for(Entry<String, Object > modelObject : modelAndView.getModel().entrySet()) {
			
			try {
				modelAndView.addObject(modelObject.getKey(), responseLinkProcessor.checkResponseToLinks(modelObject.getValue(), handler.getClass(), linkProvider));
			}
			catch (Throwable e) {
				LOGGER.error("Error processing links of object {} with exception:", modelObject.getKey(), e);
			}
		}
	}
}
