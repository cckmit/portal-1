package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CompanyDepartmentDAO;
import ru.protei.portal.core.model.ent.CompanyDepartment;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CompanyDepartmentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class CompanyDepartmentDAO_Impl extends PortalBaseJdbcDAO<CompanyDepartment> implements CompanyDepartmentDAO {

    private final static String WORKER_ENTRY_JOIN = "LEFT JOIN worker_entry WE ON WE.dep_id = company_dep.id";

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
    public Long getParentDepIdByDepId(Long depId) {
        return partialGetByCondition("company_dep.id = ?", Collections.singletonList(depId), "id", "parent_dep")
                .getParentId();
    }

    @Override
    public List<Long> getDepIdsByParentDepId(Long parentDepId) {
        return stream(partialGetListByCondition("company_dep.parent_dep = ?", Collections.singletonList(parentDepId), "id", "parent_dep"))
                .map(CompanyDepartment::getId)
                .collect(Collectors.toList());
    }

    @Override
    public List<CompanyDepartment> getListByQuery(CompanyDepartmentQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getList(parameters);
    }

    private JdbcQueryParameters buildJdbcQueryParameters(CompanyDepartmentQuery query) {
        SqlCondition where = createSqlCondition(query);
        JdbcQueryParameters parameters = new JdbcQueryParameters().withCondition(where.condition, where.args);
        if (query.getPersonId() != null) {
            parameters.withJoins(WORKER_ENTRY_JOIN);
        }
        return parameters;
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CompanyDepartmentQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getCompanyId() != null) {
                condition.append(" and company_dep.company_id=?");
                args.add(query.getCompanyId());
            }

            if (query.getPersonId() != null) {
                condition.append(" and WE.personId=?");
                args.add(query.getPersonId());
            }

            if(CollectionUtils.isNotEmpty(query.getDepartmentsIds())) {
                condition.append(" and company_dep.id in " + makeInArg(query.getDepartmentsIds(), false));
            }
        });
    }
}
