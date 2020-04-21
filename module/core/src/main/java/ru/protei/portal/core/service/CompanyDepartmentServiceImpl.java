package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CompanyDepartmentDAO;
import ru.protei.portal.core.model.dao.WorkerEntryDAO;
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
    WorkerEntryDAO workerEntryDAO;

    @Autowired
    PolicyService policyService;

    @Override
    public Result<List<CompanyDepartment>> getCompanyDepartments(AuthToken token, Long companyId) {
        List<CompanyDepartment> list = companyDepartmentDAO.getListByCompanyId(companyId);
        return Result.ok(list);
    }

    @Override
    public Result<Long> createCompanyDepartment(AuthToken token, CompanyDepartment companyDepartment) {
        if (!isValid(companyDepartment)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (companyDepartmentDAO.checkExistsByName(companyDepartment.getName(), companyDepartment.getCompanyId())){
            return error(En_ResultStatus.DEPARTMENT_ALREADY_EXIST);
        }

        companyDepartment.setCreated(new Date());
        Long companyDepartmentId = companyDepartmentDAO.persist(companyDepartment);

        if (companyDepartmentId == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        return ok(companyDepartmentId);
    }

    @Override
    public Result<Long> updateCompanyDepartment(AuthToken token, CompanyDepartment companyDepartment) {
        if (!isValid(companyDepartment)) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (companyDepartmentDAO.checkExistsByNameAndDepId(companyDepartment.getName(), companyDepartment.getCompanyId(), companyDepartment.getId())){
            return error(En_ResultStatus.DEPARTMENT_ALREADY_EXIST);
        }

        boolean result = companyDepartmentDAO.partialMerge(companyDepartment, "dep_name");

        if ( !result )
            return error(En_ResultStatus.NOT_UPDATED);

        return ok(companyDepartment.getId());
    }

    @Override
    public Result<Long> removeCompanyDepartment(AuthToken token, CompanyDepartment companyDepartment) {

        if(workerEntryDAO.checkExistsByDepId(companyDepartment.getId())){
            return error(En_ResultStatus.WORKER_WITH_THIS_DEPARTMENT_ALREADY_EXIST);
        }

        boolean result = companyDepartmentDAO.removeByKey(companyDepartment.getId());

        if ( !result )
            return error(En_ResultStatus.NOT_REMOVED);

        return ok(companyDepartment.getId());
    }

    private boolean isValid (CompanyDepartment companyDepartment){
        return companyDepartment.getCompanyId() != null && StringUtils.isNotEmpty(companyDepartment.getName());
    }
}
