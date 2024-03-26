package com.ionrhythm.easy.request.model;

import com.ionrhythm.easy.request.constant.EnumMethod;
import com.ionrhythm.easy.request.constant.EnumReqScheme;
import com.ionrhythm.easy.request.constant.EnumResScheme;
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
    private Boolean recordOrigin = false;

    public void setTimeout(long timeout) {
        if (timeout > 0) {
            this.timeout = timeout;
        }
    }
}
