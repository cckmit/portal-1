package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.ent.CompanySubscription;

import java.util.List;

/**
 * Created by michael on 26.05.17.
 */
public class CompanySubscriptionDAO_Impl extends PortalBaseJdbcDAO<CompanySubscription> implements CompanySubscriptionDAO {
    @Override
    public List<CompanySubscription> listByCompanyId( Long companyId) {
        return getListByCondition("company_id=?", companyId);
    }

    @Override
    public List<Long> listIdsByCompanyId(Long companyId) {
        StringBuilder sql = new StringBuilder("select id from ").append(getTableName()).append( " where company_id=?" );
        return jdbcTemplate.queryForList(sql.toString(), Long.class, companyId);
    }
}
