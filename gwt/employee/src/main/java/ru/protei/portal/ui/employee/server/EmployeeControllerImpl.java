package ru.protei.portal.ui.employee.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.query.EmployeeQuery;
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
    public List<WorkerEntryShortView> getWorkerEntryList(int offset, int limit) throws RequestFailedException {
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return ServiceUtils.checkResultAndGetData(employeeService.getWorkerEntryList(token, offset, limit));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    private EmployeeService employeeService;

    private static final Logger log = LoggerFactory.getLogger( EmployeeControllerImpl.class );
}
