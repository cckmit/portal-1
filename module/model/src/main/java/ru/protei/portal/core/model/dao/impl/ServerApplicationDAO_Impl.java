package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ServerApplicationDAO;
import ru.protei.portal.core.model.ent.ServerApplication;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.query.SqlCondition;

public class ServerApplicationDAO_Impl extends PortalBaseJdbcDAO<ServerApplication> implements ServerApplicationDAO {

    @Autowired
    ServerSqlBuilder serverSqlBuilder;

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(ServerQuery query) {
        return serverSqlBuilder.createSqlCondition(query);
    }
}
