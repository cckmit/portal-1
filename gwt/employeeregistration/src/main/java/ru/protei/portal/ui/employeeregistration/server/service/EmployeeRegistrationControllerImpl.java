package ru.protei.portal.ui.employeeregistration.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.portal.core.service.EmployeeRegistrationService;
import ru.protei.portal.core.service.EmployeeRegistrationServiceImpl;
import ru.protei.portal.ui.common.client.service.EmployeeRegistrationController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import javax.servlet.http.HttpServletRequest;

@Service("EmployeeRegistrationController")
public class EmployeeRegistrationControllerImpl implements EmployeeRegistrationController {

    @Override
    public SearchResult<EmployeeRegistration> getEmployeeRegistrations(EmployeeRegistrationQuery query) throws RequestFailedException {
        log.debug(" get employee registrations: offset={} | limit={}", query.getOffset(), query.getLimit());
        AuthToken token = ServiceUtils.getAuthToken(sessionService, httpRequest);
        return ServiceUtils.checkResultAndGetData(employeeRegistrationService.getSearchResult(token, query));
    }

    @Override
    public EmployeeRegistration getEmployeeRegistration(Long id) throws RequestFailedException {
        log.debug(" get employee registration, id: {}", id);

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<EmployeeRegistration> response = employeeRegistrationService.getEmployeeRegistration(descriptor.makeAuthToken(), id);
        log.debug(" get employee registration, id: {} -> {} ", id, response.isError() ? "error" : response.getData());

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Long createEmployeeRegistration(EmployeeRegistration employeeRegistration) throws RequestFailedException {
        if (employeeRegistration == null) {
            log.warn("null employee registration in request");
            throw new RequestFailedException(En_ResultStatus.INTERNAL_ERROR);
        }

        log.debug("create employee registration, id: {}", HelperFunc.nvlt(employeeRegistration.getId(), "new"));

        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        employeeRegistration.setCreatorId(descriptor.getPerson().getId());
        CoreResponse<Long> response = employeeRegistrationService.createEmployeeRegistration(descriptor.makeAuthToken(), employeeRegistration);

        log.debug("create employee registration, result: {}", response.isOk() ? "ok" : response.getStatus());

        if (response.isOk()) {
            log.debug("create employee registration, applied id: {}", response.getData());
            return response.getData();
        }

        throw new RequestFailedException(response.getStatus());
    }

    private UserSessionDescriptor getDescriptorAndCheckSession() throws RequestFailedException {
        UserSessionDescriptor descriptor = sessionService.getUserSessionDescriptor(httpRequest);
        log.info("userSessionDescriptor={}", descriptor);
        if (descriptor == null) {
            throw new RequestFailedException(En_ResultStatus.SESSION_NOT_FOUND);
        }

        return descriptor;
    }

    @Autowired
    private EmployeeRegistrationService employeeRegistrationService;

    @Autowired
    SessionService sessionService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger(EmployeeRegistrationServiceImpl.class);
}
