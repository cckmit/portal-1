package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

import static ru.protei.portal.core.model.dao.impl.EmployeeSqlBuilder.WORKER_ENTRY_JOIN;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class AccountingEmployeeSqlBuilder extends BaseSqlBuilder {

    public JdbcQueryParameters makeParameters(List<String> ids, List<String> departmentIds) {
        Condition condition = makeCondition(ids, departmentIds);
        return new JdbcQueryParameters().
                withJoins(WORKER_ENTRY_JOIN).
                withDistinct(true).
                withCondition(condition.getSqlCondition(), condition.getSqlParameters());
    }

    private Condition makeCondition( List<String> ids, List<String> departmentIds ) {
        Condition cnd = condition();
        if (isNotEmpty(ids)) {
            cnd.or("person.id").in(ids);
        }
        if (isNotEmpty(departmentIds)) {
            cnd.or("person.id").in(
                    query().select("personId").from("worker_entry")
                            .where("worker_entry.dep_id").in(departmentIds)
                            .asQuery());
        }
        return cnd;
    }
}
