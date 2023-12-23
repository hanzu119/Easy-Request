package com.easy.request.parse.req.apache;

import com.alibaba.fastjson.JSON;
import com.easy.request.client.EasyClientRequest;
import com.easy.request.parse.req.Resolver;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class ApacheJsonResolver implements Resolver {

    private ApacheJsonResolver() {
    }

    private static ApacheJsonResolver INSTANCE;

    public static ApacheJsonResolver getInstance() {
        if (INSTANCE == null) {
            synchronized (ApacheJsonResolver.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApacheJsonResolver();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public StringEntity resolve(EasyClientRequest request, Object requestBody) {
        return new StringEntity(JSON.toJSONString(requestBody), ContentType.create(request.getContentType(), request.getRequestCharset()));
    }
}
