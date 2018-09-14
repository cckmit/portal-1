package ru.protei.portal.ui.employee.server;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.core.service.EmployeeService;
import ru.protei.portal.ui.common.client.service.EmployeeController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

/**
 * Реализация сервиса по работе с сотрудниками
 */
@Service( "EmployeeController" )
public class EmployeeControllerImpl implements EmployeeController {

    @Override
    public List< EmployeeShortView > getEmployees( EmployeeQuery query ) throws RequestFailedException {
        log.debug( "getEmployees(): query={}", query );

        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor( httpServletRequest );
        log.info( "userSessionDescriptor={}", descriptor );
        if ( descriptor == null ) {
            throw new RequestFailedException( En_ResultStatus.SESSION_NOT_FOUND );
        }

        CoreResponse< List< EmployeeShortView > > response = employeeService.employeeList( descriptor.makeAuthToken(), query );

        if ( response.isError() ) {
            throw new RequestFailedException( response.getStatus() );
        }

        log.debug( "getEmployees(): isOk={}, size={}", response.isOk(), response.getData().size() );

        return response.getData();
    }

    public List< PersonShortView > getEmployeeViewList( EmployeeQuery query ) throws RequestFailedException {

        log.debug( "getEmployeeViewList(): searchPattern={} | companyId={} | isFired={} | sortField={} | sortDir={}",
                query.getSearchString(), query.getFired(), query.getSortField(), query.getSortDir() );

        CoreResponse< List< PersonShortView > > result = employeeService.shortViewList( query );

        log.debug( "result status: {}, data-amount: {}", result.getStatus(), result.isOk() ? result.getDataAmountTotal() : 0 );

        if ( result.isError() )
            throw new RequestFailedException( result.getStatus() );

        return result.getData();
    }


    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    private EmployeeService employeeService;

    private static final Logger log = LoggerFactory.getLogger( EmployeeControllerImpl.class );
}
