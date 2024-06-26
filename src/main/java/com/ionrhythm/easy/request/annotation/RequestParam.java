package com.ionrhythm.easy.request.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.PARAMETER, ElementType.TYPE, ElementType.FIELD})
@Retention(RetentionPolicy.RUNTIME)
public @interface RequestParam {

    String[] name() default {};

    String value() default "";

    String[] fixedValue() default {};

    boolean isIgnore() default false;

    boolean ignoreSerialNumber() default true;

    boolean require() default false;

}
