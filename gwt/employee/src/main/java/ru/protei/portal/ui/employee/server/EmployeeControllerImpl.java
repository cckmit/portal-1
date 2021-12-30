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
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.struct.EmployeesBirthdays;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.core.service.EmployeeService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.EmployeeController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.size;

/**
 * Реализация сервиса по работе с сотрудниками
 */
@Service( "EmployeeController" )
public class EmployeeControllerImpl implements EmployeeController {

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

        return ServiceUtils.checkResultAndGetData(result);
    }

    @Override
    public EmployeeShortView getEmployee(Long employeeId) throws RequestFailedException {
        log.info("getEmployee(): employeeId={}", employeeId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.getEmployee(token, employeeId));
    }

    @Override
    public PersonShortView getDepartmentHead(Long departmentId) throws RequestFailedException {
        log.info("getDepartmentHead(): departmentId={}", departmentId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.getDepartmentHead(token, departmentId));
    }

    @Override
    public EmployeeShortView getEmployeeWithChangedHiddenCompanyNames(Long employeeId) throws RequestFailedException {
        log.info("getEmployeeShortViewWithChangedHiddenCompanyNames(): employeeId={}", employeeId);
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.getEmployeeWithChangedHiddenCompanyNames(token, employeeId));
    }

    @Override
    public Person saveEmployee(Person person, List<WorkerEntry> workerEntries, boolean isEditablePerson, boolean needToChangeAccount) throws RequestFailedException {

        if (person == null || CollectionUtils.isEmpty(workerEntries)) {
            log.warn("saveEmployee(): null person or empty workerEntries in request");
            throw new RequestFailedException(En_ResultStatus.INCORRECT_PARAMS);
        }

        log.info("saveEmployee(): save person, id: {} ", HelperFunc.nvl(person.getId()));
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Person> response;
        if (person.getId() == null) {
            response = employeeService.createEmployee(token, person, workerEntries);
        } else if (isEditablePerson) {
            response = employeeService.updateEmployee(token, person, workerEntries, needToChangeAccount);
        } else {
            response = employeeService.updateWorkerEntries(token, person.getId(), workerEntries);
        }

        log.info("saveEmployee(): save person, result: {}", response.isOk() ? "ok" : response.getStatus());
        return ServiceUtils.checkResultAndGetData(response);
    }

    @Override
    public boolean fireEmployee(Person person) throws RequestFailedException {
        log.info("fire employee, id: {}", person.getId());

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);

        Result<Boolean> response = employeeService.fireEmployee(token, person);

        log.info("fire employee, id: {} -> {} ", person.getId(), response.isError() ? response.getStatus() : (response.getData() ? "" : "not ") + "fired");

        return ServiceUtils.checkResultAndGetData(response);
    }

    @Override
    public List<WorkerEntryShortView> getWorkerEntryList(int offset, int limit) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.getWorkerEntryList(token, offset, limit));
    }

    @Override
    public EmployeesBirthdays getEmployeesBirthdays(Date dateFrom, Date dateUntil) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.getEmployeesBirthdays(token, dateFrom, dateUntil));
    }

    @Override
    public String getEmployeeRestVacationDays(List<WorkerEntryShortView> workerEntries) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.getEmployeeRestVacationDays(token, workerEntries));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    private EmployeeService employeeService;

    private static final Logger log = LoggerFactory.getLogger( EmployeeControllerImpl.class );
}
