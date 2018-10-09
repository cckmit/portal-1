package ru.protei.portal.ui.employeeregistration.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.EmployeeRegistrationQuery;
import ru.protei.portal.core.service.CaseService;
import ru.protei.portal.core.service.EmployeeRegistrationService;
import ru.protei.portal.core.service.EmployeeRegistrationServiceImpl;
import ru.protei.portal.ui.common.client.service.EmployeeRegistrationController;
import ru.protei.portal.ui.common.server.service.SessionService;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

@Service("EmployeeRegistrationController")
public class EmployeeRegistrationControllerImpl implements EmployeeRegistrationController {
    @Override
    public List<EmployeeRegistration> getEmployeeRegistrations(EmployeeRegistrationQuery query) throws RequestFailedException {
        log.debug(" get employee registrations: offset={} | limit={}", query.getOffset(), query.getLimit());
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        CoreResponse<List<EmployeeRegistration>> response = employeeRegistrationService.employeeRegistrationList(descriptor.makeAuthToken(), query);

        if (response.isError()) {
            throw new RequestFailedException(response.getStatus());
        }
        return response.getData();
    }

    @Override
    public Integer getEmployeeRegistrationCount(EmployeeRegistrationQuery query) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug(" get employee registration count(): query={}", query);
        return employeeRegistrationService.count(descriptor.makeAuthToken(), query).getData();
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

    @Override
    public List<CaseComment> getEmployeeRegistrationComments(long id) throws RequestFailedException {
        UserSessionDescriptor descriptor = getDescriptorAndCheckSession();

        log.debug("getEmployeeRegistrationComments(): id={}", id);
        CoreResponse<List<CaseComment>> response = caseService.getEmployeeRegistrationCommentList(descriptor.makeAuthToken(), id);
        if (response.isError())
            throw new RequestFailedException(response.getStatus());
        return response.getData();
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
    CaseService caseService;

    @Autowired
    HttpServletRequest httpRequest;

    private static final Logger log = LoggerFactory.getLogger(EmployeeRegistrationServiceImpl.class);
}
