package com.easy.request.parse.res;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.nio.charset.Charset;

public interface Convertor {
    Object convert(InputStream input, Charset charset, Method method);
}
