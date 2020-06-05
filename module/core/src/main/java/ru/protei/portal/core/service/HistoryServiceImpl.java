package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.HistoryDAO;
import ru.protei.portal.core.model.dict.En_HistoryValueType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.query.HistoryQuery;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class HistoryServiceImpl implements HistoryService{

    @Autowired
    HistoryDAO historyDAO;

    @Override
    public Result<Long> createHistory(AuthToken token, Long caseObjectId, En_HistoryValueType valueType, String oldValue, String newValue) {
        if (caseObjectId == null || valueType == null || (oldValue == null && newValue == null)){
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        History history = new History();
        history.setCaseObjectId(caseObjectId);
        history.setValueType(valueType);
        history.setOldValue(oldValue);
        history.setNewValue(newValue);
        history.setInitiatorId(token.getPersonId());
        history.setDate(new Date());

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
}
