package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.ContactQuery;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EmployeeDetailView;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.WorkerView;

import java.util.List;

/**
 * Сервис управления сотрудниками
 */
public interface EmployeeService {

    CoreResponse<List<PersonShortView>> shortViewList(EmployeeQuery query);
    CoreResponse<List<WorkerView>> list(String param);
    CoreResponse<List<Person>> employeeList();
    EmployeeDetailView getEmployeeProfile (Long id);
    EmployeeDetailView getEmployeeAbsences(Long id, Long tFrom, Long tTill, Boolean isFull);

//    @GetMapping(path = "/currentMissingEmployeesIDs.json")
//    public String getCurrentMissingEmployeeIDs();
}
