package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CompanyDepartment;

/**
 * Created by turik on 18.08.16.
 */
public interface CompanyDepartmentDAO extends PortalBaseDAO<CompanyDepartment> {
    boolean checkExistsByParentId(Long departmentId);
    boolean checkExistsByParent(String extId, Long companyId);
    CompanyDepartment getByExternalId(String extId, Long companyId);
}
