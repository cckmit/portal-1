package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.HistoryDAO;
import ru.protei.portal.core.model.dict.En_HistoryAction;
import ru.protei.portal.core.model.dict.En_HistoryType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.query.HistoryQuery;
import ru.protei.portal.core.model.view.EntityOption;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class HistoryServiceImpl implements HistoryService {

    @Autowired
    HistoryDAO historyDAO;

    @Override
    public Result<Long> createHistory(AuthToken token, Long caseObjectId, En_HistoryAction action,
                                      En_HistoryType type, Long oldId, String oldName, Long newId, String newName) {

        if (caseObjectId == null || type == null || action == null || (oldId == null && newId == null) || (oldName == null && newName == null)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        History history = new History();
        history.setInitiatorId(token.getPersonId());
        history.setDate(new Date());
        history.setCaseObjectId(caseObjectId);
        history.setAction(action);
        history.setType(type);
        history.setOldId(oldId);
        history.setOldName(oldName);
        history.setNewId(newId);
        history.setNewName(newName);

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

        return listHistories(token, new HistoryQuery(caseId));
    }
}
