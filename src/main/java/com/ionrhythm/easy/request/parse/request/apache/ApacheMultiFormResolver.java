package com.ionrhythm.easy.request.parse.request.apache;

import com.ionrhythm.easy.request.client.EasyClientRequest;
import com.ionrhythm.easy.request.parse.request.Resolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.DateFormatUtils;
import org.apache.http.entity.BufferedHttpEntity;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.StringBody;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Date;
import java.util.List;

/**
 * @author AGPg
 */
public class ApacheMultiFormResolver implements Resolver {

    private static final String TIMESTAMP_FORMAT = "yyyy-MM-dd-HH-mm-ss.SSS";
    private static ApacheMultiFormResolver INSTANCE;

    private ApacheMultiFormResolver() {
    }

    public static ApacheMultiFormResolver getInstance() {
        if (INSTANCE == null) {
            synchronized (ApacheMultiFormResolver.class) {
                if (INSTANCE == null) {
                    INSTANCE = new ApacheMultiFormResolver();
                }
            }
        }
        return INSTANCE;
    }

    @Override
    public Object resolve(EasyClientRequest request, Object requestBody) {
        if (requestBody instanceof List) {
            MultipartEntityBuilder builder = MultipartEntityBuilder.create()
                    .setMode(HttpMultipartMode.BROWSER_COMPATIBLE)
                    .setContentType(ContentType.MULTIPART_FORM_DATA)
                    .setCharset(Charset.forName(request.getRequestCharset()));
            List<PartModel> list = (List<PartModel>) requestBody;
            list.forEach(partModel -> {
                String key = partModel.getKey();
                Object value = partModel.getValue();
                String filename = DateFormatUtils.format(new Date(), TIMESTAMP_FORMAT) + partModel.getFileNameSuffix();

                String type = partModel.getContentType();
                ContentType contentType = StringUtils.isBlank(type) ? ContentType.DEFAULT_TEXT : ContentType.create(type);
                if (value instanceof File) {
                    File f = (File) value;
                    filename = StringUtils.defaultString(filename, f.getName());
                    try {
                        builder.addBinaryBody(key, new FileInputStream(f), contentType, filename);
                    } catch (FileNotFoundException e) {
                        //todo
                    }

                } else if (value instanceof InputStream) {
                    InputStream inputStream = (InputStream) value;
                    builder.addBinaryBody(key, inputStream, contentType, filename);
                } else {
                    builder.addPart(key, new StringBody(String.valueOf(value), contentType));
                }
            });
            try {
                return new BufferedHttpEntity(builder.build());
            } catch (IOException e) {
                //todo
            }
        }
        return null;
    }
}
