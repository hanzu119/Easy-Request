package com.easy.request.annotation;

import com.easy.request.constant.EnumReqScheme;
import com.easy.request.constant.EnumResScheme;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 *
 */
@Target({ElementType.METHOD})
@Retention(RetentionPolicy.RUNTIME)
public @interface DELETE {

    /**
     * 请求路径
     */
    String path() default "";

    /**
     * @see #path()
     */
    String value() default "";

    /**
     * 请求体格式
     */
    EnumReqScheme reqScheme() default EnumReqScheme.EMPTY;

    /**
     * 解析响应数据字符集
     */
    String responseCharset() default "";

    /**
     * 响应数据格式
     */
    EnumResScheme responseScheme() default EnumResScheme.EMPTY;

    /**
     * 请求超市时间
     */
    long timeout() default -1L;

    /**
     * contentType
     */
    String contentType() default "";

}
