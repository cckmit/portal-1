package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.CaseTagDAO;
import ru.protei.portal.core.model.dao.HistoryDAO;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseTag;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.core.model.query.HistoryQuery;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class HistoryServiceImpl implements HistoryService {

    @Autowired
    HistoryDAO historyDAO;

    @Autowired
    CaseTagDAO caseTagDAO;

    @Override
    @Transactional
    public Result<Long> createHistory(AuthToken token, Long caseObjectId, En_HistoryAction action,
                                      En_HistoryType type, Long oldId, String oldValue, Long newId, String newValue) {

        if (token == null || caseObjectId == null || type == null || action == null || (oldId == null && newId == null) || (oldValue == null && newValue == null)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        History history = new History();
        history.setInitiatorId(token.getPersonId());
        history.setDate(new Date());
        history.setCaseObjectId(caseObjectId);
        history.setAction(action);
        history.setType(type);
        history.setOldId(oldId);
        history.setOldValue(oldValue);
        history.setNewId(newId);
        history.setNewValue(newValue);

        Long historyId = historyDAO.persist(history);

        if (historyId == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        return ok(historyId);
    }

    @Override
    public Result<List<History>> listHistories(AuthToken token, HistoryQuery query) {

        List<History> list = historyDAO.listByQuery(query);
        if (list == null) {
            return error(En_ResultStatus.GET_DATA_ERROR);
        }

        return ok(list);
    }

    @Override
    public Result<List<History>> getHistoryListByCaseId(AuthToken token, Long caseId) {
        if (caseId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Result<List<History>> historyListResult = listHistories(token, new HistoryQuery(caseId));

        if (historyListResult.isError()) {
            return error(historyListResult.getStatus());
        }

        return ok(fillHistoriesWithColors(historyListResult.getData()));
    }

    private List<History> fillHistoriesWithColors(List<History> histories) {
        Map<En_HistoryType, List<History>> typeToHistories = histories
                .stream()
                .collect(Collectors.toMap(History::getType, Arrays::asList, CollectionUtils::mergeLists));

        fillTagHistoriesWithColors(typeToHistories.get(En_HistoryType.TAG));

        return histories;
    }

    private void fillTagHistoriesWithColors(List<History> tagHistories) {
        List<Long> preparedTagIds = tagHistories
                .stream()
                .flatMap(history -> Stream.of(history.getOldId(), history.getNewId()))
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        CaseTagQuery caseTagQuery = new CaseTagQuery();
        caseTagQuery.setIds(preparedTagIds);

        Map<Long, String> caseTagIdToColor =
                caseTagDAO.getListByQuery(caseTagQuery)
                        .stream()
                        .collect(Collectors.toMap(CaseTag::getId, CaseTag::getColor));

        for (History nextHistory : tagHistories) {
            if (nextHistory.getOldId() != null) {
                nextHistory.setOldColor(caseTagIdToColor.get(nextHistory.getOldId()));
            }

            if (nextHistory.getNewId() != null) {
                nextHistory.setNewColor(caseTagIdToColor.get(nextHistory.getNewId()));
            }
        }
    }
}
