package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.ent.Kit;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

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

    @Privileged({ En_Privilege.DELIVERY_REMOVE })
    @Auditable( En_AuditType.DELIVERY_REMOVE )
    Result<Long> removeDelivery(AuthToken token, Long projectId);

    @Privileged({ En_Privilege.DELIVERY_EDIT })
    @Auditable( En_AuditType.DELIVERY_MODIFY )
    Result<Delivery> updateMeta(AuthToken token, Delivery meta);

    Result<String> getLastSerialNumber(AuthToken token, boolean isMilitaryNumbering);
    Result<String> getLastSerialNumber(AuthToken token, Long deliveryId);

    @Privileged({ En_Privilege.DELIVERY_EDIT })
    @Auditable( En_AuditType.KIT_CREATE )
    Result<List<Kit>> addKits(AuthToken token, List<Kit> kits, Long deliveryId);


}
