package com.easy.request.parse.response;

import com.easy.request.client.EasyClientRequest;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Type;
import java.nio.charset.Charset;

public class StringConvertor implements Convertor {

    private StringConvertor() {
    }

    private static StringConvertor INSTANCE;

    public static StringConvertor getInstance() {
        if (INSTANCE == null) {
            synchronized (StringConvertor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new StringConvertor();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Object convert(InputStream input, EasyClientRequest request, Type returnType) {
        try {
            return IOUtils.toString(input, Charset.forName(request.getRequestCharset()));
        } catch (IOException e) {
            throw new RuntimeException("io error");
        }
    }

}