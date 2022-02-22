package com.easy.request;

import com.easy.request.annotation.*;
import com.easy.request.client.ApacheClient;
import com.easy.request.client.DefaultClientRequest;
import com.easy.request.client.EasyClientRequest;
import com.easy.request.client.EasyRequestClient;
import com.easy.request.constant.*;
import com.easy.request.factory.ConvertorFactory;
import com.easy.request.factory.EasyClientRequestFactory;
import com.easy.request.factory.ResolverFactory;
import com.easy.request.model.ReqAttrHandle;
import com.easy.request.parse.req.RequestParamChecker;
import com.easy.request.parse.req.Resolver;
import com.easy.request.parse.res.Convertor;
import com.easy.request.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EasyInvocation implements InvocationHandler {

    public static final String SERIAL_VERSION_UID = "seriaVersionUID";
    public static final int CLASS_DEEP = 3;

    private final Class<?> interfaceClass;
    private final EasyInterceptor[] interceptors;
    private final EasyRequestClient client;
    private final RequestParamChecker checker;
    private final EasyClientRequestFactory clientRequestFactory;
    private final ResolverFactory resolverFactory;
    private final ConvertorFactory convertorFactory;

    public EasyInvocation(Class<?> interfaceClass, EasyRequestClient client,
                          EasyClientRequestFactory clientRequestFactory, RequestParamChecker checker,
                          ResolverFactory resolverFactory, ConvertorFactory convertorFactory,
                          EasyInterceptor[] interceptors) {
        if (interfaceClass == null || !interfaceClass.isInterface()) {
            throw new RuntimeException("interface error");
        }

        if (clientRequestFactory == null) {
            this.clientRequestFactory = DefaultClientRequest::new;
        } else {
            this.clientRequestFactory = clientRequestFactory;
        }
        if (client != null) {
            this.client = client;
        } else {
            this.client = new ApacheClient();
        }
        this.checker = checker;
        if (resolverFactory != null) {
            this.resolverFactory = resolverFactory;
        } else {
            this.resolverFactory = new ResolverFactory();
        }
        if (convertorFactory != null) {
            this.convertorFactory = convertorFactory;
        } else {
            this.convertorFactory = new ConvertorFactory();
        }
        this.interfaceClass = interfaceClass;
        this.interceptors = interceptors;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        EasyClientRequest request = clientRequestFactory.build();
        Objects.requireNonNull(request, "EasyClientRequest can't be null when clientRequestFactory build easyClientRequest.");
        dealHttp(method, request);
        dealEasyRequest(method, request);
        Object requestBody = dealParam(request, method, args);
        dealPath(request);
        EnumReqScheme reqScheme = request.getReqScheme();
        Resolver resolver = resolverFactory.build(reqScheme);
        Object requestEntity = resolver.resolve(request, requestBody);
        InputStream inputStream = sendRequest(request, requestEntity);
        Object response = dealResponse(request.getResScheme(), inputStream, Charset.forName(request.getRequestCharset()), method);
        call(easyInterceptor -> easyInterceptor.onReceive(request, response));
        return response;
    }

    private Object dealParam(EasyClientRequest request, Method method, Object[] args) {
        Map<String, String> urlParams = request.getParams();
        dealFixedRequestParam(this.interfaceClass.getAnnotation(EasyRequestParam.class), urlParams);
        dealFixedRequestParam(method.getAnnotation(EasyRequestParam.class), urlParams);
        Annotation[][] parameterAnnotations = method.getParameterAnnotations();
        Object requestBody = null;
        for (int i = 0; i < parameterAnnotations.length; i++) {
            Annotation[] parameterAnnotation = parameterAnnotations[i];
            if (parameterAnnotation == null || parameterAnnotation.length == 0) {
                continue;
            }
            Object arg = args[i];
            if (this.checker != null) {
                this.checker.check(arg);
            }
            if (arg == null) {
                continue;
            }
            for (Annotation annotation : parameterAnnotation) {
                if (annotation instanceof EasyRequestBody) {
                    requestBody = arg;
                } else if (annotation instanceof EasyRequestParam) {
                    dealEasyRequestParam(urlParams, arg, (EasyRequestParam) annotation);
                } else if (annotation instanceof HookParam) {
                    dealEasyHookParam(request, arg, (HookParam) annotation);
                } else if (annotation instanceof HEADER) {
                    dealEasyHeader(request, arg, (HEADER) annotation);
                } else if (annotation instanceof EasyPathVariable) {
                    dealEasyPathValue(request, arg, (EasyPathVariable) annotation);
                } else if (annotation instanceof HOST) {
                    dealEasyHost(request, arg, (HOST) annotation);
                }
            }
        }
        return requestBody;
    }

    private void dealPath(EasyClientRequest request) {
        String path = request.getPath();
        if (path.contains(EasyCodes.REPEAT_SPLIT)) {
            request.setPath(path.replaceAll(EasyCodes.REPEAT_SPLIT, EasyCodes.PATH_SPLIT));
        }
    }

    private static void dealEasyHeader(EasyClientRequest request, Object arg, HEADER header) {
        Map<String, String> headers = request.getHeaders();
        if (header.isObject()) {
            List<Field> fields = collectField(arg, header.ignoreSerialNUmber());
            for (Field field : fields) {
                try {
                    String fieldName = field.getName();
                    HEADER fieldAnnotation = field.getAnnotation(HEADER.class);
                    Object value = field.get(arg);
                    if (value != null) {
                        fillHeader(headers, fieldName, value, fieldAnnotation);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("illegal access.", e);
                }
            }
        } else {
            fillHeader(headers, null, arg, header);
        }
    }

    private static void dealEasyPathValue(EasyClientRequest request, Object arg, EasyPathVariable pathVariable) {
        String name = StringUtils.defaultIfBlank(pathVariable.name(), pathVariable.value());
        if (StringUtils.isBlank(name)) {
            throw new RuntimeException("@EasyPathVariable's name or value can't be blank at the same time.");
        }
        request.setPath(StringUtils.replacePlaceholder(name, request.getPath(), String.valueOf(arg)));
    }

    private static void dealEasyHost(EasyClientRequest request, Object nHost, HOST host) {
        String value = host.value();
        String old = request.getHost();
        request.setHost(StringUtils.replacePlaceholder(value, old, String.valueOf(nHost)));
    }

    private static void fillHeader(Map<String, String> headers, String key, Object arg, HEADER header) {
        if (header != null) {
            if (header.isIgnore()) {
                return;
            }
            if (StringUtils.isNotBlank(header.name())) {
                key = header.name();
            }
            key = StringUtils.defaultIfBlank(header.value(), key);
        }
        if (StringUtils.isBlank(key)) {
            if (arg instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) arg;
                map.forEach((k, v) -> headers.put(String.valueOf(k), String.valueOf(v)));
                return;
            } else {
                throw new RuntimeException("illegal header.");
            }
        }
        headers.put(key, String.valueOf(arg));
    }

    private static void dealEasyHookParam(EasyClientRequest request, Object arg, HookParam annotation) {
        String name = StringUtils.defaultIfBlank(annotation.name(), annotation.value());
        Map<String, Object> hookParams = request.getHookParams();
        if (hookParams == null) {
            return;
        }
        if (StringUtils.isNotBlank(name)) {
            hookParams.put(name, arg);
        } else {
            throw new RuntimeException("name or value not be appointed.");
        }
    }

    private void dealEasyRequestParam(Map<String, String> urlParams, Object arg, EasyRequestParam annotation) {
        if (annotation.isObject()) {
            List<Field> fields = collectField(arg, annotation.ignoreSerialNumber());
            for (Field field : fields) {
                try {
                    String fieldName = field.getName();
                    EasyRequestParam fieldAnnotation = field.getAnnotation(EasyRequestParam.class);
                    Object value = field.get(arg);
                    if (value != null) {
                        fillUrlParams(urlParams, fieldName, value, fieldAnnotation);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("illegal access.", e);
                }
            }
        } else {
            if (arg instanceof Map) {
                Map<?, ?> map = (Map<?, ?>) arg;
                map.forEach((k, v) -> urlParams.put(String.valueOf(k), String.valueOf(v)));
            } else {
                fillUrlParams(urlParams, null, arg, annotation);
            }
        }
    }

    private static List<Field> collectField(Object paramObject, boolean ignoreSerialNumber) {
        Class<?> objectClass = paramObject.getClass();
        Field[] fields = objectClass.getFields();
        for (int i = 0; i < CLASS_DEEP; i++) {
            objectClass = objectClass.getSuperclass();
            if (objectClass == null) {
                break;
            }
            Field[] supplierFields = objectClass.getDeclaredFields();
            int supperLength = supplierFields.length;
            if (supperLength != 0) {
                int newLength = fields.length + supperLength;
                Field[] old = fields;
                fields = new Field[newLength];
                for (int j = 0; j < newLength; j++) {
                    if (j < supperLength) {
                        fields[j] = supplierFields[j];
                    } else {
                        fields[j] = old[j - supperLength];
                    }
                }
            }
        }
        return Arrays.stream(fields).filter(field -> !ignoreSerialNumber || !SERIAL_VERSION_UID.equals(field.getName()))
                .peek(field -> field.setAccessible(true)).collect(Collectors.toList());
    }

    private void fillUrlParams(Map<String, String> urlParams, String key, Object arg, EasyRequestParam easyRequestParam) {
        if (easyRequestParam != null) {
            if (easyRequestParam.isIgnore()) {
                return;
            }
            if (easyRequestParam.name().length == 1 && StringUtils.isNotBlank(easyRequestParam.name()[0])) {
                key = easyRequestParam.name()[0];
            }
            key = StringUtils.defaultIfBlank(easyRequestParam.value(), key);
            if (StringUtils.isBlank(key)) {
                throw new RuntimeException("you should appoint name or value in your method's parameter.");
            }
            urlParams.put(key, String.valueOf(arg));
        }
    }

    public Object dealResponse(EnumResScheme resScheme, InputStream inputStream, Charset resCharset, Method method) {

        if (EnumResScheme.INPUT_STREAM.equals(resScheme)) {
            return inputStream;
        }
        Convertor convertor = this.convertorFactory.build(resScheme);
        if (convertor == null) {
            throw new RuntimeException(resScheme.name() + " not mapping any convertor.");
        }
        Object responseEntity = convertor.convert(inputStream, resCharset, method);
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("io close error.", e);
        }
        return responseEntity;
    }

    public void dealEasyRequest(Method method, EasyClientRequest request) {
        ReqAttrHandle.builder(this.interfaceClass, method).fillClientRequest(request);
    }

    public InputStream sendRequest(EasyClientRequest request, Object requestEntity) {
        call(easyInterceptor -> easyInterceptor.beforeRequest(request, requestEntity));
        EnumMethod method = request.getMethod();
        if (EnumMethod.GET.equals(method)) {
            return this.client.get(request);
        }
        if (EnumMethod.POST.equals(method)) {
            return this.client.post(request, requestEntity);
        }
        if (EnumMethod.PUT.equals(method)) {
            return this.client.put(request, requestEntity);
        }
        if (EnumMethod.DELETE.equals(method)) {
            return this.client.get(request);
        }
        throw new RuntimeException("illegal request method");
    }

    private void dealHttp(Method method, EasyClientRequest request) {
        HTTP interfaceHttp = this.interfaceClass.getAnnotation(HTTP.class);
        if (interfaceHttp != null) {
            fillRequestByHttp(request, interfaceHttp);
        } else {
            throw new RuntimeException("protocol annotation must be appointed in your interface");
        }
        HTTP methodHttp = method.getAnnotation(HTTP.class);
        if (methodHttp != null) {
            fillRequestByHttp(request, methodHttp);
        }
    }

    private static void fillRequestByHttp(EasyClientRequest request, HTTP http) {
        EnumProtocol protocol = http.protocol();
        request.setProtocol(protocol.code);
        String host = request.getHost();
        host = StringUtils.defaultIfBlank(StringUtils.defaultIfBlank(http.host(), http.value()), host);
        if (StringUtils.isBlank(host)) {
            throw new RuntimeException("host can't be blank.");
        }
        request.setHost(host);
        Integer port = request.getPort();
        if (port == null) {
            request.setPort(-1);
        }
        int newPort = http.port();
        if (newPort > 0) {
            request.setPort(newPort);
        }
    }

    private static void dealFixedRequestParam(EasyRequestParam requestParam, Map<String, String> params) {
        if (requestParam == null) {
            return;
        }
        String[] names = requestParam.name();
        String[] values = requestParam.fixedValue();
        for (int i = 0; i < names.length; i++) {
            params.put(names[i], values[i]);
        }
    }

    private void call(Consumer<EasyInterceptor> consumer) {
        if (interceptors != null) {
            for (EasyInterceptor interceptor : interceptors) {
                consumer.accept(interceptor);
            }
        }
    }
}
