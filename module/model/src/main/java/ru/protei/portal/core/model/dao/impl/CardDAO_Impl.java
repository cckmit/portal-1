package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CardDAO;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcQueryParameters;

public class CardDAO_Impl extends PortalBaseJdbcDAO<Card> implements CardDAO {

    @Autowired
    CardSqlBuilder cardSqlBuilder;

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CardQuery query) {
        return cardSqlBuilder.createSqlCondition(query);
    }

    @Override
    public SearchResult<Card> getSearchResult(CardQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    private JdbcQueryParameters buildJdbcQueryParameters(CardQuery query) {

        JdbcQueryParameters parameters = new JdbcQueryParameters();

        SqlCondition where = createSqlCondition(query);
        if (where.isConditionDefined()) {
            parameters.withCondition(where.condition, where.args);
        }

        parameters.withOffset(query.getOffset());
        parameters.withLimit(query.getLimit());
        parameters.withSort(TypeConverters.createSort( query ));

        return parameters;
    }
}
