package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface CardBatchService {

    @Privileged({ En_Privilege.DELIVERY_CREATE })
    @Auditable( En_AuditType.DELIVERY_CREATE )
    Result<CardBatch> createCardBatch(AuthToken token, CardBatch cardBatch);

    @Privileged({ En_Privilege.DELIVERY_EDIT })
    @Auditable( En_AuditType.DELIVERY_MODIFY )
    Result<CardBatch> updateMeta(AuthToken token, CardBatch meta);

    @Privileged({ En_Privilege.DELIVERY_EDIT })
    @Auditable( En_AuditType.DELIVERY_MODIFY )
    Result<CardBatch> updateCardBatch(AuthToken token, CardBatch cardBatch);

    Result<CardBatch> getLastCardBatch(AuthToken token, Long typeId);

    @Privileged({ En_Privilege.DELIVERY_VIEW })
    Result<SearchResult<CardBatch>> getCardBatches(AuthToken token, CardBatchQuery query);

    Result<CardBatch> getCardBatch(AuthToken token, Long id);
}
