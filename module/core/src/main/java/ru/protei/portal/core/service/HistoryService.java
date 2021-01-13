package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.query.HistoryQuery;

import java.util.List;

public interface HistoryService {

    Result<List<History>> listHistories(AuthToken token, HistoryQuery query);

    Result<List<History>> getHistoryListByCaseId(AuthToken token, Long caseId);

    Result<Long> createHistory(Long initiatorId, Long caseObjectId, En_HistoryAction action,
                               En_HistoryType type, Long oldId, String oldValue, Long newId, String newValue);

    Result<Long> createHistory(AuthToken token, Long caseObjectId, En_HistoryAction action,
                               En_HistoryType type, Long oldId, String oldValue, Long newId, String newValue);
}
