package com.easy.request.factory;

import com.easy.request.constant.EnumReqScheme;
import com.easy.request.parse.req.Resolver;
import com.easy.request.parse.req.apache.ApacheFileResolver;
import com.easy.request.parse.req.apache.ApacheFormResolver;
import com.easy.request.parse.req.apache.ApacheJsonResolver;
import com.easy.request.parse.req.apache.ApacheMultiFormResolver;
import com.easy.request.parse.req.apache.ApacheXmlResolver;

public class ResolverFactory {

    public Resolver build(EnumReqScheme scheme) {

        if (EnumReqScheme.EMPTY.equals(scheme) || EnumReqScheme.JSON.equals(scheme)) {
            return buildJsonResolver();
        }
        if (EnumReqScheme.FORM.equals(scheme)) {
            return buildFormResolver();
        }
        if (EnumReqScheme.FILE.equals(scheme)) {
            return buildFileResolver();
        }
        if (EnumReqScheme.XML.equals(scheme)) {
            return buildFileResolver();
        }
        if (EnumReqScheme.MULTI_FORM.equals(scheme)) {
            return buildFileResolver();
        }
        if (EnumReqScheme.CUSTOM.equals(scheme)) {
            return buildFileResolver();
        }

        throw new RuntimeException("Illegal request scheme.");
    }

    protected Resolver buildJsonResolver() {
        return ApacheJsonResolver.getInstance();
    }

    protected Resolver buildFormResolver() {
        return ApacheFormResolver.getInstance();
    }

    protected Resolver buildFileResolver() {
        return ApacheFileResolver.getInstance();
    }

    protected Resolver buildXmlResolver() {
        return ApacheXmlResolver.getInstance();
    }

    protected Resolver buildMultiFormResolver() {
        return ApacheMultiFormResolver.getInstance();
    }

    protected Resolver buildCustomResolver() {
        throw new RuntimeException("not config custom resolver");
    }

}
