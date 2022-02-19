package com.easy.request.annotation;

import com.easy.request.constant.EnumProtocol;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface HTTP {

    String host() default "";

    String value() default "";

    EnumProtocol protocol() default EnumProtocol.HTTPS;

    int port() default -1;
}
