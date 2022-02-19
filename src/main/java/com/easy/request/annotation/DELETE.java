package com.easy.request.annotation;

import com.easy.request.constant.EnumReqScheme;
import com.easy.request.constant.EnumResScheme;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DELETE {

    String path() default "";

    String value() default "";

    EnumReqScheme reqScheme() default EnumReqScheme.EMPTY;

    String responseCharset() default "";

    EnumResScheme responseScheme() default EnumResScheme.EMPTY;

    long timeout() default -1L;

    String contentType() default "";

}
