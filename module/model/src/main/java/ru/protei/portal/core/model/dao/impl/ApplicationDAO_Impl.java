package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ApplicationDAO;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.jdbc.JdbcHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ApplicationDAO_Impl extends PortalBaseJdbcDAO<Application> implements ApplicationDAO {

    @Override
    public Map<Long, Long> countByServerIds(List<Long> serverIds) {
        String sql = "SELECT server_id, COUNT(*) AS cnt FROM " + getTableName() + " " +
                "WHERE server_id IN " + HelperFunc.makeInArg(serverIds, String::valueOf) + " " +
                "GROUP BY server_id";
        Map<Long, Long> result = new HashMap<>();
        jdbcTemplate.query(sql, (rs, rowNum) -> {
            long id = rs.getLong("server_id");
            long count = rs.getLong("cnt");
            result.put(id, count);
            return null;
        });
        return result;
    }

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(ApplicationQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (query.getApplicationId() != null) {
                condition.append(" and application.id = ?");
                args.add(query.getApplicationId());
            }

            if (query.getServerIds() != null && !query.getServerIds().isEmpty()) {
                condition.append(" and application.server_id in ")
                        .append(JdbcHelper.makeSqlStringCollection(query.getServerIds(), args, null));
            }

            if (query.getComponentIds() != null && !query.getComponentIds().isEmpty()) {
                condition.append(" and application.dev_unit_id in ")
                        .append(JdbcHelper.makeSqlStringCollection(query.getComponentIds(), args, null));
            }

            if (query.getSearchString() != null && !query.getSearchString().isEmpty()) {
                condition.append(" and application.name like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }

            if (query.getComment() != null && !query.getComment().isEmpty()) {
                condition.append(" and application.comment like ?");
                args.add(HelperFunc.makeLikeArg(query.getComment(), true));
            }
        });
    }
}
