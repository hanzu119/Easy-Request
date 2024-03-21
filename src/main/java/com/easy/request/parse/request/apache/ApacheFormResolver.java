package com.easy.request.parse.request.apache;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.easy.request.client.EasyClientRequest;
import com.easy.request.parse.request.Resolver;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.message.BasicNameValuePair;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

public class ApacheFormResolver implements Resolver {

    private ApacheFormResolver() {
    }

    private static ApacheFormResolver INSTANCE;

    public static ApacheFormResolver getInstance() {
        if (INSTANCE == null) {
            synchronized (ApacheFormResolver.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApacheFormResolver();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public UrlEncodedFormEntity resolve(EasyClientRequest request, Object requestBody) {
        if (requestBody == null) {
            return null;
        }
        JSONObject jsonObject = JSON.parseObject(JSON.toJSONString(requestBody));
        List<NameValuePair> formData = new ArrayList<>();
        jsonObject.forEach((k, v) -> formData.add(new BasicNameValuePair(k, v.toString())));
        try {
            return new UrlEncodedFormEntity(formData);
        } catch (UnsupportedEncodingException e) {
            throw new RuntimeException("unsupported encoding exception.", e);
        }
    }
}
