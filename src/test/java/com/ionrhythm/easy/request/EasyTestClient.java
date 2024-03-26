package com.ionrhythm.easy.request;

import com.ionrhythm.easy.request.annotation.HTTP;
import com.ionrhythm.easy.request.annotation.Request;
import com.ionrhythm.easy.request.constant.EnumProtocol;

/**
 * @author AGPg
 */
@HTTP(host = "127.0.0.1", port = 8080, protocol = EnumProtocol.HTTP)
@Request("easy/test")
public interface EasyTestClient {


}
