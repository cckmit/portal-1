package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CommonManagerDAO;
import ru.protei.portal.core.model.ent.CommonManager;
import ru.protei.portal.core.model.util.sqlcondition.Condition;

import java.util.List;

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

    @Override
    public void removeByCompany(Long companyId) {
        Condition condition = condition()
                .and("company_id").equal(companyId);
        removeByCondition(condition.getSqlCondition(), condition.getSqlParameters());
    }

    @Override
    public List<Long> getIdsByCompany(Long companyId) {
        StringBuilder sql = new StringBuilder("select id from ").append(getTableName()).append( " where company_id=?" );
        return jdbcTemplate.queryForList(sql.toString(), Long.class, companyId);
    }
}
