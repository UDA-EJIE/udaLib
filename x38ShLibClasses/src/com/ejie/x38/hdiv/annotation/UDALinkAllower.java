package com.ejie.x38.hdiv.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ ElementType.METHOD })
@Retention(RetentionPolicy.RUNTIME)
public @interface UDALinkAllower {
	String name();

	Class<?> allower() default Void.class;

	Class<?> linkClass() default Void.class;
}
