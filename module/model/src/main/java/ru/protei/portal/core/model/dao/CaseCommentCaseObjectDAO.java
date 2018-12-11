package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CaseCommentCaseObject;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseQuery;

import java.util.List;

public interface CaseCommentCaseObjectDAO extends PortalBaseDAO<CaseCommentCaseObject> {

    Long count(CaseQuery caseQuery, CaseCommentQuery caseCommentQuery);

    List<CaseCommentCaseObject> getListByQueries(CaseQuery caseQuery, CaseCommentQuery caseCommentQuery);
}
