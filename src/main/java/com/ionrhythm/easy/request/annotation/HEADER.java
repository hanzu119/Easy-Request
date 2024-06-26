package com.ionrhythm.easy.request.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.PARAMETER, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface HEADER {

    String name() default "";

    String value() default "";

    boolean isIgnore() default false;

    boolean ignoreSerialNumber() default true;
}
