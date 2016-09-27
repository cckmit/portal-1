package ru.protei.portal.webui.controller.dict;

import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.view.EmployeeDetailView;
import ru.protei.portal.core.model.view.WorkerView;

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

    @GetMapping(path = "/gate/employees/{id:[0-9]+}/absences.json", params = {"from", "till", "full"})
    public EmployeeDetailView getEmployeeAbsences(@PathVariable("id") Long id, @RequestParam("from") Long tFrom, @RequestParam("till") Long tTill, @RequestParam("full") Boolean isFull);

    @GetMapping(path = "/gate/currentMissingEmployeesIDs.json")
    public String getCurrentMissingEmployeeIDs();
}
