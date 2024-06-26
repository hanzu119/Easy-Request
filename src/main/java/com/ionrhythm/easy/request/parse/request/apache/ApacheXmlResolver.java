package com.ionrhythm.easy.request.parse.request.apache;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.ionrhythm.easy.request.client.EasyClientRequest;
import com.ionrhythm.easy.request.parse.request.Resolver;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;

/**
 * @author AGPg
 */
public class ApacheXmlResolver implements Resolver {

    private static ApacheXmlResolver INSTANCE;
    private final XmlMapper xmlMapper = new XmlMapper();

    private ApacheXmlResolver() {
    }

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
