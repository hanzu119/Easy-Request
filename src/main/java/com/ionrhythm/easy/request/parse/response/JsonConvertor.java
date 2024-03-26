package com.ionrhythm.easy.request.parse.response;

import com.alibaba.fastjson.JSON;
import com.ionrhythm.easy.request.client.EasyClientRequest;
import com.ionrhythm.easy.request.constant.EasyCodes;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

public class JsonConvertor implements Convertor {

    private JsonConvertor() {
    }

    private static JsonConvertor INSTANCE;

    public static JsonConvertor getInstance() {
        if (INSTANCE == null) {
            synchronized (JsonConvertor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new JsonConvertor();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Object convert(InputStream input, EasyClientRequest request, Type returnType) {
        try {
            return JSON.parseObject(input, Charset.forName(request.getResponseCharset()), returnType);
        } catch (IOException e) {
            throw new RuntimeException(EasyCodes.PARSE_ERROR, e);
        }
    }

}
