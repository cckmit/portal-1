package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.service.CompanyDepartmentService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.CompanyDepartmentController;
import ru.protei.portal.ui.common.server.ServiceUtils;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.checkResultAndGetData;

@Service("CompanyDepartmentController")
public class CompanyDepartmentControllerImpl implements CompanyDepartmentController {

    @Override
    public List<CompanyDepartment> getCompanyDepartments(Long companyId) throws RequestFailedException {
        log.info("getCompanyDepartments(): companyId={}", companyId);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(companyDepartmentService.getCompanyDepartments(authToken, companyId));
    }

    @Override
    public Long removeCompanyDepartment(CompanyDepartment companyDepartment) throws RequestFailedException {
        log.info("removeCompanyDepartment(): companyDepartment={}", companyDepartment);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(companyDepartmentService.removeCompanyDepartment(authToken, companyDepartment));
    }

    @Override
    public Long createCompanyDepartment(CompanyDepartment companyDepartment) throws RequestFailedException {
        log.info("createCompanyDepartment(): companyDepartment={}", companyDepartment);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(companyDepartmentService.createCompanyDepartment(authToken, companyDepartment));
    }

    @Override
    public Long updateCompanyDepartmentName(CompanyDepartment companyDepartment) throws RequestFailedException {
        log.info("updateCompanyDepartmentName(): companyDepartment={}", companyDepartment);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(companyDepartmentService.updateCompanyDepartmentName(authToken, companyDepartment));
    }

    @Override
    public List<EntityOption> getPersonDepartments(Long personId, boolean withParentDepartments) throws RequestFailedException {
        log.info("getPersonDepartments(): personId={}, withParentDepartments={}", personId, withParentDepartments);
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(companyDepartmentService.getPersonDepartments(authToken, personId, withParentDepartments));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    CompanyDepartmentService companyDepartmentService;

    private static final Logger log = LoggerFactory.getLogger(CompanyDepartmentControllerImpl.class);
}
