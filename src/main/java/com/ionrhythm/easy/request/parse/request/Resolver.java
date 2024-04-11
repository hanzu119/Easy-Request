package com.ionrhythm.easy.request.parse.request;

import com.ionrhythm.easy.request.client.EasyClientRequest;

public interface Resolver {

    /**
     *
     * @param request request
     * @param requestBody requestBody
     *
     * @return entity
     */
    Object resolve(EasyClientRequest request, Object requestBody);
}
