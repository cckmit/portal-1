package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.UserLoginShortViewDAO;
import ru.protei.portal.core.model.ent.UserLoginShortView;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcQueryParameters;

public class UserLoginShortViewDAO_Impl extends PortalBaseJdbcDAO<UserLoginShortView> implements UserLoginShortViewDAO {
    @Override
    public SearchResult<UserLoginShortView> getSearchResult(UserLoginShortViewQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    private JdbcQueryParameters buildJdbcQueryParameters(UserLoginShortViewQuery query) {
        SqlCondition where = createSqlCondition( query );

        JdbcQueryParameters parameters = new JdbcQueryParameters();

        if (where.isConditionDefined()) {
            parameters.withCondition(where.condition, where.args);
        }

        parameters.withJoins(" LEFT JOIN person ON user_login.personId = person.id");
        parameters.withOffset(query.getOffset());
        parameters.withLimit(query.getLimit());

        return parameters;
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(UserLoginShortViewQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (StringUtils.isNotEmpty(query.getSearchString())) {
                condition.append(" and (user_login.ulogin like ? or person.displayname like ?)");

                String likeArg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                args.add(likeArg);
                args.add(likeArg);
            }

            if (query.getAdminState() != null) {
                condition
                        .append(" and user_login.astate = ")
                        .append(query.getAdminState().getId());
            }

            if (CollectionUtils.isNotEmpty(query.getLoginSet())) {
                condition
                        .append(" and user_login.ulogin IN ")
                        .append(HelperFunc.makeInArg(query.getLoginSet(), true));
            }
        });
    }
}
