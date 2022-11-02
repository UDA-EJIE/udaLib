package com.ejie.x38.hdiv.util;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

public class Utils {
	
	public static Annotation findOneFromAnnotations(Annotation[] annotations, Class<?> clazz) {
		for(Annotation annotation : annotations) {
			if(annotation.annotationType() == clazz) {
				return annotation;
			}
		}
		return null;
	}
	
	public static Parameter findAnnotatedField(Method method, Class<?> clazz) {
		
		Annotation[][] anotations = method.getParameterAnnotations();
		for (int i = 0; i < anotations.length; i++) {
			for(Annotation annotation : anotations[i]) {
				if(annotation.annotationType() == clazz) {
					return method.getParameters()[i];
				}
			}
		}
		return null;
	}
	
}
