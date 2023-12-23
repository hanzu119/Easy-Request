package com.easy.request.parse.req.apache;

import com.easy.request.client.EasyClientRequest;
import com.easy.request.parse.req.Resolver;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

/**
 * @author AGPg
 */
public class ApacheXmlResolver implements Resolver {

    private final XmlMapper xmlMapper = new XmlMapper();

    private ApacheXmlResolver() {
    }

    private static ApacheXmlResolver INSTANCE;

    public static ApacheXmlResolver getInstance() {
        if (INSTANCE == null) {
            synchronized (ApacheXmlResolver.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApacheXmlResolver();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public StringEntity resolve(EasyClientRequest request, Object requestBody) {

        try {
            return new StringEntity(
                    xmlMapper.writeValueAsString(requestBody),
                    ContentType.create(request.getContentType(), request.getRequestCharset())
            );
        } catch (JsonProcessingException e) {
            //todo
        }
        return null;
    }
}
