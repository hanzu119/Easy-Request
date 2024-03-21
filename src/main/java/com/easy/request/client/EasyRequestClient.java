package com.easy.request.client;


import com.easy.request.model.EasyResponse;

import java.io.InputStream;

public interface EasyRequestClient {

    default EasyResponse<InputStream> post(EasyClientRequest request, Object requestEntity) {
        throw new RuntimeException("not config post method");
    }

    default EasyResponse<InputStream> put(EasyClientRequest request, Object requestEntity) {
        throw new RuntimeException("not config post method");
    }

    default EasyResponse<InputStream> delete(EasyClientRequest request) {
        throw new RuntimeException("not config delete method");
    }

    default EasyResponse<InputStream> get(EasyClientRequest request) {
        throw new RuntimeException("not config get method");
    }

    /**
     * shutdown client
     */
    default void shutdown() {
        throw new RuntimeException("not config shutdown method.");
    }

}
