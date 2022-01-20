package com.ejie.x38.hdiv.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.ejie.x38.hdiv.interceptor.SecureModelAndViewInterceptor;

@Configuration
public class WebConfig extends WebMvcConfigurationSupport {

	@Autowired
	private SecureModelAndViewInterceptor secureModelAndViewInterceptor;	

	@Override
	public void addInterceptors(InterceptorRegistry registry) {
		registry.addInterceptor(secureModelAndViewInterceptor);
	}
}
