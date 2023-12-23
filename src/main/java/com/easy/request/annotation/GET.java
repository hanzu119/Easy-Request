package com.easy.request.annotation;

import com.easy.request.constant.EnumMethod;
import com.easy.request.constant.EnumResScheme;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
@Request(method = EnumMethod.GET)
public @interface GET {

    String path() default "";

    String value() default "";

    String responseCharset() default "";

    EnumResScheme responseScheme() default EnumResScheme.EMPTY;

    long timeout() default -1L;

    String contentType() default "";
}
