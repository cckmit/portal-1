package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.EmployeeDetailView;
import ru.protei.portal.core.model.view.WorkerView;

import java.util.List;

/**
 * Created by michael on 06.04.16.
 */
public interface EmployeeService {

    CoreResponse<List<WorkerView>> list(String param);

    CoreResponse<List<Person>> employeeList();

    EmployeeDetailView getEmployeeProfile (Long id);

    EmployeeDetailView getEmployeeAbsences(Long id, Long tFrom, Long tTill, Boolean isFull);

//    @GetMapping(path = "/currentMissingEmployeesIDs.json")
//    public String getCurrentMissingEmployeeIDs();
}
