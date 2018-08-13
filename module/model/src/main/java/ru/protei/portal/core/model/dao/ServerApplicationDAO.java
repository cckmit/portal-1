package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.ServerApplication;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public interface ServerApplicationDAO extends PortalBaseDAO<ServerApplication> {

    @SqlConditionBuilder
    SqlCondition createSqlCondition(ServerQuery query);
}