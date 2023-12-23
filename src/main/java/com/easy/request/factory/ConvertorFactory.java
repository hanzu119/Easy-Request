package com.easy.request.factory;

import com.easy.request.constant.EnumResScheme;
import com.easy.request.parse.res.Convertor;
import com.easy.request.parse.res.JsonConvertor;
import com.easy.request.parse.res.StringConvertor;
import com.easy.request.parse.res.XmlConvertor;

public class ConvertorFactory {

    public Convertor build(EnumResScheme scheme) {

        if (EnumResScheme.EMPTY.equals(scheme) || EnumResScheme.JSON.equals(scheme)) {
            return buildJsonConvertor();
        }
        if (EnumResScheme.STRING.equals(scheme)) {
            return buildStringConvertor();
        }
        if (EnumResScheme.XML.equals(scheme)) {
            return buildXmlConvertor();
        }
        if (EnumResScheme.CUSTOM.equals(scheme)) {
            return buildCustomConvertor();
        }
        throw new RuntimeException("Illegal response scheme.");
    }

    protected Convertor buildJsonConvertor() {
        return JsonConvertor.getInstance();
    }

    protected Convertor buildStringConvertor() {
        return StringConvertor.getInstance();
    }

    protected Convertor buildXmlConvertor() {
        return XmlConvertor.getInstance();
    }

    protected Convertor buildCustomConvertor() {
        throw new RuntimeException("not config custom convertor");
    }

}
