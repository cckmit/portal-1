package ru.protei.portal.webui.api;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.protei.portal.core.model.view.WorkerView;

/**
 * Created by michael on 06.04.16.
 */
@RestController
public interface WorkersController {

    @RequestMapping(path = "/api/workers/list")
    public HttpListResult<WorkerView> list(@RequestParam(name = "q", defaultValue = "") String param);

}
