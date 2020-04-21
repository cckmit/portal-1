package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanyDepartmentDAO;
import ru.protei.portal.core.model.ent.CompanyDepartment;

import java.util.List;

/**
 * Created by turik on 18.08.16.
 */
public class CompanyDepartmentDAO_Impl extends PortalBaseJdbcDAO<CompanyDepartment> implements CompanyDepartmentDAO {
    @Override
    public boolean checkExistsByParentId(Long departmentId) {
        return checkExistsByCondition ("company_dep.parent_dep=?", departmentId);
    }

    @Override
    public boolean checkExistsByParent(String extId, Long companyId) {
        return checkExistsByCondition ("company_dep.parent_dep=(select id from company_dep cd where cd.dep_extId=? and cd.company_id=?)", extId, companyId);
    }

    @Override
    public boolean checkExistsByName(String name, Long companyId) {
        return checkExistsByCondition ("company_dep.dep_name=? and company_dep.company_id=?", name, companyId);
    }

    @Override
    public boolean checkExistsByNameAndDepId(String name, Long companyId, Long departmentId) {
        return checkExistsByCondition ("company_dep.dep_name=? and company_dep.company_id=? and company_dep.id!=?", name, companyId, departmentId);
    }

    @Override
    public CompanyDepartment getByExternalId(String extId, Long companyId) {
        return getByCondition ("company_dep.dep_extId=? and company_dep.company_id=?", extId, companyId);
    }

    @Override
    public List<CompanyDepartment> getListByCompanyId(Long companyId) {
        return getListByCondition ("company_dep.company_id=?", companyId);
    }

}
