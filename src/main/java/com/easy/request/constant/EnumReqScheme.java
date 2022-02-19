package com.easy.request.constant;

public enum EnumReqScheme {

    EMPTY,
    JSON,
    XML,
    FORM,
    FILE,
    MULTI_FORM,
    CUSTOM;

    public boolean isEmpty() {
        return EMPTY.equals(this);
    }

    public boolean isNotEmpty() {
        return !isEmpty();
    }
}
