package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CompanySubscription;
import ru.protei.portal.core.model.ent.DevUnitSubscription;

import java.util.List;

/**
 * Created by michael on 26.05.17.
 */
public interface ProductSubscriptionDAO extends PortalBaseDAO<DevUnitSubscription> {

    List<DevUnitSubscription> listByDevUnitId(Long devUnitId);

    List<Long> listIdsByDevUnitId(Long devUnitId);
}
