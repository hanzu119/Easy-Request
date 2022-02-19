package com.easy.request.client;


import java.io.InputStream;

public interface EasyRequestClient {

    default InputStream post(EasyClientRequest request, Object requestEntity) {
        throw new RuntimeException("not config post method");
    }

    default InputStream put(EasyClientRequest request, Object requestEntity) {
        throw new RuntimeException("not config post method");
    }

    default InputStream delete(EasyClientRequest request) {
        throw new RuntimeException("not config delete method");
    }

    default InputStream get(EasyClientRequest request) {
        throw new RuntimeException("not config get method");
    }

    default void shutdown() {
        throw new RuntimeException("not config shutdown method.");
    }

}
