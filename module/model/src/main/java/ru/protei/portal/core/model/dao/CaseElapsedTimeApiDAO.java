package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.CaseElapsedTimeApi;
import ru.protei.portal.core.model.query.CaseElapsedTimeApiQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface CaseElapsedTimeApiDAO extends PortalBaseDAO<CaseElapsedTimeApi> {
    @SqlConditionBuilder
    SqlCondition createSqlCondition(CaseElapsedTimeApiQuery query );
}
