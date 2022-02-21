package com.easy.request;

import java.lang.reflect.Proxy;

public class EasyRequestBuilder {

    public static <T> T build(Class<T> tClass) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass},
                new EasyInvocation(tClass, null, null, null, null, null, null));
    }

}
