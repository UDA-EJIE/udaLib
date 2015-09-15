/*
* Copyright 2011 E.J.I.E., S.A.
*
* Licencia con arreglo a la EUPL, Versión 1.1 exclusivamente (la «Licencia»);
* Solo podrá usarse esta obra si se respeta la Licencia.
* Puede obtenerse una copia de la Licencia en
*
* http://ec.europa.eu/idabc/eupl.html
*
* Salvo cuando lo exija la legislación aplicable o se acuerde por escrito,
* el programa distribuido con arreglo a la Licencia se distribuye «TAL CUAL»,
* SIN GARANTÍAS NI CONDICIONES DE NINGÚN TIPO, ni expresas ni implícitas.
* Véase la Licencia en el idioma concreto que rige los permisos y limitaciones
* que establece la Licencia.
*/
package com.ejie.x38.validation;

import java.io.IOException;
import java.io.StringWriter;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.annotation.Resource;
import javax.validation.ConstraintViolation;
import javax.validation.Validation;
import javax.validation.Validator;
import javax.validation.ValidatorFactory;

import org.codehaus.jackson.JsonGenerator;
import org.codehaus.jackson.map.MappingJsonFactory;
import org.codehaus.jackson.map.ObjectMapper;
import org.hibernate.validator.HibernateValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.NoSuchMessageException;
import org.springframework.context.support.ReloadableResourceBundleMessageSource;
import org.springframework.util.StringUtils;

import com.ejie.x38.util.ObjectConversionManager;
import com.ejie.x38.util.StackTraceManager;
import com.ejie.x38.util.StaticsContainer;

/**
 * 
 * @author UDA
 *
 */
public class ValidationManager {

	@Resource
	ReloadableResourceBundleMessageSource messageSource;
	
	private static final long serialVersionUID = 1L;
	private final static Logger logger =  LoggerFactory.getLogger(ValidationManager.class);
	private ValidatorFactory validatorFactory;
	private Validator validator;
	private MappingJsonFactory jsonFactory;
	
	@PostConstruct
	public void init() {
		validatorFactory = Validation.byProvider(HibernateValidator.class).configure().buildValidatorFactory();
		validator = validatorFactory.getValidator();
		jsonFactory = new MappingJsonFactory();
		messageSource.setFallbackToSystemLocale(false);
	}
	
	public String validateObject(String bean, String data, Locale locale){
		try{
			Class<?> clazz = Class.forName(StaticsContainer.modelPackageName+bean);
			ObjectMapper mapper = new ObjectMapper();
			Object instance = mapper.readValue(data, clazz);					
	
			Set<ConstraintViolation<Object>> constraintViolations = validator.validate(instance);
			return summary(constraintViolations, bean, locale);
		}catch (Exception e) {
			logger.error(StackTraceManager.getStackTrace(e));
			return "error!";
		}
	}
	
	public String validateProperty(String bean, String property, String value, Locale locale){
		try{
			
			String capitalicedProperty = StringUtils.capitalize(property);
			String capitalicedBean = StringUtils.capitalize(bean);
	
			Class<?> clazz = Class.forName(StaticsContainer.modelPackageName+capitalicedBean);
			Constructor<?> cons = clazz.getConstructor();
			Object obj = cons.newInstance((Object[])null);

			Method getter = clazz.getMethod("get" + capitalicedProperty, new Class[]{});
			Method meth = clazz.getMethod("set" + capitalicedProperty, getter.getReturnType());
			Object res = ObjectConversionManager.convert(value, getter.getReturnType());
			meth.invoke(obj, res);			
			
			Set<ConstraintViolation<Object>> constraintViolations = validator.validateProperty(obj, property);			
			return summary(constraintViolations, bean, locale);
		}catch (Exception e) {
			logger.error(StackTraceManager.getStackTrace(e));
			return "error!";
		}
	}
	
	private String summary (Set<ConstraintViolation<Object>> constraintViolations, String bean, Locale locale){
		Iterator<ConstraintViolation<Object>> ite = constraintViolations.iterator();
		Map<String,List<Map<String,String>>> errors = new HashMap<String,List<Map<String,String>>>();
		String propertyKey ="";
		List<Map<String,String>> propertyErrors;
		while (ite.hasNext()) {
			ConstraintViolation<Object> constraintViolation = ite.next();
			propertyKey = constraintViolation.getPropertyPath()+"";
			if(errors.containsKey(propertyKey)){
				propertyErrors = errors.get(propertyKey);				
			}else{
				propertyErrors = new ArrayList<Map<String,String>>();
			}
			Map<String,String> node = new HashMap<String,String>();
			String interpolatedMessage;
			//Try to get the interpolated Message in this order: 1- War, 2- EAR, 3- Hibernate's Default 
			try{
				interpolatedMessage = messageSource.getMessage(constraintViolation.getMessage(), null, locale);
			}catch(NoSuchMessageException e){
				interpolatedMessage = constraintViolation.getMessage();
			}			
			if(interpolatedMessage!= null && !interpolatedMessage.equals("")){
				node.put(constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(), interpolatedMessage);
			}else{
				node.put(constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName(), "message not found");
				logger.error("Validation message for key "+constraintViolation.getConstraintDescriptor().getAnnotation().annotationType().getSimpleName()+" not found");
			}
			
			propertyErrors.add(node);
			errors.put(propertyKey, propertyErrors);
		}
		if (!errors.isEmpty()){				
			Map<String, String> title = new HashMap<String, String>();
			title.put("key", bean);
			String header = serialize(title);
			String body = serialize(errors);
			String[] result = {header, body};
			String summary = serialize(result);
			return summary;
		}else{
    		return null;		
		}
	}
	
	private String serialize (Object result){
		StringWriter sw;
		try{
	      sw = new StringWriter();
	      ObjectMapper mapper = new ObjectMapper(); 
	      JsonGenerator jsonGenerator = jsonFactory.createJsonGenerator(sw);
	      mapper.writeValue(jsonGenerator, result);
	      sw.close();
		} catch (IOException e) {
			logger.error(StackTraceManager.getStackTrace(e));
			return "error!";
		}
		return sw.getBuffer().toString();
	}
	
	//Getters & Setters
	public void setMessageSource(
			ReloadableResourceBundleMessageSource messageSource) {
		this.messageSource = messageSource;
	}
}