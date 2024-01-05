package com.ejie.x38.control.method.annotation;

import java.util.List;

import javax.validation.Validator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import com.ejie.x38.control.bind.annotation.Json;

public class JsonMethodArgumentResolver implements HandlerMethodArgumentResolver {

    @Autowired
    private RequestMappingHandlerAdapter requestMappingHandlerAdapter;
    private RequestJsonBodyMethodProcessor requestFormEntityMethodProcessor = null;
    @Autowired
    private Validator validator;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {

    	Class<?> paramType = parameter.getParameterType();
		if (parameter.hasParameterAnnotation(Json.class)) {
			return true;
		}
		
		return false;
    }

    private RequestResponseBodyMethodProcessor getRequestResponseBodyMethodProcessor() {

        if (requestFormEntityMethodProcessor == null) {
            List<HttpMessageConverter<?>> messageConverters = requestMappingHandlerAdapter.getMessageConverters();
            requestFormEntityMethodProcessor = new RequestJsonBodyMethodProcessor(messageConverters);
        }
        return requestFormEntityMethodProcessor;
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
//    	MethodParameter methodParameter = new MethodParameter(parameter.getParameterAnnotation(RequestFormEntity.class).clazz().getDeclaredConstructor(),0);
    	
//    	Conventions.getVariableNameForParameter(parameter)
//    	webRequest.getParameterNames("usuario")
    	
//    	Object valueAux = getRequestResponseBodyMethodProcessor()
//                .resolveArgument(methodParameter, mavContainer, webRequest, binderFactory);
    	
        Object value = getRequestResponseBodyMethodProcessor()
                .resolveArgument(parameter, mavContainer, webRequest, binderFactory);
        return value;
    }
}