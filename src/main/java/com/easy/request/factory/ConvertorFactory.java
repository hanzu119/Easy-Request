package com.easy.request.factory;

import com.easy.request.constant.EnumResScheme;
import com.easy.request.parse.res.Convertor;
import com.easy.request.parse.res.JsonConvertor;

public class ConvertorFactory {

    public static Convertor build(EnumResScheme scheme) {

        if (EnumResScheme.EMPTY.equals(scheme) || EnumResScheme.JSON.equals(scheme)) {
            return buildJsonConvertor();
        }
        throw new RuntimeException("Illegal response scheme.");
    }

    protected static Convertor buildJsonConvertor() {
        return JsonConvertor.getInstance();
    }

}
