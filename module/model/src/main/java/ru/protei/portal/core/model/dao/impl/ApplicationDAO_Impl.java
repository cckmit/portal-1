package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ApplicationDAO;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.jdbc.JdbcHelper;

public class ApplicationDAO_Impl extends PortalBaseJdbcDAO<Application> implements ApplicationDAO {

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
