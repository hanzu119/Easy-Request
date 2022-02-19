package com.easy.request.parse.res;

import com.alibaba.fastjson.JSON;
import com.easy.request.constant.EasyCodes;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
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
    public Object convert(InputStream input, Charset charset, Method method) {
        try {
            return JSON.parseObject(input, charset, method.getGenericReturnType());
        } catch (IOException e) {
            throw new RuntimeException(EasyCodes.PARSE_ERROR, e);
        }
    }
}
