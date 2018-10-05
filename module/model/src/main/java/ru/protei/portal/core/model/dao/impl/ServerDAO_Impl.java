package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ServerDAO;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public class ServerDAO_Impl extends PortalBaseJdbcDAO<Server> implements ServerDAO {

    @Override
    public Long count(ServerQuery query) {
        SqlCondition where = createSqlCondition(query);
        return (long) getObjectsCount(where.condition, where.args);
    }

    @Autowired
    ServerSqlBuilder serverSqlBuilder;

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(ServerQuery query) {
        return serverSqlBuilder.createSqlCondition(query);
    }
}
