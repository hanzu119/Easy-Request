package com.easy.request;

import com.easy.request.annotation.HTTP;
import com.easy.request.annotation.Request;
import com.easy.request.constant.EnumProtocol;

/**
 * @author AGPg
 */
@HTTP(host = "127.0.0.1", port = 8080, protocol = EnumProtocol.HTTP)
@Request("easy/test")
public interface EasyTestClient {


}
