package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CompanyDepartment;

import java.util.List;

/**
 * Сервис управления отделами
 */
public interface CompanyDepartmentService {

    @Privileged({ En_Privilege.EMPLOYEE_VIEW })
    Result<List<CompanyDepartment>> getCompanyDepartments(AuthToken token, Long companyId);
}
