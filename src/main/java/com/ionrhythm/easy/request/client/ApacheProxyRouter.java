package com.ionrhythm.easy.request.client;

import com.ionrhythm.easy.request.constant.EasyCodes;
import org.apache.http.HttpException;
import org.apache.http.HttpHost;
import org.apache.http.HttpRequest;
import org.apache.http.impl.conn.DefaultRoutePlanner;
import org.apache.http.protocol.HttpContext;

/**
 * @author AGPg
 */
public class ApacheProxyRouter extends DefaultRoutePlanner {
    public ApacheProxyRouter() {
        super(null);
    }

    @Override
    protected HttpHost determineProxy(HttpHost target, HttpRequest request, HttpContext context) throws HttpException {
        Object host = context.getAttribute(EasyCodes.PROXY_HOOK);
        if (host != null) {
            return (HttpHost) host;
        }
        return super.determineProxy(target, request, context);
    }
}
