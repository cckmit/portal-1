package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;
import java.util.Map;

public interface ApplicationDAO extends PortalBaseDAO<Application> {

    Map<Long, Long> countByServerIds(List<Long> serverIds);

    @SqlConditionBuilder
    SqlCondition createSqlCondition(ApplicationQuery query);
}
