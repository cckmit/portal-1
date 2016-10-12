package ru.protei.portal.core.service.dict;

import org.springframework.web.bind.annotation.*;
import ru.protei.portal.api.struct.HttpListResult;
import ru.protei.portal.core.model.view.EmployeeDetailView;
import ru.protei.portal.core.model.view.WorkerView;

/**
 * Created by michael on 06.04.16.
 */
public interface EmployeeService {

    HttpListResult<WorkerView> list(String param);

    EmployeeDetailView getEmployeeProfile (Long id);

    EmployeeDetailView getEmployeeAbsences(Long id, Long tFrom, Long tTill, Boolean isFull);

//    @GetMapping(path = "/currentMissingEmployeesIDs.json")
//    public String getCurrentMissingEmployeeIDs();
}
