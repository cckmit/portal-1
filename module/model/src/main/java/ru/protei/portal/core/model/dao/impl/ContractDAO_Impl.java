package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ContractDAO;
import ru.protei.portal.core.model.ent.Contract;
import ru.protei.portal.core.model.query.ContractQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcBaseDAO;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class ContractDAO_Impl extends JdbcBaseDAO<Long, Contract> implements ContractDAO {

    @Override
    public List<Contract> getListByQuery(ContractQuery query) {
        SqlCondition where = createSqlCondition(query);
        JdbcQueryParameters queryParameters = new JdbcQueryParameters();

        queryParameters.withCondition(where.condition, where.args)
                .withDistinct(true)
                .withSort(TypeConverters.createSort(query, "CO"))
                .withOffset(query.getOffset());
        if (query.limit > 0) {
            queryParameters = queryParameters.withLimit(query.getLimit());
        }
        return getList(queryParameters);
    }

    @Override
    public int countByQuery(ContractQuery query) {
        SqlCondition where = createSqlCondition(query);
        return getObjectsCount(where.condition, where.args);
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(ContractQuery query) {
        return new SqlCondition().build(((condition, args) -> {
            condition.append("1=1");
        }));
    }
}