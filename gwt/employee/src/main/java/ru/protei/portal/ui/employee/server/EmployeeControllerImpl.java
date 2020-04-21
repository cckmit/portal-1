package ru.protei.portal.ui.employee.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.WorkerEntry;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.EmployeeService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.EmployeeController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

/**
 * Реализация сервиса по работе с сотрудниками
 */
@Service( "EmployeeController" )
public class EmployeeControllerImpl implements EmployeeController {

    @Override
    public PersonShortView getEmployee(Long employeeId) throws RequestFailedException {
        log.info("getEmployee(): employeeId={}", employeeId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.getEmployee(token, employeeId));
    }
    @Override
    public SearchResult<EmployeeShortView> getEmployees(EmployeeQuery query) throws RequestFailedException {
        log.info("getEmployees(): query={}", query);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.employeeList(token, query));
    }

    @Override
    public SearchResult<EmployeeShortView> getEmployeesWithChangedHiddenCompanyNames(EmployeeQuery query) throws RequestFailedException {
        log.info("getEmployeesWithChangedHiddenCompanyNames(): query={}", query);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.employeeListWithChangedHiddenCompanyNames(token, query));
    }

    public List< PersonShortView > getEmployeeViewList( EmployeeQuery query ) throws RequestFailedException {

        log.info( "getEmployeeViewList(): searchPattern={} | companyId={} | isFired={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getFired(), query.getSortField(), query.getSortDir() );

        Result< List< PersonShortView > > result = employeeService.shortViewList( query );

        log.info( "result status: {}, data-amount: {}", result.getStatus(), size(result.getData()) );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }

    @Override
    public PersonShortView getDepartmentHead(Long departmentId) throws RequestFailedException {
        log.info("getDepartmentHead(): departmentId={}", departmentId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.getDepartmentHead(token, departmentId));
    }

    @Override
    public EmployeeShortView getEmployeeShortView(Long employeeId) throws RequestFailedException {
        log.info("getEmployeeShortView(): employeeId={}", employeeId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.getEmployeeShortView(token, employeeId));
    }

    @Override
    public EmployeeShortView getEmployeeShortViewWithChangedHiddenCompanyNames(Long employeeId) throws RequestFailedException {
        log.info("getEmployeeShortViewWithChangedHiddenCompanyNames(): employeeId={}", employeeId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.getEmployeeShortViewWithChangedHiddenCompanyNames(token, employeeId));
    }


    @Override
    public Person createEmployeePerson(Person person) throws RequestFailedException {

        if (person == null) {
            log.warn("createEmployeePerson(): null person in request");
            throw new RequestFailedException(En_ResultStatus.INCORRECT_PARAMS);
        }

        log.info("createEmployeePerson(): create person, id: {} ", HelperFunc.nvl(person.getId(), "new"));

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Person> response = employeeService.createEmployeePerson( token, person );

        log.info("createEmployeePerson(): create person, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.info("createEmployeePerson(): create person, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Boolean updateEmployeePerson(Person person) throws RequestFailedException {

        if (person == null || person.getId() == null) {
            log.warn("updateEmployeePerson(): null person or null id in request");
            throw new RequestFailedException(En_ResultStatus.INCORRECT_PARAMS);
        }

        log.info("updateEmployeePerson(): update person, id: {} ", HelperFunc.nvl(person.getId()));

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Boolean> response = employeeService.updateEmployeePerson( token, person );

        log.info("updateEmployeePerson(): update person, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public WorkerEntry createEmployeeWorker(WorkerEntry worker) throws RequestFailedException {

        if (worker == null) {
            log.warn("createEmployeeWorker(): null worker in request");
            throw new RequestFailedException(En_ResultStatus.INCORRECT_PARAMS);
        }

        log.info("createEmployeeWorker(): create worker, id: {} ", HelperFunc.nvl(worker.getId(), "new"));

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<WorkerEntry> response = employeeService.createEmployeeWorker( token, worker );

        log.info("createEmployeeWorker(): create worker, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.info("createEmployeeWorker(): create worker, applied id: {}", response.getData().getId());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Boolean updateEmployeeWorker(WorkerEntry worker) throws RequestFailedException {

        if (worker == null || worker.getId() == null) {
            log.warn("updateEmployeeWorker(): null worker or null worker id in request");
            throw new RequestFailedException(En_ResultStatus.INCORRECT_PARAMS);
        }

        log.info("updateEmployeeWorker(): update worker, id: {} ", HelperFunc.nvl(worker.getId()));

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Boolean> response = employeeService.updateEmployeeWorker( token, worker );

        log.info("updateEmployeeWorker(): update worker, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public boolean fireEmployee(Person person) throws RequestFailedException {
        log.info("fire employee, id: {}", person.getId());

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Boolean> response = employeeService.fireEmployee(token, person);

        log.info("fire employee, id: {} -> {} ", person.getId(), response.isError() ? response.getStatus() : (response.getData() ? "" : "not ") + "fired");

        return response.isOk() ? response.getData() : false;
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    private EmployeeService employeeService;

    private static final Logger log = LoggerFactory.getLogger( EmployeeControllerImpl.class );
}
