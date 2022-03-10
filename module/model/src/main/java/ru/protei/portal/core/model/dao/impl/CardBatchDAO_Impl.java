package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CardBatchDAO;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.Pair;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;

import static ru.protei.portal.core.model.ent.CardBatch.Columns.NUMBER;
import static ru.protei.portal.core.model.ent.CardBatch.Columns.TYPE_ID;
import static ru.protei.portal.core.model.helper.CollectionUtils.listOf;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class CardBatchDAO_Impl extends PortalBaseJdbcDAO<CardBatch> implements CardBatchDAO {

    @Autowired
    CardBatchSqlBuilder cardSqlBuilder;

    @Override
    public SearchResult<CardBatch> getSearchResultByQuery(CardBatchQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CardBatchQuery query) {
        return cardSqlBuilder.createSqlCondition(query);
    }

    private JdbcQueryParameters buildJdbcQueryParameters(CardBatchQuery query) {

        SqlCondition where = createSqlCondition(query);

        JdbcQueryParameters parameters = new JdbcQueryParameters();
        if (where.isConditionDefined())
            parameters.withCondition(where.condition, where.args);

        parameters.withOffset(query.getOffset());
        parameters.withLimit(query.getLimit());
        parameters.withSort(createSort(query));

        return parameters;
    }

    @Override
    public CardBatch getLastCardBatch(Long typeId) {

        Query query = query()
                .where(TYPE_ID)
                .equal(typeId)
                .and(NUMBER)
                .in(query()
                        .select("max(number)")
                        .from("card_batch")
                        .whereExpression(TYPE_ID + "=" + typeId)
                )
                .asQuery();

        return getByCondition(query.buildSql(), query.args());
    }

    public static JdbcSort createSort (CardBatchQuery query) {
        if (query.getSortField() == null) return null;

        return new JdbcSort(listOf(
                new Pair<>(query.getSortField().getFieldName(), TypeConverters.toWinter(query.getSortDir())),
                En_SortField.card_batch_type.equals(query.getSortField()) ?
                new Pair<>(En_SortField.card_batch_number.getFieldName(), JdbcSort.Direction.DESC) :
                        new Pair<>(En_SortField.card_batch_type.getFieldName(), JdbcSort.Direction.ASC)));
    }
}
