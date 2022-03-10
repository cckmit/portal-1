package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CardBatch;
import ru.protei.portal.core.model.ent.CardType;
import ru.protei.portal.core.model.query.CardBatchQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

public interface CardBatchService {

    @Privileged({ En_Privilege.CARD_BATCH_CREATE })
    @Auditable( En_AuditType.CARD_BATCH_CREATE )
    Result<CardBatch> createCardBatch(AuthToken token, CardBatch cardBatch);

    @Privileged({ En_Privilege.CARD_BATCH_EDIT })
    @Auditable( En_AuditType.CARD_BATCH_MODIFY )
    Result<CardBatch> updateCommonInfo(AuthToken token, CardBatch cardBatch);

    @Privileged({ En_Privilege.CARD_BATCH_EDIT })
    @Auditable( En_AuditType.CARD_BATCH_MODIFY )
    Result<CardBatch> updateMeta(AuthToken token, CardBatch meta);

    @Privileged({ En_Privilege.CARD_BATCH_EDIT })
    @Auditable( En_AuditType.CARD_BATCH_MODIFY )
    Result<CardBatch> updateCardBatch(AuthToken token, CardBatch cardBatch);

    Result<CardBatch> getLastCardBatch(AuthToken token, Long typeId);

    @Privileged({ En_Privilege.CARD_BATCH_VIEW })
    Result<SearchResult<CardBatch>> getCardBatches(AuthToken token, CardBatchQuery query);

    Result<CardBatch> getCardBatch(AuthToken token, Long id);

    Result<List<CardBatch>> getListCardBatchByType(AuthToken token, CardType cardType);

    @Privileged({ En_Privilege.CARD_BATCH_REMOVE })
    @Auditable( En_AuditType.CARD_BATCH_REMOVE )
    Result<CardBatch> removeCardBatch(AuthToken token, CardBatch value);
}
