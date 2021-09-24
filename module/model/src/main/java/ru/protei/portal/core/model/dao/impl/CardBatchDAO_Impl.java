package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CardBatchDAO;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.util.sqlcondition.Query;

import static ru.protei.portal.core.model.ent.CardBatch.Columns.NUMBER;
import static ru.protei.portal.core.model.ent.CardBatch.Columns.TYPE_ID;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class CardBatchDAO_Impl extends PortalBaseJdbcDAO<CardBatch> implements CardBatchDAO {

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
