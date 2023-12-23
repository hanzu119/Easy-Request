package com.easy.request.factory;

import com.easy.request.constant.EnumResScheme;
import com.easy.request.parse.res.Convertor;
import com.easy.request.parse.res.JsonConvertor;
import com.easy.request.parse.res.StringConvertor;

public class ConvertorFactory {

    public Convertor build(EnumResScheme scheme) {

        if (EnumResScheme.EMPTY.equals(scheme) || EnumResScheme.JSON.equals(scheme)) {
            return buildJsonConvertor();
        }
        if (EnumResScheme.STRING.equals(scheme)) {
            return buildStringConvertor();
        }
        throw new RuntimeException("Illegal response scheme.");
    }

    protected Convertor buildJsonConvertor() {
        return JsonConvertor.getInstance();
    }

    protected Convertor buildStringConvertor() {
        return StringConvertor.getInstance();
    }

}
