package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CardType;

import java.util.List;

public interface CardBatchService {

    @Privileged({ En_Privilege.DELIVERY_CREATE })
    @Auditable( En_AuditType.DELIVERY_CREATE )
    Result<CardBatch> createCardBatch(AuthToken token, CardBatch cardBatch);

    @Privileged({ En_Privilege.DELIVERY_EDIT })
    @Auditable( En_AuditType.DELIVERY_MODIFY )
    Result<CardBatch> updateMeta(AuthToken token, CardBatch meta);

    Result<CardBatch> getLastCardBatch(AuthToken token, Long typeId);

    Result<List<CardBatch>> getListCardBatchByType(AuthToken token, CardType cardType);
}
