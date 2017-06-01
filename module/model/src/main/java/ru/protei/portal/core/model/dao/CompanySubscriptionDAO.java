package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CompanySubscription;

import java.util.List;

/**
 * Created by michael on 26.05.17.
 */
public interface CompanySubscriptionDAO extends PortalBaseDAO<CompanySubscription> {

    List<CompanySubscription> listByCompanyId (Long companyId);

    List<Long> listIdsByCompanyId( Long companyId);
}
