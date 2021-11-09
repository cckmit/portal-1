package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.PcbOrder;
import ru.protei.portal.core.model.query.PcbOrderQuery;
import ru.protei.winter.core.utils.beans.SearchResult;

public interface PcbOrderService {

    @Privileged({ En_Privilege.PCB_ORDER_VIEW })
    Result<SearchResult<PcbOrder>> getPcbOrderList(AuthToken token, PcbOrderQuery query);

    @Privileged({ En_Privilege.PCB_ORDER_CREATE })
    @Auditable( En_AuditType.PCB_ORDER_CREATE )
    Result<PcbOrder> createPcbOrder(AuthToken token, PcbOrder pcbOrder);

    @Privileged({ En_Privilege.PCB_ORDER_REMOVE })
    @Auditable( En_AuditType.PCB_ORDER_REMOVE )
    Result<PcbOrder> removePcbOrder(AuthToken token, PcbOrder value);
}
