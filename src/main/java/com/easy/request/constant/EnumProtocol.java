package com.easy.request.constant;

public enum EnumProtocol {

    HTTP("http", 80),
    HTTPS("https", 443);

    EnumProtocol(String code, int defaultPort) {
        this.code = code;
        this.defaultPort = defaultPort;
    }

    public final String code;
    public final int defaultPort;
}
