package com.easy.request.parse.request.apache;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author AGPg
 */
@Data
@AllArgsConstructor
public class PartModel {

    public static final String PREFIX = ".";

    private String key;
    private Object value;
    private String contentType;
    private String fileNameSuffix;

    public String getFileNameSuffix() {
        if (fileNameSuffix.startsWith(PREFIX)) {
            return fileNameSuffix;
        }
        return PREFIX + fileNameSuffix;
    }
}
