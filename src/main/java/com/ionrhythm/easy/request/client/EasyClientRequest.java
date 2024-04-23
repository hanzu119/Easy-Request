package com.ionrhythm.easy.request.client;

import com.ionrhythm.easy.request.constant.EnumMethod;
import com.ionrhythm.easy.request.constant.EnumReqScheme;
import com.ionrhythm.easy.request.constant.EnumResScheme;

import java.util.Map;

public interface EasyClientRequest {

    default String getProtocol() {
        return null;
    }

    void setProtocol(String protocol);

    default EnumReqScheme getReqScheme() {
        return null;
    }

    void setReqScheme(EnumReqScheme requestScheme);

    default String getRequestCharset() {
        return null;
    }

    void setRequestCharset(String requestCharset);

    long getTimeout();

    void setTimeout(long timeout);

    String getHost();

    void setHost(String host);

    Integer getPort();

    void setPort(Integer port);

    String getPath();

    void setPath(String path);

    Map<String, String> getParams();

    Map<String, String> getHeaders();

    Map<String, Object> getCookies();

    default Map<String, Object> getHookParams() {
        return null;
    }

    default String getContentType() {
        return null;
    }

    void setContentType(String contentType);

    EnumResScheme getResScheme();

    void setResScheme(EnumResScheme resScheme);

    String getResponseCharset();

    void setResponseCharset(String charset);

    EnumMethod getMethod();

    void setMethod(EnumMethod method);

    Boolean getRecordOrigin();

    void setRecordOrigin(Boolean recordOrigin);

}
