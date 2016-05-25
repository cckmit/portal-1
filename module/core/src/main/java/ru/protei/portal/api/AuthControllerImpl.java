package ru.protei.portal.api;

import org.jboss.netty.handler.codec.http.HttpResponse;
import org.jboss.netty.handler.codec.http.HttpResponseStatus;

/**
 * Created by michael on 25.05.16.
 */
public class AuthControllerImpl implements AuthController {

    @Override
    public HttpResponse login(String uname, String upass, String sessionId) {

        HttpResponse resp = ru.protei.winter.http.HttpUtils.makeResponse(HttpResponseStatus.OK);
//        resp.addHeader();
        return resp;
    }
}
