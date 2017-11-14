package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.ent.UserRole;
import ru.protei.portal.core.model.ent.UserSessionDescriptor;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления учетными записями
 */
public class AccountServiceImpl implements AccountService {
    private static Logger log = LoggerFactory.getLogger( AccountServiceImpl.class );

    @Autowired
    UserLoginDAO userLoginDAO;

    @Autowired
    UserRoleDAO userRoleDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    @Override
    public CoreResponse< List< UserLogin > > accountList(AuthToken token, AccountQuery query ) {
        List< UserLogin > list = userLoginDAO.getAccounts( query );

        if (list == null)
            new CoreResponse< List< UserLogin > >().error( En_ResultStatus.GET_DATA_ERROR );
        jdbcManyRelationsHelper.fill( list, "roles" );

        return new CoreResponse< List< UserLogin > >().success( list );
    }

    @Override
    public CoreResponse< Long > count( AuthToken authToken, AccountQuery query ) {
        Long count = userLoginDAO.count( query );

        if ( count == null )
            return new CoreResponse< Long >().error( En_ResultStatus.GET_DATA_ERROR );

        return new CoreResponse< Long >().success( count );
    }

    @Override
    public CoreResponse< UserLogin > getAccount( AuthToken token, long id ) {
        UserLogin userLogin = userLoginDAO.get( id );

        if ( userLogin == null ) {
            return  new CoreResponse< UserLogin >().error( En_ResultStatus.NOT_FOUND );
        }

        jdbcManyRelationsHelper.fill( userLogin, "roles" );

        return new CoreResponse< UserLogin >().success( userLogin );
    }

    @Override
    @Transactional
    public CoreResponse< UserLogin > saveAccount( AuthToken token, UserLogin userLogin ) {
        if ( !isValidLogin( userLogin ) )
            return new CoreResponse< UserLogin >().error( En_ResultStatus.VALIDATION_ERROR );

        if ( !isUniqueLogin( userLogin.getUlogin(), userLogin.getId() ) ) {
            return new CoreResponse< UserLogin >().error( En_ResultStatus.ALREADY_EXIST );
        }

        userLogin.setUlogin( userLogin.getUlogin().trim() );

        UserLogin account = userLogin.getId() == null ? null : getAccount( token, userLogin.getId() ).getData();

        if ( account == null || ( account.getUpass() == null && userLogin.getUpass() != null ) ||
                ( account.getUpass() != null && userLogin.getUpass() != null && !account.getUpass().equalsIgnoreCase( userLogin.getUpass().trim() ) ) ) {
            userLogin.setUpass( DigestUtils.md5DigestAsHex( userLogin.getUpass().trim().getBytes() ) );
        }

        if( userLogin.getId() == null ) {
            userLogin.setCreated( new Date() );
            userLogin.setAuthTypeId( En_AuthType.LOCAL.getId() );
            userLogin.setAdminStateId( En_AdminState.UNLOCKED.getId() );
        }

        if ( userLoginDAO.saveOrUpdate( userLogin ) ) {

            jdbcManyRelationsHelper.persist( userLogin, "roles" );

            return new CoreResponse< UserLogin >().success( userLogin );
        }

        return new CoreResponse< UserLogin >().error( En_ResultStatus.INTERNAL_ERROR );
    }

    @Override
    public CoreResponse< Boolean > checkUniqueLogin( String login, Long excludeId ) {

        if( HelperFunc.isEmpty( login ) )
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        return new CoreResponse< Boolean >().success( isUniqueLogin( login, excludeId ) );
    }

    @Override
    public CoreResponse< Boolean > removeAccount( AuthToken token, Long accountId ) {

        if ( userLoginDAO.removeByKey( accountId ) ) {
            return new CoreResponse< Boolean >().success( true );
        }

        return new CoreResponse< Boolean >().error( En_ResultStatus.INTERNAL_ERROR );
    }

//    @Override
//    public CoreResponse< List< UserRole > > roleList() {
//        List< UserRole > list = userRoleDAO.getAll();
//
//        if (list == null)
//            new CoreResponse< List< UserRole > >().error( En_ResultStatus.GET_DATA_ERROR );
//
//        return new CoreResponse< List< UserRole > >().success( list );
//    }

    private boolean isValidLogin( UserLogin userLogin ) {
        return HelperFunc.isNotEmpty( userLogin.getUlogin() )
                && userLogin.getPersonId() != null;
    }

    private boolean isUniqueLogin( String login, Long excludeId ) {
        UserLogin userLogin = userLoginDAO.checkExistsByLogin( login );

        return userLogin == null || userLogin.getId().equals( excludeId );
    }

    private void applyFilterByScope( AuthToken token, AccountQuery query ) {
        UserSessionDescriptor descriptor = authService.findSession( token );

        if ( !descriptor.isGrantAccess() && descriptor.hasScopeFor( En_Scope.ROLE ) ) {
            query.setRoleIds(
                    Optional.ofNullable( descriptor.getLogin().getRoles())
                            .orElse( Collections.emptySet() )
                            .stream()
                            .map( UserRole::getId )
                            .collect( Collectors.toList()) );
        }
    }
}
