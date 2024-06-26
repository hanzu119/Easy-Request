package com.ionrhythm.easy.request.parse.request.apache;

import com.alibaba.fastjson.JSON;
import com.ionrhythm.easy.request.client.EasyClientRequest;
import com.ionrhythm.easy.request.parse.request.Resolver;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

public class ApacheJsonResolver implements Resolver {

    private static ApacheJsonResolver INSTANCE;

    private ApacheJsonResolver() {
    }

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
