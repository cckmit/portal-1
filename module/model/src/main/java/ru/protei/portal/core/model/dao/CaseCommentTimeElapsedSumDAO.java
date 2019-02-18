package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CaseCommentTimeElapsedSum;
import ru.protei.portal.core.model.query.CommentTimeElapsedQuery;

import java.util.List;

public interface CaseCommentTimeElapsedSumDAO extends PortalBaseDAO<CaseCommentTimeElapsedSum> {

    List<CaseCommentTimeElapsedSum> getListByQuery( CommentTimeElapsedQuery caseCommentQuery);
}
