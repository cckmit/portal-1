package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.query.CompanyDepartmentQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

/**
 * Created by turik on 18.08.16.
 */
public interface CompanyDepartmentDAO extends PortalBaseDAO<CompanyDepartment> {
    boolean checkExistsByParentId(Long departmentId);
    boolean checkExistsByParent(String extId, Long companyId);
    boolean checkExistsByName(String name, Long companyId);
    boolean checkExistsByNameAndDepId(String name, Long companyId, Long departmentId);
    CompanyDepartment getByExternalId(String extId, Long companyId);

    Long getParentDepIdByDepId(Long depId);
    List<Long> getDepIdsByParentDepId(Long parentDepId);

    List<CompanyDepartment> getListByQuery(CompanyDepartmentQuery query);

    @SqlConditionBuilder
    SqlCondition createSqlCondition(CompanyDepartmentQuery query);
}
