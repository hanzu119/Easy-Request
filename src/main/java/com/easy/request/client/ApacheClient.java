package com.easy.request.client;

import org.apache.commons.io.IOUtils;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.*;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.nio.client.CloseableHttpAsyncClient;
import org.apache.http.impl.nio.client.HttpAsyncClientBuilder;
import org.apache.http.impl.nio.conn.PoolingNHttpClientConnectionManager;
import org.apache.http.impl.nio.reactor.DefaultConnectingIOReactor;
import org.apache.http.impl.nio.reactor.IOReactorConfig;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.nio.reactor.ConnectingIOReactor;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;


public class ApacheClient implements EasyRequestClient {

    private Header cookie = null;

    private final CloseableHttpAsyncClient httpAsyncClient;

    public ApacheClient() {
        try {
            IOReactorConfig ioReactorConfig = IOReactorConfig.custom().setIoThreadCount(2).build();
            ConnectingIOReactor ioReactor = new DefaultConnectingIOReactor(ioReactorConfig);
            PoolingNHttpClientConnectionManager cm = new PoolingNHttpClientConnectionManager(ioReactor);
            cm.setMaxTotal(300);
            cm.setDefaultMaxPerRoute(300);
            httpAsyncClient = HttpAsyncClientBuilder.create().setConnectionManager(cm).setDefaultRequestConfig(
                            RequestConfig.custom().setConnectionRequestTimeout(16 * 1000).setSocketTimeout(30 * 1000).build())
                    .setUserAgent("Apache").build();
            httpAsyncClient.start();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public ApacheClient(CloseableHttpAsyncClient httpAsyncClient) {
        this.httpAsyncClient = httpAsyncClient;
    }

    private URIBuilder convert(EasyClientRequest request) {
        URIBuilder builder = new URIBuilder().setScheme(request.getProtocol())
                .setHost(request.getHost())
                .setPort(request.getPort())
                .setPath(request.getPath())
                .setCharset(Charset.forName(request.getRequestCharset()));
        Map<String, String> params = request.getParams();
        if (params != null && !params.isEmpty()) {
            builder.setParameters(params.entrySet().stream()
                    .map(entry -> new BasicNameValuePair(entry.getKey(), entry.getValue()))
                    .collect(Collectors.toList()));
        }
        return builder;
    }

    static class FutureAdapter implements FutureCallback<HttpResponse> {
        private final CompletableFuture<HttpResponse> cf = new CompletableFuture<>();

        @Override
        public void completed(HttpResponse httpResponse) {
            cf.complete(httpResponse);
        }

        @Override
        public void failed(Exception e) {
            cf.completeExceptionally(e);
        }

        @Override
        public void cancelled() {
            cf.cancel(true);
        }
    }

    public InputStream execute(long timeout, HttpRequestBase requestBase) {
        FutureAdapter fa = new FutureAdapter();
        this.httpAsyncClient.execute(requestBase, fa);
        try {
            HttpResponse response = fa.cf.get(timeout, TimeUnit.MILLISECONDS);
            this.cookie = response.getFirstHeader("Set-Cookie");
            StatusLine statusLine = response.getStatusLine();
            if (HttpURLConnection.HTTP_OK != statusLine.getStatusCode()) {
                throw new RuntimeException(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase() + " " +
                        IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
            }
            return response.getEntity().getContent();
        } catch (IOException e) {
            throw new RuntimeException("io error", e);
        } catch (ExecutionException e) {
            throw new RuntimeException("execution error", e);
        } catch (InterruptedException e) {
            throw new RuntimeException("interrupted error", e);
        } catch (TimeoutException e) {
            throw new RuntimeException("time out error", e);
        }
    }

    @Override
    public InputStream post(EasyClientRequest request, Object requestEntity) {
        URIBuilder builder = convert(request);
        HttpPost post = new HttpPost(builder.toString());
        request.getHeaders().forEach(post::addHeader);
        post.setEntity((HttpEntity) requestEntity);
        return execute(request.getTimeout(), post);
    }

    @Override
    public InputStream put(EasyClientRequest request, Object requestEntity) {
        URIBuilder builder = convert(request);
        HttpPut put = new HttpPut(builder.toString());
        request.getHeaders().forEach(put::addHeader);
        put.setEntity((HttpEntity) requestEntity);
        return execute(request.getTimeout(), put);
    }

    @Override
    public InputStream delete(EasyClientRequest request) {
        URIBuilder builder = convert(request);
        HttpDelete delete = new HttpDelete(builder.toString());
        request.getHeaders().forEach(delete::addHeader);
        return execute(request.getTimeout(), delete);
    }

    @Override
    public InputStream get(EasyClientRequest request) {
        URIBuilder builder = convert(request);
        HttpGet get = new HttpGet(builder.toString());
        request.getHeaders().forEach(get::addHeader);
        return execute(request.getTimeout(), get);
    }

    @Override
    public void shutdown() {
        try {
            this.httpAsyncClient.close();
        } catch (IOException e) {
            throw new RuntimeException("failed to shutdown http client.", e);
        }
    }

    public String getCookie() {
        if (this.cookie == null) {
            return null;
        }
        return this.cookie.getValue();
    }

}
