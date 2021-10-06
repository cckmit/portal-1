package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CardDAO;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Query;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class CardDAO_Impl extends PortalBaseJdbcDAO<Card> implements CardDAO {

    @Autowired
    CardSqlBuilder cardSqlBuilder;

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CardQuery query) {
        return cardSqlBuilder.createSqlCondition(query);
    }

    public Long getLastNumber(Long typeId, Long batchId) {
        Query query = query()
                .select("max(RIGHT("+ Card.Columns.SERIAL_NUMBER + ", 3))")
                .from(getSelectTableName())
                .where(Card.Columns.TYPE_ID).equal(typeId)
                    .and(Card.Columns.CARD_BATCH_ID).equal(batchId)
                .asQuery();

        return jdbcTemplate.queryForObject(query.buildSql(), query.args(), Long.class);
    }
}
