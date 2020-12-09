package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.query.HistoryQuery;

import java.util.List;

public interface HistoryDAO extends PortalBaseDAO<History> {

    List<History> getListByQuery(HistoryQuery query);

    void removeByCaseId(Long caseId);
}
