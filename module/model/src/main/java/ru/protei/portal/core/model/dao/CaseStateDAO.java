package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.query.CaseStateQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

public interface CaseStateDAO extends PortalBaseDAO<CaseState> {

    List<CaseState> getListByQuery(CaseStateQuery query);

    @SqlConditionBuilder
    SqlCondition createSqlCondition(CaseStateQuery query);
}
