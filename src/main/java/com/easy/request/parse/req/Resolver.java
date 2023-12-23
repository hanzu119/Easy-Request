package com.easy.request.parse.req;

import com.easy.request.client.EasyClientRequest;

public interface Resolver {

    /**
     * 请求数据的处理方法
     *
     * @param request
     * @param requestBody
     *
     * @return
     */
    Object resolve(EasyClientRequest request, Object requestBody);
}
