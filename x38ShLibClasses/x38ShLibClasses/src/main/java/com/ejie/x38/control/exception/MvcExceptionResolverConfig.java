package com.ejie.x38.control.exception;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.HandlerExceptionResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurationSupport;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerAdapter;

import com.ejie.x38.control.exception.handler.MvcAccessDeniedExceptionHandler;
import com.ejie.x38.control.exception.handler.MvcExceptionHandler;
import com.ejie.x38.control.exception.handler.MvcValidationExceptionHandler;
import com.ejie.x38.validation.ValidationManager;

/**
 * 
 * Clase encargada del configurar el gestor general de excepciones (MvcExceptionResolver) para que 
 * redirija las excepciones no capturadas en el Controller a la clase MvcExceptionHandler
 * 
 * @author UDA
 *
 */
@Configuration
public class MvcExceptionResolverConfig extends WebMvcConfigurationSupport { 

	@Autowired
	private WebApplicationContext webApplicationContext;
	
	/**
	 * Lista de handlers definida por la aplicación
	 */
	 private List<Object> handlers = new ArrayList<Object>();
	 public void setHandlers(List<Object> handlers) {
		 this.handlers = handlers;
	 }
	 
	 /**
	  * Determina si debe aplicarse MvcAccessDeniedExceptionHandler
	  */
	private boolean disable_accessDenied;
	public void setDisable_accessDenied(boolean disable_accessDenied) {
		this.disable_accessDenied = disable_accessDenied;
	}


	/**
	 * Determina si debe aplicarse MvcValidationExceptionHandler 
	 */
	private boolean disable_validation;
	public void setDisable_validation(boolean disable_validation) {
		this.disable_validation = disable_validation;
	}
	

	 
	 
	/**
	 * Comprobar que se han definido correctamente las variables necesarias para el resolver:
	 */
	@PostConstruct
	public void postConstruct(){
		String war = webApplicationContext.getServletContext().getContextPath().substring(1);
		try {
			webApplicationContext.getBean("requestMappingHandlerAdapter");
		} catch (Exception e) {
			throw new IllegalStateException("No se puede crear el bean 'MvcExceptionResolverConfig' en el fichero mvc-config.xml del proyecto <"+war+">. Falta definir el bean 'requestMappingHandlerAdapter' de tipo 'RequestMappingHandlerAdapter'");
		}
		try {
			webApplicationContext.getBean("messageSource");
		} catch (Exception e) {
			throw new IllegalStateException("No se puede crear el bean 'MvcExceptionResolverConfig' en el fichero mvc-config.xml del proyecto <"+war+">. Falta definir el bean 'messageSource' de tipo 'ReloadableResourceBundleMessageSource'");
		}
		try {
			webApplicationContext.getBean("validationManager");
		} catch (Exception e) {
			throw new IllegalStateException("No se puede crear el bean 'MvcExceptionResolverConfig' en el fichero validation-config.xml del proyecto <"+war+">. Falta definir el bean 'validationManager' de tipo 'com.ejie.x38.validation.ValidationManager'");
		}
	}
	
	/**
	 * Se recuperan los messageConverters definidos para la aplicación y se asocian al MvcExceptionResolver
	 */
	 @Override
	 public void configureMessageConverters (List<HttpMessageConverter<?>> converters){
		 RequestMappingHandlerAdapter requestMethodHandlerAdapter = (RequestMappingHandlerAdapter)webApplicationContext.getBean(RequestMappingHandlerAdapter.class);
		 for (HttpMessageConverter<?> httpMessageConverter : requestMethodHandlerAdapter.getMessageConverters()) {
			 converters.add(httpMessageConverter);
		 }
	 }
	 
	/**
	 * Configura MvcExceptionHandler como un elemento para la resolución de excepciones
	 */
	@Override
	public void configureHandlerExceptionResolvers(List<HandlerExceptionResolver> exceptionResolvers) {
		MvcExceptionResolver customResolver = new MvcExceptionResolver();
		List<Object> exceptionHandlers = this.handlers;
		
		//Handlers de UDA
		if (!disable_validation){
			ValidationManager validationManager = (ValidationManager)webApplicationContext.getBean("validationManager");
			exceptionHandlers.add(0, new MvcValidationExceptionHandler(validationManager));
		}
		ReloadableResourceBundleMessageSource messageSource = (ReloadableResourceBundleMessageSource)webApplicationContext.getBean("messageSource");
		if (!disable_accessDenied){
			exceptionHandlers.add(0, new MvcAccessDeniedExceptionHandler(messageSource));
		}
		exceptionHandlers.add(new MvcExceptionHandler(messageSource));
		
		customResolver.setExceptionHandlers(exceptionHandlers);
		customResolver.setMessageConverters(getMessageConverters());
		customResolver.afterPropertiesSet();
		exceptionResolvers.add(customResolver);
	}

}