package com.easy.request.annotation;

import com.easy.request.constant.EnumMethod;
import com.easy.request.constant.EnumReqScheme;
import com.easy.request.constant.EnumResScheme;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD, ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
public @interface EasyRequest {

    String path() default "";

    String value() default "";

    EnumMethod method() default EnumMethod.GET;

    EnumReqScheme requestScheme() default EnumReqScheme.EMPTY;

    String requestCharset() default "";

    EnumResScheme responseScheme() default EnumResScheme.EMPTY;

    String responseCharset() default "";

    long timeout() default -1L;

    String contentType() default "";

}
