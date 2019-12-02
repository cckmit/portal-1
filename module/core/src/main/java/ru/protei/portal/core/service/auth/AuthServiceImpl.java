package ru.protei.portal.core.service.auth;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dao.UserSessionDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

/**
 * Created by michael on 29.06.16.
 */
public class AuthServiceImpl implements AuthService {

    private static Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserLoginDAO userLoginDAO;
    @Autowired
    private UserSessionDAO sessionDAO;
    @Autowired
    private PersonDAO personDAO;
    @Autowired
    private LDAPAuthProvider ldapAuthProvider;
    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    private PortalConfig config;

    public AuthServiceImpl() {}

    private En_ResultStatus authentificate(UserLogin login, String ulogin, String pwd) {
        if (login == null) {
            log.debug("login [" + ulogin + "] not found, auth-failed");
            return En_ResultStatus.INVALID_LOGIN_OR_PWD;
        }

        if (login.isLDAP_Auth()) {
            // check by LDAP
            //
            // ATTENTION!
            //
            // I will cut off hands to anyone who will comment out next code again!
            // You have to add your own personal account with authentication by password
            //
            //
            //
            return ldapAuthProvider.checkAuth(ulogin, pwd);
        } else {
            // check MD5
            String md5Hash = DigestUtils.md5DigestAsHex(pwd.getBytes());
            if (login.getUpass() == null || !login.getUpass().equalsIgnoreCase(md5Hash)) {
                log.debug("login " + ulogin + " - invalid password, auth-failed");
                return En_ResultStatus.INVALID_LOGIN_OR_PWD;
            } else {
                return En_ResultStatus.OK;
            }
        }
    }

    @Override
    public Result<AuthToken> login( String appSessionId, String login, String pwd, String ip, String userAgent) {

        log.info("login(): {} {} {} {} {}", appSessionId, login, makePasswordString(pwd), ip, userAgent);
        if (StringUtils.isEmpty(login) || StringUtils.isEmpty(pwd)) {
            log.debug("null login or pwd, auth-failed");
            return error( En_ResultStatus.INVALID_LOGIN_OR_PWD);
        }

        UserLogin userLogin = userLoginDAO.findByLogin(login);
        String loginSuffix = config.data().getLoginSuffix();
        boolean loginHasSuffix = login.contains("@");
        En_ResultStatus loginStatus;
        if (isEmpty(loginSuffix) || loginHasSuffix) {
            loginStatus = authentificate(userLogin, login, pwd);
        } else {
            // PORTAL-490 feature
            if (userLogin == null || !userLogin.isLDAP_Auth()) {
                log.debug("login [{}] not found, forced to use suffix [{}]", login, loginSuffix);
                login += loginSuffix;
                userLogin = userLoginDAO.findByLogin(login);
                loginStatus = authentificate(userLogin, login, pwd);
            } else {
                loginStatus = authentificate(userLogin, login, pwd);
                if (loginStatus != En_ResultStatus.OK) {
                    log.debug("ldap login [{}] failed, forced to use suffix [{}]", login, loginSuffix);
                    login += loginSuffix;
                    userLogin = userLoginDAO.findByLogin(login);
                    loginStatus = authentificate(userLogin, login, pwd);
                }
            }
        }
        if (loginStatus != En_ResultStatus.OK) {
            return error(loginStatus);
        }

        UserSession userSession = getUserSession(appSessionId);
        if (userSession != null) {
            if (!Objects.equals(userSession.getLoginId(), userLogin.getId())) {
                log.warn("Security exception, client {}({}) from host {} is trying to accessType session {} created for {}@{}",
                        userLogin.getUlogin(), userLogin.getId(), ip, userSession.getSessionId(), userSession.getLoginId(), userSession.getClientIp());
                return error(En_ResultStatus.INVALID_SESSION_ID);
            }
            if (!Objects.equals(userSession.getClientIp(), ip)) {
                log.warn("Security exception, host {} is trying to accessType session {} created for {}",
                        ip, userSession.getSessionId(), userSession.getClientIp());
                return error(En_ResultStatus.INVALID_SESSION_ID);
            }
        }
        if (userSession == null) {
            userSession = new UserSession();
            userSession.setSessionId(appSessionId);
            userSession.setCreated(new Date());
        }

        Person person = personDAO.get(userLogin.getPersonId());
        if (person.isFired() || person.isDeleted()) {
            log.debug("login [{}] - person {}, access denied", login, person.isFired() ? "fired" : "deleted");
            return error( En_ResultStatus.PERMISSION_DENIED);
        }

        userSession.setExpired(DateUtils.addSeconds(new Date(), AuthService.DEF_APP_SESSION_LIVE_TIME));
        userSession.setClientIp(ip);
        userSession.setLoginId(userLogin.getId());
        userSession.setPersonId(userLogin.getPersonId());
        userSession.setCompanyId(userLogin.getCompanyId());

        sessionDAO.removeByCondition("session_id = ?", userSession.getSessionId());
        sessionDAO.persist(userSession);

        AuthToken token = makeAuthToken(userSession);

        log.info("Auth success for {} / {} / {}",
                login,
                token.getRoles() == null ? "null" : token.getRoles().stream().map(UserRole::getCode).collect(Collectors.joining(",")),
                person.toDebugString()
        );

        return ok(token);
    }

