package com.easy.request.model;

import com.easy.request.constant.EnumMethod;
import com.easy.request.constant.EnumReqScheme;
import com.easy.request.constant.EnumResScheme;
import lombok.Data;

@Data
public class ReqAttrModel {
    private String path;
    private EnumMethod method;
    private EnumReqScheme reqScheme = EnumReqScheme.EMPTY;
    private String reqCharset = "utf-8";
    private EnumResScheme resScheme = EnumResScheme.EMPTY;
    private String resCharset = "utf-8";
    private long timeout = 3000L;
    private String contentType;

    public void setTimeout(long timeout) {
        if (timeout > 0) {
            this.timeout = timeout;
        }
    }
}
