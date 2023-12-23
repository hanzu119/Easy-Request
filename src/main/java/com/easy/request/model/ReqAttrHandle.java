package com.easy.request.model;

import com.easy.request.annotation.DELETE;
import com.easy.request.annotation.GET;
import com.easy.request.annotation.POST;
import com.easy.request.annotation.PUT;
import com.easy.request.annotation.PathVariable;
import com.easy.request.annotation.RecordOrigin;
import com.easy.request.annotation.Request;
import com.easy.request.client.EasyClientRequest;
import com.easy.request.constant.EasyCodes;
import com.easy.request.constant.EnumMethod;
import com.easy.request.constant.EnumReqScheme;
import com.easy.request.constant.EnumResScheme;
import com.easy.request.util.StringUtils;

import java.lang.reflect.Method;

public class ReqAttrHandle {
    private final StringBuilder url = new StringBuilder();
    private final ReqAttrModel model = new ReqAttrModel();

    private ReqAttrHandle(final Class<?> interfaceClass, Method method) {
        Request interfaceEasyRequest = interfaceClass.getAnnotation(Request.class);
        PathVariable pathVariable = method.getAnnotation(PathVariable.class);
        Request methodEasyRequest = method.getAnnotation(Request.class);
        GET get = method.getAnnotation(GET.class);
        POST post = method.getAnnotation(POST.class);
        PUT put = method.getAnnotation(PUT.class);
        DELETE delete = method.getAnnotation(DELETE.class);

        RecordOrigin interfaceRecord = interfaceClass.getAnnotation(RecordOrigin.class);
        RecordOrigin methodRecord = method.getAnnotation(RecordOrigin.class);
        dealRecordOrigin(interfaceRecord, methodRecord);

        dealInterfaceRequest(interfaceEasyRequest);
        if (methodEasyRequest != null) {
            buildMethodRequest(methodEasyRequest);
        } else if (get != null) {
            buildGet(get);
        } else if (post != null) {
            buildPost(post);
        } else if (put != null) {
            buildPut(put);
        } else if (delete != null) {
            buildDelete(delete);
        } else {
            throw new RuntimeException("method must appoint easy request annotation.");
        }
        dealPath(pathVariable);
        EnumReqScheme reqScheme = model.getReqScheme();
        String contentType = model.getContentType();
        if (StringUtils.isBlank(contentType)) {
            if (EnumReqScheme.JSON.equals(reqScheme) || EnumReqScheme.EMPTY.equals(reqScheme)) {
                model.setContentType("application/json");
            } else if (EnumReqScheme.XML.equals(reqScheme)) {
                model.setContentType("text/xml");
            }
        }
    }

    public static ReqAttrHandle builder(Class<?> interfaceClass, Method method) {
        return new ReqAttrHandle(interfaceClass, method);
    }

    public void fillClientRequest(EasyClientRequest request) {
        request.setMethod(model.getMethod());
        request.setPath(model.getPath());
        request.setReqScheme(model.getReqScheme());
        request.setRequestCharset(model.getReqCharset());
        request.setResScheme(model.getResScheme());
        request.setResponseCharset(model.getResCharset());
        request.setTimeout(model.getTimeout());
        request.setContentType(model.getContentType());
        request.setRecordOrigin(model.getRecordOrigin());
    }

    private void dealInterfaceRequest(Request interfaceEasyRequest) {
        if (interfaceEasyRequest != null) {
            model.setTimeout(interfaceEasyRequest.timeout());
            url.append(StringUtils.defaultIfBlank(interfaceEasyRequest.path(), interfaceEasyRequest.value()));
            EnumReqScheme reqScheme = interfaceEasyRequest.requestScheme();
            EnumResScheme resScheme = interfaceEasyRequest.responseScheme();
            fillRequestInfo(reqScheme, interfaceEasyRequest.requestCharset(), resScheme, interfaceEasyRequest.responseCharset());
        }
    }

    private void buildMethodRequest(Request methodEasyRequest) {
        model.setMethod(methodEasyRequest.method());
        model.setContentType(methodEasyRequest.contentType());
        model.setTimeout(methodEasyRequest.timeout());
        appendPath(methodEasyRequest.path(), methodEasyRequest.value());
        fillRequestInfo(methodEasyRequest.requestScheme(), methodEasyRequest.requestCharset(),
                methodEasyRequest.responseScheme(), methodEasyRequest.responseCharset());
    }


    private void buildGet(GET get) {
        model.setMethod(EnumMethod.GET);
        model.setContentType(get.contentType());
        model.setTimeout(get.timeout());
        appendPath(get.path(), get.value());
        fillRequestInfo(EnumReqScheme.FORM, EasyCodes.DEFAULT_CHARSET, get.responseScheme(), get.responseCharset());

    }

    private void buildPost(POST post) {
        model.setMethod(EnumMethod.POST);
        model.setContentType(post.contentType());
        model.setTimeout(post.timeout());
        appendPath(post.path(), post.value());
        fillRequestInfo(post.requestScheme(), post.requestCharset(), post.responseScheme(), post.responseCharset());

    }

    private void buildPut(PUT put) {
        model.setMethod(EnumMethod.PUT);
        model.setContentType(put.contentType());
        model.setTimeout(put.timeout());
        appendPath(put.path(), put.value());
        fillRequestInfo(put.requestScheme(), put.requestCharset(), put.responseScheme(), put.responseCharset());

    }

    private void buildDelete(DELETE delete) {
        model.setMethod(EnumMethod.DELETE);
        model.setContentType(delete.contentType());
        model.setTimeout(delete.timeout());
        appendPath(delete.path(), delete.value());
        fillRequestInfo(EnumReqScheme.FORM, EasyCodes.DEFAULT_CHARSET, delete.responseScheme(), delete.responseCharset());

    }

    private void dealRecordOrigin(RecordOrigin infc, RecordOrigin method) {
        if (infc != null) {
            model.setRecordOrigin(infc.enable());
        }
        if (method != null) {
            model.setRecordOrigin(method.enable());
        }
    }

    private void fillRequestInfo(EnumReqScheme reqScheme, String reqCharset, EnumResScheme resScheme, String resCharset) {
        if (reqScheme.isNotEmpty()) {
            model.setReqScheme(reqScheme);
        }
        if (resScheme.isNotEmpty()) {
            model.setResScheme(resScheme);
        }
        if (StringUtils.isNotBlank(reqCharset)) {
            model.setReqCharset(reqCharset);
        }
        if (StringUtils.isNotBlank(resCharset)) {
            model.setResCharset(resCharset);
        }
    }

    private void appendPath(String path, String value) {
        String pathValue = StringUtils.defaultIfBlank(path, value);
        if (StringUtils.isNotBlank(pathValue)) {
            url.append(EasyCodes.PATH_SPLIT).append(pathValue);
        }
    }

    private void dealPath(PathVariable pathVariable) {
        String path = url.toString();
        if (path.endsWith(EasyCodes.PATH_SPLIT)) {
            path = path.substring(0, path.length() - 1);
        }
        if (pathVariable != null) {
            String name = StringUtils.defaultIfBlank(pathVariable.name(), pathVariable.value());
            model.setPath(StringUtils.replacePlaceholder(name, path, pathVariable.fixedValue()));
            return;
        }
        model.setPath(path);
    }
}
