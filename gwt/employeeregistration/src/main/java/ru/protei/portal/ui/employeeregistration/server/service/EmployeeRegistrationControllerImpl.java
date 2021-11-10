package ru.protei.portal.ui.employeeregistration.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.EmployeeRegistrationShortView;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.portal.core.service.EmployeeRegistrationService;
import ru.protei.portal.core.service.EmployeeRegistrationServiceImpl;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.EmployeeRegistrationController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;

import static ru.protei.portal.core.model.helper.CollectionUtils.setOf;

@Service("EmployeeRegistrationController")
public class EmployeeRegistrationControllerImpl implements EmployeeRegistrationController {

    @Override
    public SearchResult<EmployeeRegistration> getEmployeeRegistrations(EmployeeRegistrationQuery query) throws RequestFailedException {
        log.info(" get employee registrations: offset={} | limit={}", query.getOffset(), query.getLimit());
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(employeeRegistrationService.getEmployeeRegistrations(token, query));
    }

    @Override
    public EmployeeRegistration getEmployeeRegistration(Long id) throws RequestFailedException {
        log.info(" get employee registration, id: {}", id);

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<EmployeeRegistration> response = employeeRegistrationService.getEmployeeRegistration(token, id);
        log.info(" get employee registration, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public EmployeeRegistrationShortView getEmployeeRegistrationShortView(Long id) throws RequestFailedException {
        log.info(" get employee registration short view, id: {}", id);

        EmployeeRegistration employeeRegistration  =  getEmployeeRegistration(id);

        if (employeeRegistration == null) {
            return null;
        }

        EmployeeRegistrationShortView employeeRegistrationShortView = new EmployeeRegistrationShortView();
        employeeRegistrationShortView.setId(employeeRegistration.getId());
        employeeRegistrationShortView.setCurators( setOf( employeeRegistration.getCurators() ) );
        employeeRegistrationShortView.setEmploymentDate(employeeRegistration.getEmploymentDate());
        employeeRegistrationShortView.setHeadOfDepartmentId(employeeRegistration.getHeadOfDepartmentId());

        return employeeRegistrationShortView;
    }

    @Override
    public Long createEmployeeRegistration(EmployeeRegistration employeeRegistration) throws RequestFailedException {
        if (employeeRegistration == null) {
            log.warn("null employee registration in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.info("create employee registration, id: {}", HelperFunc.nvlt(employeeRegistration.getId(), "new"));

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        employeeRegistration.setCreatorId(token.getPersonId());
        Result<Long> response = employeeRegistrationService.createEmployeeRegistration(token, employeeRegistration);

        log.info("create employee registration, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.info("create employee registration, applied id: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public Long updateEmployeeRegistration(EmployeeRegistrationShortView employeeRegistration) throws RequestFailedException {
        log.info("updateEmployeeRegistration: employeeRegistration = {}", employeeRegistration);

        if (employeeRegistration == null) {
            log.warn("null employee registration in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<Long> response = employeeRegistrationService.updateEmployeeRegistration(token, employeeRegistration);

        if (response.isOk()) {
            log.info("employee registration successfully updated. id = {}", response.getData());
            return response.getData();
        }

        log.warn("updateEmployeeRegistration: status = {}", response.getStatus());

        throw new RequestFailedException(response.getStatus());
    }

    @Override
    public EmployeeRegistration completeProbationPeriod(Long id) throws RequestFailedException {
        log.info("completeProbationPeriod: employeeRegistrationId = {}", id);

        if (id == null) {
            log.warn("null employee registration id in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);

        Result<EmployeeRegistration> response = employeeRegistrationService.completeProbationPeriod(token, id);

        if (response.isOk()) {
            log.info("employee registration successfully updated. id = {}", response.getData());
            return response.getData();
        }

        log.warn("completeProbationPeriod: status = {}", response.getStatus());

        throw new RequestFailedException(response.getStatus());
    }

    @Autowired
    private EmployeeRegistrationService employeeRegistrationService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger(EmployeeRegistrationServiceImpl.class);
}
