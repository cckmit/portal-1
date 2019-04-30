package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CompanySubscription;

import java.util.List;
import java.util.Set;

/**
 * Created by michael on 26.05.17.
 */
public interface CompanySubscriptionDAO extends PortalBaseDAO<CompanySubscription> {

    List<CompanySubscription> listByCompanyId (Long companyId);

    List<CompanySubscription> listByCompanyIds( Set<Long> companyIds );

    List<Long> listIdsByCompanyId( Long companyId);
}
