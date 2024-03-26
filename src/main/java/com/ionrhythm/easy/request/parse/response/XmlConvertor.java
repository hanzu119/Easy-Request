package com.ionrhythm.easy.request.parse.response;

import com.ionrhythm.easy.request.client.EasyClientRequest;
import com.ionrhythm.easy.request.constant.EasyCodes;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.dataformat.xml.XmlMapper;
import com.fasterxml.jackson.dataformat.xml.ser.ToXmlGenerator;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;

/**
 * @author AGPg
 */
public class XmlConvertor implements Convertor {

    private final XmlMapper xmlMapper = new XmlMapper();

    private XmlConvertor() {
        this.xmlMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        this.xmlMapper.enable(ToXmlGenerator.Feature.WRITE_XML_DECLARATION);
    }

    private static XmlConvertor INSTANCE;

    public static XmlConvertor getInstance() {
        if (INSTANCE == null) {
            synchronized (XmlConvertor.class) {
                if (INSTANCE == null) {
                    INSTANCE = new XmlConvertor();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Object convert(InputStream input, EasyClientRequest request, Type returnType) {
        try {
            return xmlMapper.readValue(new InputStreamReader(input, request.getResponseCharset()), xmlMapper.constructType(returnType));
        } catch (IOException e) {
            throw new RuntimeException(EasyCodes.PARSE_ERROR);
        }
    }
}
