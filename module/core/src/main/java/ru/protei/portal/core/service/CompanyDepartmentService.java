package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;

/**
 * Сервис управления отделами
 */
public interface CompanyDepartmentService {

    @Privileged(requireAny = {En_Privilege.EMPLOYEE_CREATE, En_Privilege.EMPLOYEE_EDIT})
    Result<List<CompanyDepartment>> getCompanyDepartments(AuthToken token, Long companyId);

    @Auditable(En_AuditType.DEPARTMENT_CREATE)
    @Privileged(requireAny = {En_Privilege.EMPLOYEE_CREATE})
    Result<Long> createCompanyDepartment(AuthToken token, CompanyDepartment companyDepartment);

    @Auditable(En_AuditType.DEPARTMENT_MODIFY)
    @Privileged(requireAny = {En_Privilege.EMPLOYEE_CREATE, En_Privilege.EMPLOYEE_EDIT})
    Result<Long> updateCompanyDepartmentName(AuthToken token, CompanyDepartment companyDepartment);

    @Auditable(En_AuditType.DEPARTMENT_REMOVE)
    @Privileged(requireAny = {En_Privilege.EMPLOYEE_CREATE, En_Privilege.EMPLOYEE_EDIT})
    Result<Boolean> removeCompanyDepartment(AuthToken token, CompanyDepartment companyDepartment);

    Result<List<EntityOption>> getPersonDepartments(AuthToken authToken, Long personId, boolean withParentDepartments);
}
