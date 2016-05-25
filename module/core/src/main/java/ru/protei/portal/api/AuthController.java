package ru.protei.portal.api;

import ru.protei.winter.http.annotations.HttpHeader;
import ru.protei.winter.http.annotations.HttpMethod;
import ru.protei.winter.http.annotations.HttpParam;
import ru.protei.winter.http.annotations.HttpService;

import static ru.protei.winter.http.annotations.HttpParam.Mode.MANDATORY;

/**
 * Created by michael on 25.05.16.
 */
@HttpService(baseUrl = "/controller")
public interface AuthController {

    @HttpMethod(url = "/auth")
    public org.jboss.netty.handler.codec.http.HttpResponse login(
            @HttpParam(name = "uname", mode = MANDATORY) String uname,
            @HttpParam(name = "upass", mode = MANDATORY) String upass,
            @HttpHeader(name = "psessionid", mode = HttpHeader.Mode.OPTIONAL) String sessionId
    );

}
