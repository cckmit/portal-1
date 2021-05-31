package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

/**
 * Сервис управления Поставками
 */
public interface DeliveryService {
    @Privileged({ En_Privilege.DELIVERY_VIEW })
    Result<SearchResult<Delivery>> getDeliveries(AuthToken token, DeliveryQuery query);

    @Privileged({ En_Privilege.DELIVERY_VIEW })
    Result<Delivery> getDelivery(AuthToken token, Long id);

    @Privileged({ En_Privilege.DELIVERY_CREATE })
    @Auditable( En_AuditType.DELIVERY_CREATE )
    Result<Delivery> createDelivery(AuthToken token, Delivery delivery);

    @Privileged({ En_Privilege.DELIVERY_EDIT })
    @Auditable( En_AuditType.DELIVERY_MODIFY )
    Result<Delivery> updateMeta(AuthToken token, Delivery meta);

    Result<String> getLastSerialNumber(AuthToken token, boolean isArmyProject);
}
