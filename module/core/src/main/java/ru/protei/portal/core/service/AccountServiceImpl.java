package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.UserLogin;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AccountQuery;

import java.util.Date;
import java.util.List;

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

        if ( HelperFunc.isEmpty( userLogin.getUlogin() ) )
            return new CoreResponse< UserLogin >().error( En_ResultStatus.VALIDATION_ERROR );

        userLogin.setUlogin( userLogin.getUlogin().trim() );

        UserLogin account = userLogin.getId() == null ? null : getAccount( userLogin.getId() ).getData();

        if ( account == null || ( account.getUpass() == null && userLogin.getUpass() != null ) ||
                ( account.getUpass() != null && userLogin.getUpass() != null && !account.getUpass().equalsIgnoreCase( userLogin.getUpass().trim() ) ) ) {
            userLogin.setUpass( DigestUtils.md5DigestAsHex( userLogin.getUpass().trim().getBytes() ) );
        }

        if( userLogin.getId() == null ) {
            userLogin.setCreated(new Date());
            userLogin.setAuthTypeId( En_AuthType.LOCAL.getId() );
            //userLogin.setRoleId(En_UserRole.CRM_CLIENT.getId());
            userLogin.setAdminStateId( En_AdminState.UNLOCKED.getId() );
        }

        if ( userLoginDAO.saveOrUpdate( userLogin ) ) {
            return new CoreResponse< UserLogin >().success( userLogin );
        }

        return new CoreResponse< UserLogin >().error( En_ResultStatus.INTERNAL_ERROR );
    }
}
