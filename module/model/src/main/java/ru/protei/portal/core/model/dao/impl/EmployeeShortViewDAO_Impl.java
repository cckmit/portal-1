package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.EmployeeShortViewDAO;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcHelper;
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
        SearchResult<EmployeeShortView> searchResult = new SearchResult();
        if (parameters.getOffset() <= 0 && parameters.getLimit() > 0) {
            searchResult.setTotalCount(count(query));
        }
        searchResult.setResults(getList(parameters));
        return searchResult;
    }

    private int count(EmployeeQuery query) {
        StringBuilder sql = new StringBuilder("select count(*) from ( select distinct ").append(getSelectSQL());
        SqlCondition whereCondition = createEmployeeSqlCondition(query);
        sql.append(" where ").append(whereCondition.condition).append(" ) empl");
        return jdbcTemplate.queryForObject(sql.toString(), Long.class, whereCondition.args.toArray()).intValue();
    }

    private String getSelectSQL() {
        return this.getObjectMapper().getSelectSQL(JdbcHelper.getDatabaseType(this.getJdbcTemplate()));
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
