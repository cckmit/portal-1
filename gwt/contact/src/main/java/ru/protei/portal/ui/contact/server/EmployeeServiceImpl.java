package ru.protei.portal.ui.contact.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.client.service.EmployeeService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Реализация сервиса по работе с сотрудниками
 */
@Service( "EmployeeService" )
public class EmployeeServiceImpl implements EmployeeService{

    @Override
    public List<Person> getEmployees() throws RequestFailedException {
        CoreResponse<List<Person>> response = employeeService.employeeList();

        if (response.isError()) {
            throw new RequestFailedException( response.getStatus() );
        }

        log.debug( "getEmployees(): isOk={}, size={}", response.isOk(), response.getData().size() );

        return response.getData();
    }

    @Override
    public List<EntityOption> getEmployeeOptionList() throws RequestFailedException {
        log.debug( "getEmployeeOptionList()" );

        CoreResponse< List< EntityOption > > result = employeeService.employeeOptionList();

        log.debug( "result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0 );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Autowired
    ru.protei.portal.core.service.EmployeeService employeeService;

    private static final Logger log = LoggerFactory.getLogger("web");

}