    @Override
    public boolean logout(String appSessionId, String ip, String userAgent) {

        log.info("logout(): {} {} {}", appSessionId, ip, userAgent);

        UserSession userSession = getUserSession(appSessionId);
        if (userSession == null) {
            return false;
        }

        if (!Objects.equals(userSession.getClientIp(), ip)) {
            log.warn("Security exception, host {} is trying to close session {} created for {}",
                    ip, userSession.getSessionId(), userSession.getClientIp());
            return false;
        }

        closeUserSession(userSession);

        return true;
    }

    @Override
    public Result<AuthToken> validateAuthToken(AuthToken token) {

        if (token == null) {
            return error(En_ResultStatus.SESSION_NOT_FOUND);
        }

        UserSession userSession = getUserSession(token.getSessionId());

        if (userSession == null) {
            return error(En_ResultStatus.SESSION_NOT_FOUND);
        }

        if (userSession.checkIsExpired()) {
            log.warn("Session with id {} is expired, block request", userSession.getSessionId());
            closeUserSession(userSession);
            return error(En_ResultStatus.SESSION_NOT_FOUND);
        }

        if (!Objects.equals(userSession.getClientIp(), token.getIp())) {
            log.warn("Security exception, host {} is trying to accessType session {} created for {}",
                    token.getIp(), userSession.getSessionId(), userSession.getClientIp());
            return error(En_ResultStatus.INVALID_SESSION_ID);
        }

        return ok(token);
    }

    private void closeUserSession(UserSession userSession) {
        if (userSession == null) return;
        sessionDAO.removeByKey(userSession.getId());
    }

    private UserSession getUserSession(String appSessionId) {
        UserSession userSession = sessionDAO.findBySID(appSessionId);
        if (userSession == null) {
            log.info("UserSession '{}' doesn't exists", appSessionId);
            return null;
        }
        return userSession;
    }

    private AuthToken makeAuthToken(UserSession userSession) {
        if (userSession == null) {
            return null;
        }
        return new AuthToken(
            userSession.getSessionId(),
            userSession.getClientIp(),
            userSession.getLoginId(),
            userSession.getPersonId(),
            userSession.getCompanyId(),
            getUserRoles(userSession.getLoginId())
        );
    }

    private Set<UserRole> getUserRoles(Long loginId) {
        UserLogin userLogin = new UserLogin();
        userLogin.setId(loginId);
        jdbcManyRelationsHelper.fill(userLogin, "roles");
        return userLogin.getRoles();
    }

    public static String makePasswordString(String password) {
        if (isEmpty(password)) {
            return password;
        } else {
            return "********";
        }
    }
}
