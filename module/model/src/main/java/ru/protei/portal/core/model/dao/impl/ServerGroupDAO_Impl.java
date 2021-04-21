package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ServerGroupDAO;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.core.model.query.ServerGroupQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;

public class ServerGroupDAO_Impl extends PortalBaseJdbcDAO<ServerGroup> implements ServerGroupDAO {
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(ServerGroupQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getPlatformId() != null) {
                condition.append(" and platform_id = ?");
                args.add(query.getPlatformId());
            }
        });
    }
}
