package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface ServerDAO extends PortalBaseDAO<Server> {

    @SqlConditionBuilder
    SqlCondition createSqlCondition(ServerQuery query);
}
