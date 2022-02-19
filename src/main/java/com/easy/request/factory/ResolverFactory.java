package com.easy.request.factory;

import com.easy.request.constant.EnumReqScheme;
import com.easy.request.parse.req.ApacheJsonResolver;
import com.easy.request.parse.req.Resolver;

public class ResolverFactory {

    public static Resolver build(EnumReqScheme scheme) {

        if (EnumReqScheme.EMPTY.equals(scheme) || EnumReqScheme.JSON.equals(scheme)) {
            return buildJsonResolver();
        }
        throw new RuntimeException("Illegal request scheme.");
    }

    protected static Resolver buildJsonResolver() {
        return ApacheJsonResolver.getInstance();
    }

}
