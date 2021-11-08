package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.PcbOrderDAO;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.Pair;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;


public class PcbOrderDAO_Impl extends PortalBaseJdbcDAO<PcbOrder> implements PcbOrderDAO {

    @Autowired
    PcbOrderSqlBuilder sqlBuilder;

    @Override
    public SearchResult<PcbOrder> getSearchResultByQuery(PcbOrderQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(PcbOrderQuery query) {
        return sqlBuilder.createSqlCondition(query);
    }

    private JdbcQueryParameters buildJdbcQueryParameters(PcbOrderQuery query) {

        SqlCondition where = createSqlCondition(query);

        JdbcQueryParameters parameters = new JdbcQueryParameters();
        if (where.isConditionDefined())
            parameters.withCondition(where.condition, where.args);

        parameters.withOffset(query.getOffset());
        parameters.withLimit(query.getLimit());
        parameters.withSort(createSort(query));

        return parameters;
    }

    private static JdbcSort createSort (PcbOrderQuery query) {
        if (query.getSortField() == null) return null;

        return new JdbcSort(listOf(
                new Pair<>(query.getSortField().getFieldName(), TypeConverters.toWinter(query.getSortDir()))
        ));
    }
}
