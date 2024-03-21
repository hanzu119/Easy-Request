package com.easy.request.factory;

import com.easy.request.parse.request.RequestParamChecker;

public interface ParamCheckerFactory {

    RequestParamChecker build();
}
