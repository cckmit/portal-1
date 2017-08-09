package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CompanyDepartment;

/**
 * Created by turik on 18.08.16.
 */
public interface CompanyDepartmentDAO extends PortalBaseDAO<CompanyDepartment> {
    boolean checkExistsByExternalId(Long extId, Long companyId);
    boolean checkExistsByParentId(Long departmentId);
    CompanyDepartment getByExternalId(Long extId, Long companyId);
}
