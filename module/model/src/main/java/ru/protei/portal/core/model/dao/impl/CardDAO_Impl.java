package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CardDAO;
import ru.protei.portal.core.model.ent.Card;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CardQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Query;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    @Override
    public Map<Long, Long> countByBatchIds(List<Long> batchIds) {
        String sql = "SELECT card_batch_id, COUNT(*) AS cnt FROM " + getTableName() + " " +
                "WHERE card_batch_id IN " + HelperFunc.makeInArg(batchIds, String::valueOf) + " " +
                "GROUP BY card_batch_id";

        Map<Long, Long> result = new HashMap<>();
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            long id = rs.getLong("card_batch_id");
            long count = rs.getLong("cnt");
            result.put(id, count);
            return null;
        });

        return result;
    }

    @Override
    public Boolean existByCardBatchId(Long cardBatchId) {
        return checkExistsByCondition(Card.Columns.CARD_BATCH_ID + "= ?", cardBatchId);
    }
}
