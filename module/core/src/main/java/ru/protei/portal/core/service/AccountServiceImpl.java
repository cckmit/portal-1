package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.DigestUtils;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.event.UserLoginUpdateEvent;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dao.UserRoleDAO;
import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.dict.En_AuthType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.AccountQuery;
import ru.protei.portal.core.model.struct.NotificationEntry;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

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
    PersonDAO personDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    @Autowired
    EventPublisherService publisherService;

    @Override
    public Result<SearchResult<UserLogin>> getAccounts( AuthToken token, AccountQuery query) {

        applyFilterByScope(token, query);

        SearchResult<UserLogin> sr = userLoginDAO.getSearchResult(query);

        jdbcManyRelationsHelper.fill(sr.getResults(), "roles");

        return ok( sr);
    }

    @Override
    public Result< UserLogin > getAccount( AuthToken token, long id ) {
        UserLogin userLogin = userLoginDAO.get( id );

        if ( userLogin == null ) {
            return  error( En_ResultStatus.NOT_FOUND );
        }

        jdbcManyRelationsHelper.fill( userLogin, "roles" );

        return ok( userLogin );
    }

    @Override
    public Result< UserLogin > getContactAccount( AuthToken authToken, long personId ) {
        UserLogin userLogin = userLoginDAO.findByPersonId( personId );

        if ( userLogin == null ) {
            return  error( En_ResultStatus.NOT_FOUND );
        }

        jdbcManyRelationsHelper.fill( userLogin, "roles" );

        return ok( userLogin );
    }

    @Override
    @Transactional
    public Result< UserLogin > saveAccount( AuthToken token, UserLogin userLogin, Boolean sendWelcomeEmail ) {
        if ( !isValidLogin( userLogin ) )
            return error( En_ResultStatus.VALIDATION_ERROR );

        if ( !isUniqueLogin( userLogin.getUlogin(), userLogin.getId() ) ) {
            return error( En_ResultStatus.ALREADY_EXIST );
        }

        if (userLogin.getRoles() == null || userLogin.getRoles().size() == 0) {
            log.warn("saveAccount(): Can't save account. Expected one or more Roles.");
            return error( En_ResultStatus.INCORRECT_PARAMS );
        }

        userLogin.setUlogin( userLogin.getUlogin().trim() );

        boolean isNewAccount = userLogin.getId() == null;
        UserLogin account = isNewAccount ? null : getAccount( token, userLogin.getId() ).getData();

        String passwordRaw = sendWelcomeEmail ? userLogin.getUpass() : null;

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

            if (sendWelcomeEmail) {

                Person person = personDAO.get(userLogin.getPersonId());
                userLogin.setPerson(person);

                PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(person.getContactInfo());
                String address = HelperFunc.nvlt(infoFacade.getEmail(), infoFacade.getEmail_own(), null);
                NotificationEntry notificationEntry = NotificationEntry.email(address, person.getLocale());
                UserLoginUpdateEvent userLoginUpdateEvent = new UserLoginUpdateEvent(userLogin.getUlogin(), passwordRaw, userLogin.getInfo(), isNewAccount, notificationEntry);

                publisherService.publishEvent(userLoginUpdateEvent);
            }

            return ok( userLogin );
        }

        return error( En_ResultStatus.INTERNAL_ERROR );
    }

    @Override
    public Result<UserLogin> saveContactAccount( AuthToken token, UserLogin userLogin, Boolean sendWelcomeEmail) {

        if (userLogin.getId() == null) {
            Set<UserRole> userRoles = new HashSet<>(userRoleDAO.getDefaultContactRoles());
            userLogin.setRoles(userRoles);
        }

        return saveAccount(token, userLogin, sendWelcomeEmail);
    }

    @Override
    public Result< Boolean > checkUniqueLogin( String login, Long excludeId ) {

        if( HelperFunc.isEmpty( login ) )
            return error( En_ResultStatus.INCORRECT_PARAMS);

        return ok( isUniqueLogin( login, excludeId ) );
    }

    @Override
    public Result< Boolean > removeAccount( AuthToken token, Long accountId ) {

        if ( userLoginDAO.removeByKey( accountId ) ) {
            return ok( true );
        }

        return error( En_ResultStatus.INTERNAL_ERROR );
    }

    @Override
    public Result<?> updateAccountPassword( AuthToken token, Long loginId, String currentPassword, String newPassword) {
        UserLogin userLogin = getAccount(token, loginId).getData();

        Long personIdFromSession = authService.findSession(token).getPerson().getId();

        if (userLogin.isLDAP_Auth() || !Objects.equals(personIdFromSession, userLogin.getPersonId())) {
            return error( En_ResultStatus.NOT_AVAILABLE);
        }

        String md5Password = DigestUtils.md5DigestAsHex(currentPassword.getBytes());
        if (!userLogin.getUpass().equalsIgnoreCase(md5Password)) {
            return error( En_ResultStatus.INVALID_CURRENT_PASSWORD);
        }
        userLogin.setUpass(DigestUtils.md5DigestAsHex(newPassword.getBytes()));
        userLogin.setLastPwdChange(new Date());

        return userLoginDAO.saveOrUpdate(userLogin) ? ok() : error( En_ResultStatus.INTERNAL_ERROR);
    }

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

        if ( !policyService.hasGrantAccessFor( descriptor.getLogin().getRoles(), En_Privilege.ACCOUNT_VIEW ) ) {
            query.setRoleIds(
                    Optional.ofNullable( descriptor.getLogin().getRoles())
                            .orElse( Collections.emptySet() )
                            .stream()
                            .map( UserRole::getId )
                            .collect( Collectors.toList()) );
        }
    }
}
