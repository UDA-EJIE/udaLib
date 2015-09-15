package com.ejie.x38.control.bind.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.http.MediaType;

@Target(ElementType.PARAMETER)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface RequestJsonBody {

	String param() default "param";
	
	Class<?> clazz() default Object.class;
	
	String value() default "args";
	
	String contentType() default MediaType.APPLICATION_JSON_VALUE;

	boolean required() default true;	
}
