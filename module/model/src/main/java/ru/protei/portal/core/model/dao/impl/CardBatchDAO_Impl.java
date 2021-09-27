package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CardBatchDAO;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Query;

import static ru.protei.portal.core.model.ent.CardBatch.Columns.NUMBER;
import static ru.protei.portal.core.model.ent.CardBatch.Columns.TYPE_ID;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class CardBatchDAO_Impl extends PortalBaseJdbcDAO<CardBatch> implements CardBatchDAO {

    @Autowired
    CardBatchSqlBuilder cardSqlBuilder;

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CardBatchQuery query) {
        return cardSqlBuilder.createSqlCondition(query);
    }

    @Override
    public CardBatch getLastCardBatch(Long typeId) {

        Query query = query()
                .where(TYPE_ID)
                .equal( typeId )
                .and(NUMBER)
                .in(query()
                        .select("max(number)")
                        .from("card_batch")
                )
                .asQuery();

        return getByCondition(query.buildSql(), query.args());
    }
}
