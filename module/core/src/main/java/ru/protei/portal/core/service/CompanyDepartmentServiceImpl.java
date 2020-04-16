package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CompanyDepartmentDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.service.policy.PolicyService;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class CompanyDepartmentServiceImpl implements CompanyDepartmentService{

    @Autowired
    CompanyDepartmentDAO companyDepartmentDAO;

    @Autowired
    PolicyService policyService;

    @Override
    public Result<List<CompanyDepartment>> getCompanyDepartments(AuthToken token, Long companyId) {
        List<CompanyDepartment> list = companyDepartmentDAO.getListByCompanyId(companyId);
        return Result.ok(list);
    }

    @Override
    public Result<Long> createCompanyDepartments(AuthToken token, CompanyDepartment companyDepartment) {
        if (!isValid(companyDepartment)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        companyDepartment.setCreated(new Date());
        Long companyDepartmentId = companyDepartmentDAO.persist(companyDepartment);

        if (companyDepartmentId == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        return ok(companyDepartmentId);
    }

    @Override
    public Result<Long> updateCompanyDepartments(AuthToken token, CompanyDepartment companyDepartment) {
        if (!isValid(companyDepartment)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }
        boolean result = companyDepartmentDAO.merge(companyDepartment);

        if ( !result )
            return error(En_ResultStatus.NOT_UPDATED);

        return ok(companyDepartment.getId());
    }

    @Override
    public Result<Long> removeCompanyDepartments(AuthToken token, Long companyDepartmentId) {
        boolean result = companyDepartmentDAO.removeByKey(companyDepartmentId);

        if ( !result )
            return error(En_ResultStatus.NOT_REMOVED);

        return ok(companyDepartmentId);
    }

    private boolean isValid (CompanyDepartment companyDepartment){
        return companyDepartment.getCompanyId() != null && StringUtils.isNotEmpty(companyDepartment.getName());
    }
}
