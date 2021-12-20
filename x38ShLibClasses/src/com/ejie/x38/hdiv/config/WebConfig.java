package com.ejie.x38.hdiv.config;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;
import org.springframework.web.bind.support.ConfigurableWebBindingInitializer;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.ejie.x38.control.method.annotation.RequestJsonBodyMethodArgumentResolver;
import com.ejie.x38.hdiv.interceptor.SecureModelAndViewInterceptor;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

	@Autowired
	private HttpMessageConverter<?> msgConverter;

	@Autowired
	private ConversionService conversionService;
	
	@Autowired
	private SecureModelAndViewInterceptor secureModelAndViewInterceptor;

	@Autowired
	@Qualifier("validator")
	private LocalValidatorFactoryBean validatorFactoryBean;

	@Override
	@Bean(name = "requestMappingHandlerAdapter")
	public RequestMappingHandlerAdapter requestMappingHandlerAdapter() {

		RequestMappingHandlerAdapter adapter = super.requestMappingHandlerAdapter();

		ConfigurableWebBindingInitializer webBindingInitializer = new ConfigurableWebBindingInitializer();
		webBindingInitializer.setConversionService(conversionService);
		webBindingInitializer.setValidator(validatorFactoryBean);
		adapter.setWebBindingInitializer(webBindingInitializer);

		List<HttpMessageConverter<?>> msgConverters = new ArrayList<HttpMessageConverter<?>>();
		msgConverters.add(msgConverter);
		adapter.setMessageConverters(msgConverters);

		List<HandlerMethodArgumentResolver> argumentResolvers = new ArrayList<HandlerMethodArgumentResolver>();
		argumentResolvers.add(new RequestJsonBodyMethodArgumentResolver());
		adapter.setCustomArgumentResolvers(argumentResolvers);

		return adapter;
	}

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
	    registry.addInterceptor(secureModelAndViewInterceptor);
	}
}
