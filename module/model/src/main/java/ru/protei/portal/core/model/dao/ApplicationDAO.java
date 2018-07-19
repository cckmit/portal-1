package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface ApplicationDAO extends PortalBaseDAO<Application> {

    @SqlConditionBuilder
    SqlCondition createSqlCondition(ApplicationQuery query);
}
