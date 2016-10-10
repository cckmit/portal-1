package ru.protei.portal.core.service.dict;

import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.view.EmployeeDetailView;
import ru.protei.portal.core.model.view.WorkerView;

/**
 * Created by michael on 06.04.16.
 */
@RestController
@RequestMapping(path = "/api/gate/employee")
public interface EmployeeService {

    @RequestMapping(path = "/list")
    public HttpListResult<WorkerView> list(@RequestParam(name = "q", defaultValue = "") String param);

    @GetMapping("/profile/{id:[0-9]+}.json")
    public EmployeeDetailView getEmployeeProfile (@PathVariable("id") Long id);

    @GetMapping(path = "/profile/{id:[0-9]+}/absences.json", params = {"from", "till", "full"})
    public EmployeeDetailView getEmployeeAbsences(@PathVariable("id") Long id, @RequestParam("from") Long tFrom, @RequestParam("till") Long tTill, @RequestParam("full") Boolean isFull);

//    @GetMapping(path = "/currentMissingEmployeesIDs.json")
//    public String getCurrentMissingEmployeeIDs();
}
