package com.ionrhythm.easy.request.factory;

import com.ionrhythm.easy.request.parse.request.RequestParamChecker;

public interface ParamCheckerFactory {

    RequestParamChecker build();
}
