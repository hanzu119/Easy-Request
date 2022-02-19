package com.easy.request.factory;

import com.easy.request.parse.req.RequestParamChecker;

public interface ParamCheckerFactory {

    RequestParamChecker build();
}
