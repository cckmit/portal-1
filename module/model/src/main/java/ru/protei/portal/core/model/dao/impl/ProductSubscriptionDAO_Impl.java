package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.dao.ProductSubscriptionDAO;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.ent.DevUnitSubscription;

import java.util.List;

/**
 * Created by michael on 26.05.17.
 */
public class ProductSubscriptionDAO_Impl extends PortalBaseJdbcDAO<DevUnitSubscription> implements ProductSubscriptionDAO {
    @Override
    public List<Long> listIdsByDevUnitId(Long devUnitId) {
        StringBuilder sql = new StringBuilder("select id from ").append(getTableName()).append( " where dev_unit_id=?" );
        return jdbcTemplate.queryForList(sql.toString(), Long.class, devUnitId);
    }
}
