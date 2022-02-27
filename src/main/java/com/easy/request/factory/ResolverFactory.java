package com.easy.request.factory;

import com.easy.request.constant.EnumReqScheme;
import com.easy.request.parse.req.ApacheFileResolver;
import com.easy.request.parse.req.ApacheFormResolver;
import com.easy.request.parse.req.ApacheJsonResolver;
import com.easy.request.parse.req.Resolver;

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

}
