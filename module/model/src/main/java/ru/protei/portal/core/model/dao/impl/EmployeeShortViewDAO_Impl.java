package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.EmployeeShortViewDAO;
import ru.protei.portal.core.model.query.EmployeeQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class EmployeeShortViewDAO_Impl extends PortalBaseJdbcDAO<EmployeeShortView> implements EmployeeShortViewDAO {

    @Autowired
    EmployeeSqlBuilder employeeSqlBuilder;

    private final static String WORKER_ENTRY_JOIN = "LEFT JOIN worker_entry WE ON WE.personId = person.id";

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
        SqlCondition where = createEmployeeSqlCondition(query);
        return getList(
                new JdbcQueryParameters().
                        withJoins(WORKER_ENTRY_JOIN).
                        withCondition(where.condition, where.args).
                        withDistinct(true).
                        withOffset(query.getOffset()).
                        withLimit(query.getLimit()).
                        withSort( TypeConverters.createSort(query))
        );
    }
}
