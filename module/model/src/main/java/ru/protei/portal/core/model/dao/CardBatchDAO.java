package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface CardBatchDAO extends PortalBaseDAO<CardBatch> {
    CardBatch getLastCardBatch(Long typeId);

    @SqlConditionBuilder
    SqlCondition createSqlCondition(CardBatchQuery query);
}