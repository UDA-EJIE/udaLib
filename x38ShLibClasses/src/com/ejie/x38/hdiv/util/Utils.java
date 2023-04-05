package com.ejie.x38.hdiv.util;

import java.lang.annotation.Annotation;

public class Utils {
	
	public static Annotation findOneFromAnnotations(Annotation[] annotations, Class<?> clazz) {
		for(Annotation annotation : annotations) {
			if(annotation.annotationType() == clazz) {
				return annotation;
			}
		}
		return null;
	}
	
}
