package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.util.sqlcondition.Query;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;

import java.util.List;
import java.util.Set;

/**
 * Created by michael on 26.05.17.
 */
public class CompanySubscriptionDAO_Impl extends PortalBaseJdbcDAO<CompanySubscription> implements CompanySubscriptionDAO {
    @Override
    public List<CompanySubscription> listByCompanyId( Long companyId) {
        return getListByCondition("company_id=?", companyId);
    }

    @Override
    public List<CompanySubscription> listByCompanyIds( Set<Long> companyIds ) {
        return getListByCondition( "company_id in " + HelperFunc.makeInArg( companyIds ) );
    }

    @Override
    public List<Long> listIdsByCompanyId(Long companyId) {
        StringBuilder sql = new StringBuilder("select id from ").append(getTableName()).append( " where company_id=?" );
        return jdbcTemplate.queryForList(sql.toString(), Long.class, companyId);
    }
}
