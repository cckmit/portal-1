package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.DeliverySpecification;
import ru.protei.portal.core.model.query.DeliverySpecificationQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface DeliverySpecificationService {
    @Privileged({ En_Privilege.DELIVERY_SPECIFICATION_VIEW })
    Result<SearchResult<DeliverySpecification>> getDeliverySpecifications(AuthToken token, DeliverySpecificationQuery query);

    @Privileged({ En_Privilege.DELIVERY_SPECIFICATION_VIEW })
    Result<DeliverySpecification> getDeliverySpecification(AuthToken token, Long id);

    @Privileged({ En_Privilege.DELIVERY_SPECIFICATION_CREATE })
    Result<DeliverySpecification> createDeliverySpecification(AuthToken token, DeliverySpecification deliverySpecification);

    @Privileged({ En_Privilege.DELIVERY_SPECIFICATION_CREATE })
    Result<Boolean> createDeliverySpecifications(AuthToken token, List<DeliverySpecification> deliverySpecifications);
}
