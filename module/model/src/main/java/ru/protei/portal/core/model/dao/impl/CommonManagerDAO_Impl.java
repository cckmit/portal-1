package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CommonManagerDAO;
import ru.protei.portal.core.model.ent.CommonManager;
import ru.protei.portal.core.model.util.sqlcondition.Condition;

import java.util.List;

import static ru.protei.portal.core.model.ent.CommonManager.Columns.*;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;


public class CommonManagerDAO_Impl extends PortalBaseJdbcDAO<CommonManager> implements CommonManagerDAO {
    @Override
    public void removeByProduct(Long productId) {
        Condition condition = condition().and(PRODUCT_ID).equal(productId)
                .and(getTableName() + "." + COMPANY_ID).isNull(true);
        removeByCondition(condition.getSqlCondition(), condition.getSqlParameters());
    }

    @Override
    public CommonManager getByProduct(Long productId) {
        Condition condition = condition().and(PRODUCT_ID).equal(productId)
                .and(getTableName() + "." + COMPANY_ID).isNull(true);
        return getByCondition(condition.getSqlCondition(), condition.getSqlParameters());
    }

    @Override
    public void removeByCompany(Long companyId) {
        Condition condition = condition()
                .and(getTableName() + "." + COMPANY_ID).equal(companyId);
        removeByCondition(condition.getSqlCondition(), condition.getSqlParameters());
    }

    @Override
    public List<Long> getIdsByCompany(Long companyId) {
        return jdbcTemplate.queryForList("select id from " + getTableName() + " where " + COMPANY_ID + "=?", Long.class, companyId);
    }
}
