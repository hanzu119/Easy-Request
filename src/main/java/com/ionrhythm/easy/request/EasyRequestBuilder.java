package com.ionrhythm.easy.request;

import com.ionrhythm.easy.request.client.EasyRequestClient;

import java.lang.reflect.Proxy;

public class EasyRequestBuilder {

    public static <T> T build(Class<T> tClass) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass},
                new EasyRequestInvocation(tClass, null, null, null, null, null, null));
    }

    public static <T> T build(Class<T> tClass, EasyRequestClient client) {
        return (T) Proxy.newProxyInstance(tClass.getClassLoader(), new Class[]{tClass},
                new EasyRequestInvocation(tClass, client, null, null, null, null, null));
    }

}
