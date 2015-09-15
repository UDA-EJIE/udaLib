package com.ejie.x38.control.method.annotation;

import java.io.IOException;
import java.lang.annotation.Annotation;
import java.util.List;

import org.codehaus.jackson.JsonNode;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.Conventions;
import org.springframework.core.MethodParameter;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJacksonHttpMessageConverter;
import org.springframework.http.server.ServletServerHttpRequest;
import org.springframework.validation.BindingResult;
import org.springframework.web.HttpMediaTypeNotAcceptableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.ModelAndViewContainer;
import org.springframework.web.servlet.mvc.method.annotation.RequestResponseBodyMethodProcessor;

import com.ejie.x38.control.bind.annotation.RequestJsonBody;
import com.ejie.x38.serialization.UdaMappingJacksonHttpMessageConverter;

public class RequestJsonBodyMethodProcessor extends RequestResponseBodyMethodProcessor {

	@Autowired
	private UdaMappingJacksonHttpMessageConverter udaMappingJacksonHttpMessageConverter;
	
	public RequestJsonBodyMethodProcessor(
			List<HttpMessageConverter<?>> messageConverters) {
		super(messageConverters);
		// TODO Auto-generated constructor stub
	}

	@Override
	public boolean supportsParameter(MethodParameter parameter) {
		return parameter.hasParameterAnnotation(RequestJsonBody.class);
	}

	@Override
	public boolean supportsReturnType(MethodParameter returnType) {
		return false;
	}

	private MappingJacksonHttpMessageConverter getMappingJacksonHttpMessageConverter(){
		
		for (HttpMessageConverter httpMessageConverter :this.messageConverters){
			if (httpMessageConverter instanceof MappingJacksonHttpMessageConverter){
				return (MappingJacksonHttpMessageConverter)httpMessageConverter;
			}
		}
		
		/*
		 * FIXME : Arreglar
		 */
		throw new RuntimeException(" ERROR ");
	}
	
	@Override
	public Object resolveArgument(MethodParameter parameter,
			ModelAndViewContainer mavContainer, NativeWebRequest webRequest,
			WebDataBinderFactory binderFactory) throws Exception {
		ServletServerHttpRequest createInputMessage = this.createInputMessage(webRequest);
		
		JsonNode readTree;
		if (createInputMessage.getServletRequest().getAttribute("readTree")==null){
			readTree = this.getMappingJacksonHttpMessageConverter().getObjectMapper().readTree(createInputMessage.getBody());
			createInputMessage.getServletRequest().setAttribute("readTree", readTree);
		}else{
			readTree = (JsonNode)createInputMessage.getServletRequest().getAttribute("readTree");
		}
		
		String param = parameter.getParameterAnnotation(RequestJsonBody.class).param();
		
		JsonNode baseNode = param.equals("param")?readTree:readTree.get(param);
		
		Object arg = this.getMappingJacksonHttpMessageConverter().getObjectMapper().readValue(baseNode, parameter.getParameterType());
		
        Annotation annotations[] = parameter.getParameterAnnotations();
        Annotation aannotation[];
        int j = (aannotation = annotations).length;
        for(int i = 0; i < j; i++)
        {
            Annotation annot = aannotation[i];
            if(annot.annotationType().getSimpleName().startsWith("Valid"))
            {
                String name = Conventions.getVariableNameForParameter(parameter);
                WebDataBinder binder = binderFactory.createBinder(webRequest, arg, name);
                Object hints = AnnotationUtils.getValue(annot);
                binder.validate((hints instanceof Object[]) ? (Object[])hints : (new Object[] {
                    hints
                }));
                BindingResult bindingResult = binder.getBindingResult();
                if(bindingResult.hasErrors())
                    throw new MethodArgumentNotValidException(parameter, bindingResult);
            }
        }

        return arg;
	}

	@Override
	public void handleReturnValue(Object returnValue,
			MethodParameter returnType, ModelAndViewContainer mavContainer,
			NativeWebRequest webRequest) throws IOException,
			HttpMediaTypeNotAcceptableException {
		
		super.handleReturnValue(returnValue, returnType, mavContainer, webRequest);
	}
	

}
