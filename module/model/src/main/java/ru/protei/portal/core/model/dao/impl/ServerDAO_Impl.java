package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ServerDAO;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ServerDAO_Impl extends PortalBaseJdbcDAO<Server> implements ServerDAO {

    @Override
    public Map<Long, Long> countByPlatformIds(List<Long> platformIds) {
        String sql = "SELECT platform_id, COUNT(*) AS cnt FROM " + getTableName() + " " +
                "WHERE platform_id IN (" + StringUtils.join(platformIds, ",") + ") " +
                "GROUP BY platform_id";
        Map<Long, Long> result = new HashMap<>();
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            long id = rs.getLong("platform_id");
            long count = rs.getLong("cnt");
            result.put(id, count);
            return null;
        });
        return result;
    }

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
