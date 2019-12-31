package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Created by michael on 19.05.16.
 */
public interface CaseShortViewDAO extends PortalBaseDAO<CaseShortView> {

    SearchResult<CaseShortView> getSearchResult(CaseQuery query);

    List<CaseShortView> partialGetCases(CaseQuery query, String... columns);

    CaseShortView getCase(Long caseNo);

    List<CaseShortView> getListByCompanyId(Long companyId);

    @SqlConditionBuilder
    SqlCondition caseQueryCondition( CaseQuery query );
}
