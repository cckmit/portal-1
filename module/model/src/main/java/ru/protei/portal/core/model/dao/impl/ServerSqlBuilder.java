package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.jdbc.JdbcHelper;

public class ServerSqlBuilder {

    public SqlCondition createSqlCondition(ServerQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getServerId() != null) {
                condition.append(" and server.id = ?");
                args.add(query.getServerId());
            }

            if (query.getPlatformIds() != null && !query.getPlatformIds().isEmpty()) {
                condition.append(" and server.platform_id in ")
                        .append(JdbcHelper.makeSqlStringCollection(query.getPlatformIds(), args, null));
            }

            if (query.getSearchString() != null && !query.getSearchString().isEmpty()) {
                condition.append(" and server.name like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }

            if (query.getIp() != null && !query.getIp().isEmpty()) {
                condition.append(" and server.ip like ?");
                args.add(HelperFunc.makeLikeArg(query.getIp(), true));
            }

            if (query.getParams() != null && !query.getParams().isEmpty()) {
                condition.append(" and server.parameters like ?");
                args.add(HelperFunc.makeLikeArg(query.getParams(), true));
            }

            if (query.getComment() != null && !query.getComment().isEmpty()) {
                condition.append(" and server.comment like ?");
                args.add(HelperFunc.makeLikeArg(query.getComment(), true));
            }
        });
    }
}
