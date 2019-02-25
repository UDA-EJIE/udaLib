package com.ejie.x38.upload;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.multipart.MultipartResolver;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.EnableWebMvc;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;

import com.ejie.x38.control.exception.MvcExceptionResolver;
import com.ejie.x38.control.exception.handler.MvcAccessDeniedExceptionHandler;
import com.ejie.x38.control.exception.handler.MvcExceptionHandler;
import com.ejie.x38.control.exception.handler.MvcValidationExceptionHandler;
import com.ejie.x38.util.UdaMultipartResolver;
import com.ejie.x38.validation.ValidationManager;

/**
 * @author Petri Kainulainen
 */
@Configuration
@EnableWebMvc
//@ComponentScan(basePackages = {
//        "net.petrikainulainen.spring.testmvc.common.controller",
//        "net.petrikainulainen.spring.testmvc.todo.controller",
//        "net.petrikainulainen.spring.testmvc.todo.service"
//})
@ImportResource({"classpath:test-config.xml" })
//@PropertySource("classpath:application.properties")
public class ExampleApplicationContext extends WebMvcConfigurationSupport {

    private static final String MESSAGE_SOURCE_BASE_NAME = "i18n/messages";

    private static final String VIEW_RESOLVER_PREFIX = "/WEB-INF/jsp/";
    private static final String VIEW_RESOLVER_SUFFIX = ".jsp";
    
    
    @Autowired
	private WebApplicationContext webApplicationContext;
    
    private List<Object> handlers = new ArrayList<Object>();

	@Bean
    public MultipartResolver multipartResolver() {
    	UdaMultipartResolver multipartResolver = new UdaMultipartResolver();
    	multipartResolver.setMaxUploadSize(100);
        return multipartResolver;
    }

	
	

	@Override
	protected void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		// TODO Auto-generated method stub
		
		
		MvcExceptionResolver customResolver = new MvcExceptionResolver();
		List<Object> exceptionHandlers = this.handlers;
		
		//Handlers de UDA
//		if (!disable_validation){
			ValidationManager validationManager = (ValidationManager)webApplicationContext.getBean("validationManager");
			exceptionHandlers.add(0, new MvcValidationExceptionHandler(validationManager));
//		}
		ReloadableResourceBundleMessageSource messageSource = (ReloadableResourceBundleMessageSource)webApplicationContext.getBean("messageSource");
//		if (!disable_accessDenied){
			exceptionHandlers.add(0, new MvcAccessDeniedExceptionHandler(messageSource));
//		}
		exceptionHandlers.add(new MvcExceptionHandler(messageSource));
		
		customResolver.setExceptionHandlers(exceptionHandlers);
		customResolver.setMessageConverters(getMessageConverters());
		customResolver.afterPropertiesSet();
		exceptionResolvers.add(customResolver);
		
		
		super.configureHandlerExceptionResolvers(exceptionResolvers);
		
		
		
		
		
	}
	
	

//    @Override
//    public void addResourceHandlers(ResourceHandlerRegistry registry) {
//        registry.addResourceHandler("/static/**").addResourceLocations("/static/");
//    }

//    @Override
//    public void configureDefaultServletHandling(DefaultServletHandlerConfigurer configurer) {
//    	
////        configurer.enable();
//    }
//
//    @Bean
//    public MessageSource messageSource() {
//        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
//
//        messageSource.setBasename(MESSAGE_SOURCE_BASE_NAME);
//        messageSource.setUseCodeAsDefaultMessage(true);
//
//        return messageSource;
//    }
//
//    @Bean
//    public SimpleMappingExceptionResolver exceptionResolver() {
//        SimpleMappingExceptionResolver exceptionResolver = new SimpleMappingExceptionResolver();
//
//        Properties exceptionMappings = new Properties();
//
//        exceptionMappings.put("net.petrikainulainen.spring.testmvc.todo.exception.TodoNotFoundException", "error/404");
//        exceptionMappings.put("java.lang.Exception", "error/error");
//        exceptionMappings.put("java.lang.RuntimeException", "error/error");
//
//        exceptionResolver.setExceptionMappings(exceptionMappings);
//
//        Properties statusCodes = new Properties();
//
//        statusCodes.put("error/404", "404");
//        statusCodes.put("error/error", "500");
//
//        exceptionResolver.setStatusCodes(statusCodes);
//
//        return exceptionResolver;
//    }
//
//    @Bean
//    public ViewResolver viewResolver() {
//        InternalResourceViewResolver viewResolver = new InternalResourceViewResolver();
//
//        viewResolver.setViewClass(JstlView.class);
//        viewResolver.setPrefix(VIEW_RESOLVER_PREFIX);
//        viewResolver.setSuffix(VIEW_RESOLVER_SUFFIX);
//
//        return viewResolver;
//    }
    
    
    
}
