package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.CaseTimeElapsedApiSum;
import ru.protei.portal.core.model.query.CaseTimeElapsedApiQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface CaseTimeElapsedApiSumDAO extends PortalBaseDAO<CaseTimeElapsedApiSum> {
    @SqlConditionBuilder
    SqlCondition createSqlCondition(CaseTimeElapsedApiQuery query );
}
