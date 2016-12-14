package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.List;
import java.util.Map;

/**
 * Created by michael on 19.05.16.
 */
public interface CaseShortViewDAO extends PortalBaseDAO<CaseShortView> {
    Map<Long,Long> getNumberToIdMap( En_CaseType caseType );

    List<CaseShortView> getCases( CaseQuery query );

    @SqlConditionBuilder
    SqlCondition caseQueryCondition( CaseQuery query );
}
