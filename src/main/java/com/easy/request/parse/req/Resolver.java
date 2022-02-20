package com.easy.request.parse.req;

import com.easy.request.client.EasyClientRequest;

public interface Resolver {

    Object resolve(EasyClientRequest request, Object requestBody);
}
