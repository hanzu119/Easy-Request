package com.easy.request.client;

import com.easy.request.constant.EnumMethod;
import com.easy.request.constant.EnumReqScheme;
import com.easy.request.constant.EnumResScheme;

import java.util.Map;

public interface EasyClientRequest {

    void setMethod(EnumMethod method);

    default String getProtocol() {
        return null;
    }

    default EnumReqScheme getReqScheme() {
        return null;
    }

    default String getRequestCharset() {
        return null;
    }

    long getTimeout();

    String getHost();

    Integer getPort();

    String getPath();

    Map<String, String> getParams();

    Map<String, String> getHeaders();

    Map<String, Object> getCookies();

    default Map<String, Object> getHookParams() {
        return null;
    }

    default String getContentType() {
        return null;
    }

    EnumResScheme getResScheme();

    String getResponseCharset();

    EnumMethod getMethod();

    void setResponseCharset(String charset);

    void setResScheme(EnumResScheme resScheme);

    void setContentType(String contentType);

    void setReqScheme(EnumReqScheme requestScheme);

    void setRequestCharset(String requestCharset);

    void setTimeout(long timeout);

    void setProtocol(String protocol);

    void setHost(String host);

    void setPort(Integer port);

    void setPath(String path);

    Boolean getRecordOrigin();

    void setRecordOrigin(Boolean recordOrigin);

}
