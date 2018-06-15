package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

/**
 * Created by michael on 20.05.16.
 */
public interface CaseCommentDAO extends PortalBaseDAO<CaseComment> {

    List<CaseComment> getCaseComments( long caseId );

    List<CaseComment> getCaseComments( CaseCommentQuery query );

    @SqlConditionBuilder
    SqlCondition createSqlCondition( CaseCommentQuery query );
}
