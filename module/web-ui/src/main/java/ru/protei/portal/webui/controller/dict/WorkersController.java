package ru.protei.portal.webui.controller.dict;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.core.model.view.WorkerView;
import ru.protei.portal.webui.api.HttpListResult;

/**
 * Created by michael on 06.04.16.
 */
@RestController
@RequestMapping(path = "/api")
public interface WorkersController {

    @RequestMapping(path = "/gate/workers/list")
    public HttpListResult<WorkerView> list(@RequestParam(name = "q", defaultValue = "") String param);

}
