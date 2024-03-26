package com.ionrhythm.easy.request;

import com.ionrhythm.easy.request.client.EasyClientRequest;

public interface Interceptor {

    default void beforeRequest(EasyClientRequest request, Object requestEntity) {
    }

    default void onReceive(EasyClientRequest request, Object responseEntity) {
    }

}
