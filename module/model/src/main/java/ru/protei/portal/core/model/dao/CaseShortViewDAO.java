package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.List;

/**
 * Created by michael on 19.05.16.
 */
public interface CaseShortViewDAO extends PortalBaseDAO<CaseShortView> {
    List<CaseShortView> getCases( CaseQuery query );

    Long count(CaseQuery query);

    @SqlConditionBuilder
    SqlCondition caseQueryCondition( CaseQuery query );
}
