package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CompanySubscriptionDAO;
import ru.protei.portal.core.model.ent.CompanySubscription;

import java.util.List;

/**
 * Created by michael on 26.05.17.
 */
public class CompanySubscriptionDAO_Impl extends PortalBaseJdbcDAO<CompanySubscription> implements CompanySubscriptionDAO {
    @Override
    public List<CompanySubscription> listByCompanyId(Long companyId) {
        return getListByCondition("company_id=?", companyId);
    }
}
