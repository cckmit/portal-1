package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface CardBatchDAO extends PortalBaseDAO<CardBatch> {
    CardBatch getLastCardBatch(Long typeId);

    SearchResult<CardBatch> getSearchResultByQuery(CardBatchQuery query);

    @SqlConditionBuilder
    SqlCondition createSqlCondition(CardBatchQuery query);
}