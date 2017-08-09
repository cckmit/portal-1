package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanyDepartmentDAO;
import ru.protei.portal.core.model.ent.CompanyDepartment;

/**
 * Created by turik on 18.08.16.
 */
public class CompanyDepartmentDAO_Impl extends PortalBaseJdbcDAO<CompanyDepartment> implements CompanyDepartmentDAO {
    @Override
    public boolean checkExistsByExternalId(Long extId, Long companyId) {
        return checkExistsByCondition ("dep_extId=? and company_id=?", extId, companyId);
    }

    @Override
    public boolean checkExistsByParentId(Long departmentId) {
        return checkExistsByCondition ("parent_dep=?", departmentId);
    }

    @Override
    public CompanyDepartment getByExternalId(Long extId, Long companyId) {
        return getByCondition ("dep_extId=? and company_id=?", extId, companyId);
    }
}
