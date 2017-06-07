package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.view.PersonShortView;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления учетными записями
 */
public class AccountServiceImpl implements AccountService {
    private static Logger log = LoggerFactory.getLogger( AccountServiceImpl.class );

    @Autowired
    UserLoginDAO userLoginDAO;

    @Override
    public CoreResponse< List< UserLogin > > accountList( AccountQuery query) {
        List< UserLogin > list = userLoginDAO.getAccounts(query);

        if (list == null)
            new CoreResponse< List< UserLogin > >().error( En_ResultStatus.GET_DATA_ERROR );

        return new CoreResponse< List< UserLogin > >().success( list );
    }

    @Override
    public CoreResponse< Long > count( AccountQuery query ) {
        Long count = userLoginDAO.count( query );

        if ( count == null )
            return new CoreResponse< Long >().error( En_ResultStatus.GET_DATA_ERROR );

        return new CoreResponse< Long >().success( count );
    }

    @Override
    public CoreResponse< UserLogin > getAccount( long id ) {
        UserLogin userLogin = userLoginDAO.get( id );

        return userLogin != null ? new CoreResponse< UserLogin >().success( userLogin )
                : new CoreResponse< UserLogin >().error( En_ResultStatus.NOT_FOUND );
    }

    @Override
    public CoreResponse< UserLogin > saveAccount( UserLogin userLogin ) {

        if ( !isValidLogin( userLogin ) )
            return new CoreResponse< UserLogin >().error( En_ResultStatus.VALIDATION_ERROR );

        if ( !isUniqueLogin( userLogin.getUlogin(), userLogin.getId() ) ) {
            return new CoreResponse< UserLogin >().error( En_ResultStatus.ALREADY_EXIST );
        }

        userLogin.setUlogin( userLogin.getUlogin().trim() );

        UserLogin account = userLogin.getId() == null ? null : getAccount( userLogin.getId() ).getData();

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

    private boolean isValidLogin( UserLogin userLogin ) {
        return HelperFunc.isNotEmpty( userLogin.getUlogin() )
                && userLogin.getPersonId() != null;
    }

    private boolean isUniqueLogin( String login, Long excludeId ) {
        UserLogin userLogin = userLoginDAO.checkExistsByLogin( login );

        return userLogin == null || userLogin.getId().equals( excludeId );
    }

}
