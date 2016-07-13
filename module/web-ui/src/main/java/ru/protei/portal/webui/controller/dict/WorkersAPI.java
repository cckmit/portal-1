package ru.protei.portal.webui.controller.dict;

import org.springframework.web.bind.annotation.*;
import ru.protei.portal.core.model.view.EmployeeDetailView;
import ru.protei.portal.core.model.view.WorkerView;
import ru.protei.portal.webui.api.struct.HttpListResult;

/**
 * Created by michael on 06.04.16.
 */
@RestController
@RequestMapping(path = "/api")
public interface WorkersAPI {

    @RequestMapping(path = "/gate/employees/list")
    public HttpListResult<WorkerView> list(@RequestParam(name = "q", defaultValue = "") String param);

    @GetMapping("/gate/employees/{id:[0-9]+}.json")
    public EmployeeDetailView getEmployeeProfile (@PathVariable("id") Long id);

    @GetMapping(path = "/gate/employees/{id:[0-9]+}/absences.json", params = {"from", "till"})
    public EmployeeDetailView getEmployeeAbsences(@PathVariable("id") Long id, @RequestParam("from") Long tFrom, @RequestParam("till") Long tTill);
}
