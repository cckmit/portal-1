package ru.protei.portal.core.service.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dict.En_AdminState;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.winter.core.utils.net.AddressMask;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.net.InetSocketAddress;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.StringUtils.isEmpty;

/**
 * Created by michael on 29.06.16.
 */
public class AuthServiceImpl implements AuthService {

    private static Logger log = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserLoginDAO userLoginDAO;
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

        if (login.getAdminStateId() == En_AdminState.LOCKED.getId()) {
            log.debug("account [" + login + "] is locked");
            return En_ResultStatus.ACCOUNT_IS_LOCKED;
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

        if (isNotEmpty(userLogin.getIpMaskAllow())) {
            if (!AddressMask.isInRange(
                    new InetSocketAddress(ip, 0),
                    stream(userLogin.getIpMaskAllow()).map(AddressMask::new).collect(Collectors.toList()))) {
                log.debug("login [{}] access denied by ip mask allow = {}", login, ip);
                return error(En_ResultStatus.PERMISSION_DENIED);
            }
        }

        Person person = personDAO.get(userLogin.getPersonId());
        if (person.isFired() || person.isDeleted()) {
            log.debug("login [{}] - person {}, access denied", login, person.isFired() ? "fired" : "deleted");
            return error( En_ResultStatus.PERMISSION_DENIED);
        }

        AuthToken token = new AuthToken(appSessionId);
        token.setIp(ip);
        token.setUserLoginId(userLogin.getId());
        token.setPersonId(userLogin.getPersonId());
        token.setPersonDisplayShortName(userLogin.getDisplayShortName());
        token.setCompanyId(userLogin.getCompanyId());
        token.setCompanyAndChildIds(getCompanyAndChildIds(userLogin.getCompanyId()));
        token.setRoles(getUserRoles(userLogin.getId()));

        log.info("Auth success for {} / {} / {}",
                login,
                token.getRoles() == null ? "null" : token.getRoles().stream().map(UserRole::getCode).collect(Collectors.joining(",")),
                person.toDebugString()
        );

        return ok(token);
    }

    @Override
    public Result<AuthToken> logout(AuthToken token, String ip, String userAgent) {

        log.info("logout(): {} {} {}", ip, userAgent, token);

        if (token == null) {
            return error(En_ResultStatus.SESSION_NOT_FOUND);
        }

        if (!Objects.equals(token.getIp(), ip)) {
            log.warn("Security exception, host {} is trying to close session {} created for {}",
                    ip, token.getSessionId(), token.getIp());
            return error(En_ResultStatus.INVALID_SESSION_ID);
        }

        return ok(token);
    }

    @Override
    public Result<UserLogin> getUserLogin(AuthToken token, Long userLoginId) {
        UserLogin userLogin = userLoginDAO.get(userLoginId);
        jdbcManyRelationsHelper.fillAll(userLogin);
        return ok(userLogin);
    }

    private Set<UserRole> getUserRoles(Long loginId) {
        UserLogin userLogin = new UserLogin();
        userLogin.setId(loginId);
        jdbcManyRelationsHelper.fill(userLogin, "roles");
        return userLogin.getRoles();
    }

    private Collection<Long> getCompanyAndChildIds(Long companyId) {
        Company company = new Company();
        company.setId(companyId);
        jdbcManyRelationsHelper.fill(company, "childCompanies");
        return company.getCompanyAndChildIds();
    }

    public static String makePasswordString(String password) {
        if (isEmpty(password)) {
            return password;
        } else {
            return "********";
        }
    }
}
