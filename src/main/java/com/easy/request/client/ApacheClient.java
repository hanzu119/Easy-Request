package com.easy.request.client;

import com.easy.request.model.EasyResponse;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.concurrent.FutureCallback;
import org.apache.http.impl.client.BasicCookieStore;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


public class ApacheClient implements EasyRequestClient {

    private final CloseableHttpClient httpClient;

    public ApacheClient() {

        httpClient = HttpClientBuilder.create().setDefaultCookieStore(new BasicCookieStore()).build();
    }

    public ApacheClient(CloseableHttpClient client) {
        this.httpClient = client;
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

    public EasyResponse<InputStream> execute(HttpRequestBase requestBase) {
        try {
            HttpResponse response = this.httpClient.execute(requestBase);
            StatusLine statusLine = response.getStatusLine();
            if (HttpURLConnection.HTTP_OK != statusLine.getStatusCode()) {
                throw new RuntimeException(statusLine.getStatusCode() + " " + statusLine.getReasonPhrase() + " " +
                        IOUtils.toString(response.getEntity().getContent(), StandardCharsets.UTF_8));
            }
            EasyResponse<InputStream> easyResponse = new EasyResponse<>();
            easyResponse.setCode(statusLine.getStatusCode());
            easyResponse.setReason(statusLine.getReasonPhrase());
            easyResponse.setEntity(response.getEntity().getContent());
            return easyResponse;
        } catch (IOException e) {
            throw new RuntimeException("io error", e);
        }
    }

    @Override
    public EasyResponse<InputStream> post(EasyClientRequest request, Object requestEntity) {
        URIBuilder builder = convert(request);
        HttpPost post = new HttpPost(builder.toString());
        request.getHeaders().forEach(post::addHeader);
        post.setEntity((HttpEntity) requestEntity);
        return execute(post);
    }

    @Override
    public EasyResponse<InputStream> put(EasyClientRequest request, Object requestEntity) {
        URIBuilder builder = convert(request);
        HttpPut put = new HttpPut(builder.toString());
        request.getHeaders().forEach(put::addHeader);
        put.setEntity((HttpEntity) requestEntity);
        return execute(put);
    }

    @Override
    public EasyResponse<InputStream> delete(EasyClientRequest request) {
        URIBuilder builder = convert(request);
        HttpDelete delete = new HttpDelete(builder.toString());
        request.getHeaders().forEach(delete::addHeader);
        return execute(delete);
    }

    @Override
    public EasyResponse<InputStream> get(EasyClientRequest request) {
        URIBuilder builder = convert(request);
        HttpGet get = new HttpGet(builder.toString());
        request.getHeaders().forEach(get::addHeader);
        return execute(get);
    }

    @Override
    public void shutdown() {
        try {
            this.httpClient.close();
        } catch (IOException e) {
            throw new RuntimeException("failed to shutdown http client.", e);
        }
    }

}
