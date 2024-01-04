package com.ejie.x38.control.bind.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ejie.x38.json.JsonMixin;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface Json {

    /**
     * A list of Jackson Mixins.
     * <p>
     * {@link http://wiki.fasterxml.com/JacksonMixInAnnotations}
     */
    JsonMixin[] mixins() default {};

}