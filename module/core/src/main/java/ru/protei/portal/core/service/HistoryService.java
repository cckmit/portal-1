package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_HistoryValueType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.query.HistoryQuery;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.List;

public interface HistoryService {

    Result<List<History>> listHistories(AuthToken token, HistoryQuery query);

    Result<List<History>> getHistoryListByCaseId(AuthToken token, Long caseId);

    Result<Long> createHistory(AuthToken token, Long caseObjectId, En_HistoryValueType valueType,
                               String oldValue, String newValue, EntityOption oldValueData, EntityOption newValueData);
}
