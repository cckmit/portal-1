package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;
import java.util.Map;

public interface ServerDAO extends PortalBaseDAO<Server> {

    Map<Long, Long> countByPlatformIds(List<Long> platformIds);

    Long count(ServerQuery query);

    @SqlConditionBuilder
    SqlCondition createSqlCondition(ServerQuery query);
}
