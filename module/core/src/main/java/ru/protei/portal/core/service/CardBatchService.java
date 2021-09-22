package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CardBatch;

public interface CardBatchService {

    @Privileged({ En_Privilege.DELIVERY_CREATE })
    @Auditable( En_AuditType.DELIVERY_CREATE )
    Result<CardBatch> createCardBatch(AuthToken token, CardBatch cardBatch);

    @Privileged({ En_Privilege.DELIVERY_EDIT })
    @Auditable( En_AuditType.DELIVERY_MODIFY )
    Result<CardBatch> updateMeta(AuthToken token, CardBatch meta);

    Result<String> getLastNumber(AuthToken token, Long typeId);
}
