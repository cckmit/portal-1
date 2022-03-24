package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CommonManagerDAO;
import ru.protei.portal.core.model.ent.CommonManager;
import ru.protei.portal.core.model.util.sqlcondition.Condition;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;


public class CommonManagerDAO_Impl extends PortalBaseJdbcDAO<CommonManager> implements CommonManagerDAO {
    @Override
    public void removeByProduct(Long productId) {
        Condition condition = condition().and("product_id").equal(productId)
                .and("company_id").isNull(true);
        removeByCondition(condition.getSqlCondition(), condition.getSqlParameters());
    }

    @Override
    public CommonManager getByProduct(Long productId) {
        Condition condition = condition().and("product_id").equal(productId)
                .and("company_id").isNull(true);
        return getByCondition(condition.getSqlCondition(), condition.getSqlParameters());
    }
}
