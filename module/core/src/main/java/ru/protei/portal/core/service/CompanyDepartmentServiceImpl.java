package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CompanyDepartmentDAO;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.service.policy.PolicyService;

import java.util.List;

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
}
