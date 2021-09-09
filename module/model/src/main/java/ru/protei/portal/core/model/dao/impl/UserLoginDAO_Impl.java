package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by michael on 16.06.16.
 */
public class UserLoginDAO_Impl extends PortalBaseJdbcDAO<UserLogin> implements UserLoginDAO {

    private static final String COLUMN_ASTATE = "astate";
    private static final String COLUMN_PERSON_ID = "personId";

    @Override
    public UserLogin findByLogin(String login) {
        return getByCondition("ulogin=?", login);
    }

    @Override
    public boolean isUnique(String login) {
        return countByExpression("ulogin=?", login) == 0L;
    }

    @Override
    public List<UserLogin> findByPersonId(Long id) {
        return getListByCondition("personId=?", id);
    }

    @Override
    public List<UserLogin> findLDAPByPersonId(Long id) {
        return getListByCondition("personId=? and authType=?", id, En_AuthType.LDAP.getId());
    }

    @Override
    public UserLogin checkExistsByLogin(String login) {
        return getByCondition("ulogin=?", login );
    }

    @Override
    public SearchResult<UserLogin> getSearchResult(AccountQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    @Override
    public int removeByPersonId(Long id) {
        return removeByCondition("personId = ?", id);
    }

    private JdbcQueryParameters buildJdbcQueryParameters(AccountQuery query) {

        SqlCondition where = createSqlCondition( query );

        JdbcQueryParameters parameters = new JdbcQueryParameters();
        boolean distinct = false;

        if ( where.isConditionDefined() ) {
            parameters.withCondition(where.condition, where.args);
        }

        String join = "";
        if ( StringUtils.isNotEmpty( query.getSearchString())
                || query.getCompanyId() != null ) {
            distinct = true;
            join += " LEFT JOIN person ON user_login.personId = person.id";
        }
        if ( query.getRoleIds() != null ) {
            distinct = true;
            join += " LEFT JOIN login_role_item LR ON user_login.id = LR.login_id";
        }

        parameters.withDistinct( distinct );
        parameters.withJoins( join );
        parameters.withOffset( query.getOffset() );
        parameters.withLimit( query.getLimit() );
        parameters.withSort( TypeConverters.createSort(
                query,
                query.getSortField() == En_SortField.person_full_name ? "p" : null // p: alias for UserLogin.person
        ));

        return parameters;
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition( AccountQuery query ) {
        return new SqlCondition().build(( condition, args ) -> {
            condition.append( "1=1" ) ;

            if ( CollectionUtils.isNotEmpty( query.getTypes() ) ) {
                condition.append(" and user_login.authType in (" +
                        query.getTypes().stream().map( En_AuthType::getId ).collect( Collectors.toList() )
                                .stream().map( Object::toString ).collect( Collectors.joining("," ) ) + ")");
            }

            if ( StringUtils.isNotEmpty( query.getSearchString() ) ) {
                condition.append( " and (user_login.ulogin like ? or person.displayname like ?)" );

                String likeArg = HelperFunc.makeLikeArg( query.getSearchString(), true );
                args.add( likeArg );
                args.add( likeArg );
            }

            if ( CollectionUtils.isNotEmpty( query.getRoleIds() ) ) {
                condition.append(" and LR.role_id IN ( ");
                condition.append( query.getRoleIds().stream()
                        .map( String::valueOf )
                        .collect( Collectors.joining(",")));
                condition.append( " )");
            }

            if ( query.getCompanyId() != null ) {
                condition.append(" and person.company_id = " + query.getCompanyId());
            }
        });
    }

    @Override
    public void unlockAccounts(Long personId) {
        String sql = "UPDATE " + getTableName() + " SET " + COLUMN_ASTATE + " = " +
                En_AdminState.UNLOCKED.getId() + " WHERE " + COLUMN_PERSON_ID + " = " + personId;
        jdbcTemplate.update(sql);
    }
}
