package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.lang3.StringUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.collections.CollectionUtils;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by michael on 16.06.16.
 */
public class UserLoginDAO_Impl extends PortalBaseJdbcDAO<UserLogin> implements UserLoginDAO {

    @Override
    public UserLogin findByLogin(String login) {
        return getByCondition("ulogin=?", login);
    }

    @Override
    public UserLogin findByPersonId(Long id) {
        return getByCondition("personId=?", id);
    }

    @Override
    public UserLogin checkExistsByLogin(String login) {
        return getByCondition("ulogin=?", login );
    }

    @Override
    public List< UserLogin > getAccounts( AccountQuery query) {
        return listByQuery( query );
    }

    private List< UserLogin > listByQuery( AccountQuery query ) {
        SqlCondition where = createSqlCondition( query );

        JdbcQueryParameters parameters = new JdbcQueryParameters();

        if ( where.isConditionDefined() ) {
            parameters.withCondition(where.condition, where.args);
        }

        String join = "";
        if ( StringUtils.isNotEmpty( query.getSearchString() ) ) {
            join += "LEFT JOIN person ON user_login.personId = person.id";
        }
        if ( query.getRoleIds() != null ) {
            join += " LEFT JOIN login_role_item ON user_login.id = login_role_item.login_id";
        }

        parameters.withJoins( join );
        parameters.withOffset( query.getOffset() );
        parameters.withLimit( query.getLimit() );
        parameters.withSort( TypeConverters.createSort( query ) );
        return getList( parameters );
    }

    @Override
    public Long count( AccountQuery query ) {
        String join = "";
        boolean distinct = false;

        if ( StringUtils.isNotEmpty( query.getSearchString() ) ) {
            join += "LEFT JOIN person ON user_login.personId = person.id";
            distinct = true;
        }
        if ( query.getRoleIds() != null ) {
            join += " LEFT JOIN login_role_item LR ON user_login.id = LR.login_id";
        }

        SqlCondition where = createSqlCondition( query );

        return (long) getObjectsCount( where.condition, where.args, join, distinct );
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
        });
    }
}