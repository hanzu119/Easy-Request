package com.easy.request.parse.req;

import com.easy.request.client.EasyClientRequest;

public interface Resolver {

    Object resolver(EasyClientRequest request, Object requestBody);
}
