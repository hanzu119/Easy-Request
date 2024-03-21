package com.easy.request;

import com.easy.request.annotation.HEADER;
import com.easy.request.annotation.HOST;
import com.easy.request.annotation.HTTP;
import com.easy.request.annotation.HookParam;
import com.easy.request.annotation.PathVariable;
import com.easy.request.annotation.RequestBody;
import com.easy.request.annotation.RequestParam;
import com.easy.request.annotation.RequestPart;
import com.easy.request.client.ApacheClient;
import com.easy.request.client.DefaultClientRequest;
import com.easy.request.client.EasyClientRequest;
import com.easy.request.client.EasyRequestClient;
import com.easy.request.constant.EasyCodes;
import com.easy.request.constant.EnumMethod;
import com.easy.request.constant.EnumProtocol;
import com.easy.request.constant.EnumReqScheme;
import com.easy.request.constant.EnumResScheme;
import com.easy.request.factory.ConvertorFactory;
import com.easy.request.factory.EasyClientRequestFactory;
import com.easy.request.factory.ResolverFactory;
import com.easy.request.model.EasyResponse;
import com.easy.request.model.ReqAttrHandle;
import com.easy.request.parse.request.RequestParamChecker;
import com.easy.request.parse.request.Resolver;
import com.easy.request.parse.request.apache.PartModel;
import com.easy.request.parse.response.Convertor;
import com.easy.request.util.StringUtils;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.net.HttpURLConnection;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class EasyRequestInvocation implements InvocationHandler {

    public static final String SERIAL_VERSION_UID = "serialVersionUID";
    public static final int CLASS_DEEP = 3;

    private final Class<?> interfaceClass;
    private final Interceptor[] interceptors;
    private final EasyRequestClient client;
    private final RequestParamChecker checker;
    private final EasyClientRequestFactory clientRequestFactory;
    private final ResolverFactory resolverFactory;
    private final ConvertorFactory convertorFactory;

    public EasyRequestInvocation(Class<?> interfaceClass, EasyRequestClient client,
                                 EasyClientRequestFactory clientRequestFactory, RequestParamChecker checker,
                                 ResolverFactory resolverFactory, ConvertorFactory convertorFactory,
                                 Interceptor... interceptors) {
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
        ReqAttrHandle.builder(this.interfaceClass, method).fillClientRequest(request);
        Object requestBody = dealParam(request, method, args);
        dealPath(request);
        EnumReqScheme reqScheme = request.getReqScheme();
        Resolver resolver = resolverFactory.build(reqScheme);
        Object requestEntity = resolver.resolve(request, requestBody);

        EasyResponse easyResponse = sendRequest(request, requestEntity);
        InputStream inputStream = (InputStream) easyResponse.getEntity();
        int available = inputStream.available();
        boolean assignableFrom = method.getReturnType().isAssignableFrom(EasyResponse.class);
        Type type;
        if (assignableFrom) {
            type = ((ParameterizedType) method.getGenericReturnType()).getActualTypeArguments()[0];
        } else {
            type = method.getGenericReturnType();
        }
        EnumResScheme resScheme = request.getResScheme();
        if (assignableFrom) {
            if (available != 0) {
                if (EnumResScheme.INPUT_STREAM.equals(resScheme)) {
                    easyResponse.setEntity(inputStream);
                } else {
                    String requestCharset = request.getRequestCharset();
                    if (request.getRecordOrigin()) {
                        String origin = IOUtils.toString(inputStream, requestCharset);
                        easyResponse.setOriginEntity(origin);
                        easyResponse.setEntity(dealResponse(request, IOUtils.toInputStream(origin, requestCharset), type));
                    } else {
                        easyResponse.setEntity(dealResponse(request, inputStream, type));
                    }
                }
            } else {
                easyResponse.setEntity(null);
            }
            call(interceptor -> interceptor.onReceive(request, easyResponse));
            return easyResponse;
        }
        if (HttpURLConnection.HTTP_OK != easyResponse.getCode()) {
            throw new RuntimeException(easyResponse.getCode() + " " + easyResponse.getReason() + " " + IOUtils.toString(inputStream, StandardCharsets.UTF_8));
        }
        Object entity = available == 0 ? null : dealResponse(request, inputStream, type);
        call(interceptor -> interceptor.onReceive(request, entity));
        return entity;
    }

    private Object dealParam(EasyClientRequest request, Method method, Object[] args) {
        Map<String, String> urlParams = request.getParams();
        dealFixedRequestParam(this.interfaceClass.getAnnotation(RequestParam.class), urlParams);
        dealFixedRequestParam(method.getAnnotation(RequestParam.class), urlParams);
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
                if (annotation instanceof RequestBody) {
                    requestBody = arg;
                } else if (annotation instanceof RequestParam) {
                    dealEasyRequestParam(urlParams, arg, (RequestParam) annotation);
                } else if (annotation instanceof HookParam) {
                    dealEasyHookParam(request, arg, (HookParam) annotation);
                } else if (annotation instanceof HEADER) {
                    dealEasyHeader(request, arg, (HEADER) annotation);
                } else if (annotation instanceof PathVariable) {
                    dealEasyPathValue(request, arg, (PathVariable) annotation);
                } else if (annotation instanceof HOST) {
                    dealEasyHost(request, arg, (HOST) annotation);
                } else if (annotation instanceof RequestPart) {
                    if (requestBody == null) {
                        requestBody = new ArrayList<>();
                    }
                    if (requestBody instanceof List) {
                        List<PartModel> list = (List<PartModel>) requestBody;
                        RequestPart requestPart = (RequestPart) annotation;
                        list.add(new PartModel(requestPart.value(), arg, requestPart.contentType(), requestPart.filenameSuffix()));
                    }
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
        if (arg instanceof String || arg instanceof Number || arg instanceof Boolean) {
            fillHeader(headers, null, arg, header);
        } else if (arg instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) arg;
            map.forEach((k, v) -> headers.put(String.valueOf(k), String.valueOf(v)));
        } else {
            List<Field> fields = collectField(arg, header.ignoreSerialNumber());
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
        }
    }

    private static void dealEasyPathValue(EasyClientRequest request, Object arg, PathVariable pathVariable) {
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

    private void dealEasyRequestParam(Map<String, String> urlParams, Object arg, RequestParam annotation) {
        if (arg instanceof String || arg instanceof Number || arg instanceof Boolean) {
            fillUrlParams(urlParams, null, arg, annotation);
        } else if (arg instanceof Map) {
            Map<?, ?> map = (Map<?, ?>) arg;
            map.forEach((k, v) -> urlParams.put(String.valueOf(k), String.valueOf(v)));
        } else {
            List<Field> fields = collectField(arg, annotation.ignoreSerialNumber());
            for (Field field : fields) {
                try {
                    String fieldName = field.getName();
                    RequestParam fieldAnnotation = field.getAnnotation(RequestParam.class);
                    Object value = field.get(arg);
                    if (value != null) {
                        fillUrlParams(urlParams, fieldName, value, fieldAnnotation);
                    }
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("illegal access.", e);
                }
            }
        }
    }

    private static List<Field> collectField(Object paramObject, boolean ignoreSerialNumber) {
        Class<?> objectClass = paramObject.getClass();
        Field[] fields = objectClass.getDeclaredFields();
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
        return Arrays.stream(fields)
                .filter(field -> !ignoreSerialNumber || !SERIAL_VERSION_UID.equals(field.getName()))
                .peek(field -> field.setAccessible(true))
                .collect(Collectors.toList());
    }

    private void fillUrlParams(Map<String, String> urlParams, String key, Object arg, RequestParam requestParam) {
        if (requestParam == null && arg == null) {
            return;
        }
        if (requestParam != null) {
            if (requestParam.isIgnore()) {
                return;
            }
            if (requestParam.name().length == 1 && StringUtils.isNotBlank(requestParam.name()[0])) {
                key = requestParam.name()[0];
            }
            key = StringUtils.defaultIfBlank(requestParam.value(), key);
        }
        if (StringUtils.isBlank(key)) {
            throw new RuntimeException("you should appoint name or value in your method's parameter.");
        }
        if (arg == null) {
            if (requestParam.require()) {
                urlParams.put(key, StringUtils.EMPTY);
            }
            return;
        }
        urlParams.put(key, String.valueOf(arg));
    }

    public Object dealResponse(EasyClientRequest request, InputStream inputStream, Type returnType) {
        EnumResScheme resScheme = request.getResScheme();
        if (EnumResScheme.INPUT_STREAM.equals(resScheme)) {
            return inputStream;
        }
        Convertor convertor = this.convertorFactory.build(resScheme);
        if (convertor == null) {
            throw new RuntimeException(resScheme.name() + " not mapping any convertor.");
        }
        Object responseEntity = convertor.convert(inputStream, request, returnType);
        try {
            inputStream.close();
        } catch (IOException e) {
            throw new RuntimeException("io close error.", e);
        }
        return responseEntity;
    }

    public EasyResponse<InputStream> sendRequest(EasyClientRequest request, Object requestEntity) {
        call(interceptor -> interceptor.beforeRequest(request, requestEntity));
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

    private static void dealFixedRequestParam(RequestParam requestParam, Map<String, String> params) {
        if (requestParam == null) {
            return;
        }
        String[] names = requestParam.name();
        String[] values = requestParam.fixedValue();
        if (names.length != values.length) {
            throw new RuntimeException("names' array length must same as values' length.");
        }
        for (int i = 0; i < names.length; i++) {
            params.put(names[i], values[i]);
        }
    }

    private void call(Consumer<Interceptor> consumer) {
        if (interceptors != null) {
            for (Interceptor interceptor : interceptors) {
                consumer.accept(interceptor);
            }
        }
    }
}
