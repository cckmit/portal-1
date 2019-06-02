package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.EmployeeShortViewDAO;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class EmployeeShortViewDAO_Impl extends PortalBaseJdbcDAO<EmployeeShortView> implements EmployeeShortViewDAO {

    @Autowired
    EmployeeSqlBuilder employeeSqlBuilder;

    @Override
    public List<EmployeeShortView> getEmployees(EmployeeQuery query) {
        return employeeListByQuery(query);
    }

    @Override
    @SqlConditionBuilder
    public SqlCondition createEmployeeSqlCondition(EmployeeQuery query) {
        return employeeSqlBuilder.createSqlCondition(query);
    }

    private List<EmployeeShortView> employeeListByQuery(EmployeeQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getList(parameters);
    }

    @Override
    public SearchResult<EmployeeShortView> getSearchResult(EmployeeQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    private JdbcQueryParameters buildJdbcQueryParameters(EmployeeQuery query) {

        SqlCondition where = createEmployeeSqlCondition(query);
        JdbcQueryParameters parameters = new JdbcQueryParameters();

        parameters.withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query))
                .withOffset(query.getOffset())
                .withLimit(query.getLimit());

        return parameters;
    }
}
