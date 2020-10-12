package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CompanyDepartmentDAO;
import ru.protei.portal.core.model.dao.WorkerEntryDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CompanyDepartmentQuery;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class CompanyDepartmentServiceImpl implements CompanyDepartmentService{

    @Autowired
    private CompanyDepartmentDAO companyDepartmentDAO;

    @Autowired
    private WorkerEntryDAO workerEntryDAO;

    @Override
    public Result<List<CompanyDepartment>> getCompanyDepartments(AuthToken token, Long companyId) {
        List<CompanyDepartment> list = companyDepartmentDAO.getListByQuery(new CompanyDepartmentQuery(companyId, null));
        return Result.ok(list);
    }

    @Override
    @Transactional
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
    @Transactional
    public Result<Long> updateCompanyDepartmentName(AuthToken token, CompanyDepartment companyDepartment) {
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
    @Transactional
    public Result<Long> removeCompanyDepartment(AuthToken token, CompanyDepartment companyDepartment) {

        if(workerEntryDAO.checkExistsByDepId(companyDepartment.getId())){
            return error(En_ResultStatus.WORKER_WITH_THIS_DEPARTMENT_ALREADY_EXIST);
        }

        boolean result = companyDepartmentDAO.removeByKey(companyDepartment.getId());

        if ( !result )
            return error(En_ResultStatus.NOT_REMOVED);

        return ok(companyDepartment.getId());
    }

    @Override
    public Result<List<EntityOption>> getPersonDepartments(AuthToken authToken, Long personId, boolean withParentDepartments) {
        if (personId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        List<CompanyDepartment> personDepartments = companyDepartmentDAO.getListByQuery(new CompanyDepartmentQuery(null, personId));
        if ( personDepartments == null ) {
            return ok(null);
        }
        if (withParentDepartments) {
            Set<Long> parentDepartmentsIds = personDepartments.stream()
                    .map(CompanyDepartment::getParentId)
                    .filter(Objects::nonNull)
                    .collect(Collectors.toSet());
            List<CompanyDepartment> parentDepartments = companyDepartmentDAO.getListByQuery(new CompanyDepartmentQuery(parentDepartmentsIds));
            personDepartments.addAll(parentDepartments);
        }

        List<EntityOption> options = personDepartments.stream().map( CompanyDepartment::toOption ).collect(Collectors.toList());
        return ok(options);
    }

    private boolean isValid (CompanyDepartment companyDepartment){
        return companyDepartment.getCompanyId() != null && StringUtils.isNotEmpty(companyDepartment.getName());
    }
}
