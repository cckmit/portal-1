package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CaseCommentNightWork;
import ru.protei.portal.core.model.query.CaseQuery;

import java.util.List;

public interface CaseCommentNightWorkDAO extends PortalBaseDAO<CaseCommentNightWork> {

    List<CaseCommentNightWork> getListByQuery( CaseQuery caseCommentQuery);
}
