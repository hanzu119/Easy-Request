package com.easy.request.parse.res;

import com.easy.request.client.EasyClientRequest;

import java.io.InputStream;
import java.lang.reflect.Type;

public interface Convertor {
    Object convert(InputStream input, EasyClientRequest request, Type returnType);
}
