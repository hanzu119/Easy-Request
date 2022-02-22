package com.easy.request.client;

import com.easy.request.constant.EnumMethod;
import com.easy.request.constant.EnumReqScheme;
import com.easy.request.constant.EnumResScheme;
import lombok.Data;

import java.util.HashMap;
import java.util.Map;

@Data
public class DefaultClientRequest implements EasyClientRequest {

    private EnumMethod method;
    private String protocol;
    private EnumReqScheme reqScheme = EnumReqScheme.EMPTY;
    private String requestCharset = "utf-8";
    private long timeout = -1;
    private String host;
    private Integer port = -1;
    private String path;
    private String contentType;
    private EnumResScheme resScheme;
    private String responseCharset;
    private Map<String, String> params;
    private Map<String, String> headers;
    private Map<String, Object> cookies;
    private Map<String, Object> hookParams;

    public DefaultClientRequest() {
        this.params = new HashMap<>();
        this.headers = new HashMap<>();
        this.cookies = new HashMap<>();
        this.hookParams = new HashMap<>();
    }


}
