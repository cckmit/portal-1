package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.view.CaseCommentShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

/**
 * Created by michael on 19.05.16.
 */
public interface CaseCommentShortViewDAO extends PortalBaseDAO<CaseCommentShortView> {

    SearchResult<CaseCommentShortView> getSearchResult(CaseCommentQuery query);

    @SqlConditionBuilder
    SqlCondition caseCommentQueryCondition(CaseCommentQuery query);
}
