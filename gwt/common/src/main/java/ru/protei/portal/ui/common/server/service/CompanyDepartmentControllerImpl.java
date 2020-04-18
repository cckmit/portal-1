package ru.protei.portal.ui.common.server.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CompanyDepartment;
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
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(companyDepartmentService.getCompanyDepartments(authToken, companyId));
    }

    @Override
    public Long removeCompanyDepartment(CompanyDepartment companyDepartment) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(companyDepartmentService.removeCompanyDepartments(authToken, companyDepartment));
    }

    @Override
    public Long createCompanyDepartment(CompanyDepartment companyDepartment) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(companyDepartmentService.createCompanyDepartments(authToken, companyDepartment));
    }

    @Override
    public Long updateCompanyDepartment(CompanyDepartment companyDepartment) throws RequestFailedException {
        AuthToken authToken = ServiceUtils.getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(companyDepartmentService.updateCompanyDepartments(authToken, companyDepartment));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    CompanyDepartmentService companyDepartmentService;
}
