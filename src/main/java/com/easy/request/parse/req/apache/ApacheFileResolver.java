package com.easy.request.parse.req.apache;

import com.easy.request.client.EasyClientRequest;
import com.easy.request.parse.req.Resolver;
import com.easy.request.util.StringUtils;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.FileEntity;
import org.apache.http.entity.InputStreamEntity;

import java.io.File;
import java.io.InputStream;
import java.nio.charset.Charset;

public class ApacheFileResolver implements Resolver {

    private static ApacheFileResolver INSTANCE;

    private ApacheFileResolver() {
    }

    public static ApacheFileResolver getInstance() {
        if (INSTANCE == null) {
            synchronized (ApacheFileResolver.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApacheFileResolver();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Object resolve(EasyClientRequest request, Object requestBody) {
        if (requestBody instanceof File) {
            return new FileEntity((File) requestBody);
        }
        if (requestBody instanceof InputStream) {
            String contentType = request.getContentType();
            if (StringUtils.isBlank(contentType)) {
                return new InputStreamEntity((InputStream) requestBody);
            } else {
                return new InputStreamEntity((InputStream) requestBody,
                        ContentType.create(contentType, Charset.forName(request.getRequestCharset())));
            }
        }
        return null;
    }
}
