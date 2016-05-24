package ru.protei.portal.api;

import ru.protei.portal.core.model.view.WorkerView;
import ru.protei.winter.http.annotations.HttpMethod;
import ru.protei.winter.http.annotations.HttpParam;
import ru.protei.winter.http.annotations.HttpService;
import ru.protei.winter.http.converter.HttpBodyConverterJson;

import static ru.protei.winter.http.annotations.HttpParam.Mode.OPTIONAL;

/**
 * Created by michael on 06.04.16.
 */
@HttpService(baseUrl = "/controller")
public interface WorkersController {

    @HttpMethod(url = "/workers/list", resultConverter = HttpBodyConverterJson.class)
    public HttpListResult<WorkerView> list(@HttpParam(name = "q", mode = OPTIONAL) String param);

}
