package com.easy.request.parse.res;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
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
    public Object convert(InputStream input, Charset charset, Method method) {
        try {
            return IOUtils.toString(input, charset);
        } catch (IOException e) {
            throw new RuntimeException("io error");
        }
    }
}
