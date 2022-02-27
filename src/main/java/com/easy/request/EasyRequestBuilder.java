package com.easy.request;

import com.easy.request.client.EasyRequestClient;

import java.lang.reflect.Proxy;

public class EasyRequestBuilder {

    public static <T> T build(Class<T> tClass) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass},
                new EasyInvocation(tClass, null, null, null, null, null, null));
    }

    public static <T> T build(Class<T> tClass, EasyRequestClient client) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass},
                new EasyInvocation(tClass, client, null, null, null, null, null));
    }

}
