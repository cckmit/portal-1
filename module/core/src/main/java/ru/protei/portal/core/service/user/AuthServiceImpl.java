package ru.protei.portal.core.service.user;

import org.apache.commons.lang3.time.DateUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dao.CompanyDAO;
import ru.protei.portal.core.model.dao.PersonDAO;
import ru.protei.portal.core.model.dao.UserLoginDAO;
import ru.protei.portal.core.model.dao.UserSessionDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.service.EmployeeRegistrationDataSyncRunner;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by michael on 29.06.16.
 */
public class AuthServiceImpl implements AuthService {

    private static Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    @Autowired
    private UserLoginDAO userLoginDAO;

    @Autowired
    private UserSessionDAO sessionDAO;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private CompanyDAO companyDAO;

    @Autowired
    private LDAPAuthProvider ldapAuthProvider;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    private Map<String, UserSessionDescriptor> sessionCache;

    @Autowired
    private PortalConfig config;

    @Autowired
    private EmployeeRegistrationDataSyncRunner employeeRegistrationDataSyncRunner;

    public AuthServiceImpl() {
        this.sessionCache = new HashMap<>();
    }

    @Override
    public UserSessionDescriptor findSession(String appSessionId, String ip, String userAgent) {
        UserSessionDescriptor descriptor = getSessionDescriptor(appSessionId);

        if (descriptor == null) return null;


        // validate session
        if (descriptor.getSession() != null) {

            if (!descriptor.isValid()) {
                logger.warn("invalid session " + descriptor.getSessionId());
                closeSessionDesc(descriptor);
                return null;
            }

            if (descriptor.isExpired()) {
                logger.warn("session with id " + descriptor.getSessionId() + " is expired, block request");
                closeSessionDesc(descriptor);
                return null;
            }

            if (!descriptor.getSession().getClientIp().equals(ip)) {
                logger.warn("Security exception, host " + ip + " is trying to accessType session " + descriptor.getSessionId() + " created for " + descriptor.getSession().getClientIp());
                return null;
            }

            // now, all is ok

            return descriptor;
        }

        return null;
    }

    public UserSessionDescriptor findSession (AuthToken token) {
        return findSession( token.getSid(), token.getIp(), "" );
    }

    private UserSessionDescriptor getSessionDescriptor(String appSessionId) {
        // get from cache
        UserSessionDescriptor descriptor = sessionCache.get(appSessionId);

        if (descriptor == null) {
            logger.debug(" no session found in cache, id=" + appSessionId);

            // try to restore from database
            UserSession appSession = sessionDAO.findBySID(appSessionId);

            if (appSession != null) {
                //ok
                logger.debug("session " + appSessionId + " restored from database");
                descriptor = new UserSessionDescriptor();
                descriptor.init(appSession);

                UserLogin userLogin = userLoginDAO.get(appSession.getLoginId());
                jdbcManyRelationsHelper.fill(userLogin, "roles");

                Company company = companyDAO.get( appSession.getCompanyId() );
                jdbcManyRelationsHelper.fillAll(company);
                descriptor.login(userLogin,
                        personDAO.get(appSession.getPersonId()),
                        company
                );

                sessionCache.put(appSessionId, descriptor);
            } else {
                logger.debug("session " + appSessionId + " doesn't exists");
                return null;
            }
        }
        return descriptor;
    }

    @Override
    public CoreResponse<UserSessionDescriptor> login(String appSessionId, String ulogin, String pwd, String ip, String userAgent) {

        employeeRegistrationDataSyncRunner.__debug();

        String loginSuffix = config.data().getLoginSuffix();
        if (!ulogin.contains("@") && !loginSuffix.isEmpty()) {
            logger.debug("login [{}] missed suffix [{}], forced to use suffix", ulogin, loginSuffix);
            ulogin += loginSuffix;
        }

        UserLogin login = userLoginDAO.findByLogin(ulogin);
        if (login == null) {
            logger.debug("login [" + ulogin + "] not found, auth-failed");
            return new CoreResponse().error(En_ResultStatus.INVALID_LOGIN_OR_PWD);
        }

        jdbcManyRelationsHelper.fill(login, "roles");

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
            En_ResultStatus status = ldapAuthProvider.checkAuth(ulogin, pwd);
            if (status != En_ResultStatus.OK)
                return new CoreResponse().error(status);

        } else {
            // check MD5
            String md5Hash = DigestUtils.md5DigestAsHex(pwd.getBytes());
            if (login.getUpass() == null || !login.getUpass().equalsIgnoreCase(md5Hash)) {
                logger.debug("login " + ulogin + " - invalid password, auth-failed");
                return new CoreResponse().error(En_ResultStatus.INVALID_LOGIN_OR_PWD);
            }
        }

        UserSessionDescriptor descriptor = getSessionDescriptor(appSessionId);
        if (descriptor != null) {
            if (!descriptor.getLogin().getId().equals(login.getId())) {
                logger.warn("Security exception, client " + login.getUlogin() + " from host " + ip
                        + " is trying to accessType session " + descriptor.getSessionId()
                        + " created for " + descriptor.getLogin().getUlogin() + "@" + descriptor.getSession().getClientIp());
                return new CoreResponse().error(En_ResultStatus.INVALID_SESSION_ID);
            }

            if (!descriptor.getSession().getClientIp().equals(ip)) {
                logger.warn("Security exception, host " + ip + " is trying to accessType session " + descriptor.getSessionId() + " created for " + descriptor.getSession().getClientIp());
                return new CoreResponse().error(En_ResultStatus.INVALID_SESSION_ID);
            }
        } else {
            descriptor = new UserSessionDescriptor();
        }

        Person person = personDAO.get(login.getPersonId());
        Company company = companyDAO.get(person.getCompanyId());
        jdbcManyRelationsHelper.fillAll( company );

        logger.debug("Auth success for " + ulogin + " / " + ( login.getRoles() == null ? "null" : login.getRoles().stream().map( UserRole::getCode ).collect( Collectors.joining("," ) ) ) + "/" + person.toDebugString());

        UserSession s = new UserSession();
        s.setClientIp(ip);
        s.setCreated(new Date());
        s.setSessionId(appSessionId);
        s.setExpired(DateUtils.addSeconds(new Date(), AuthService.DEF_APP_SESSION_LIVE_TIME));

        descriptor.init(s);

        descriptor.getSession().setCompanyId(person.getCompanyId());
        descriptor.getSession().setLoginId(login.getId());
        descriptor.getSession().setPersonId(login.getPersonId());
        descriptor.getSession().setExpired(DateUtils.addHours(new Date(), 3));
        descriptor.login(login, person, company);

        sessionDAO.removeByCondition("client_ip=? and login_id=?", descriptor.getSession().getClientIp(),
                login.getId());
        sessionDAO.persist(descriptor.getSession());

        sessionCache.put(descriptor.getSessionId(), descriptor);
        return new CoreResponse().success(descriptor);
    }

    @Override
    public boolean logout(String appSessionId, String ip, String userAgent) {
        UserSessionDescriptor descriptor = getSessionDescriptor(appSessionId);

        if (descriptor == null) return false;

        if (!descriptor.getSession().getClientIp().equals(ip)) {
            logger.warn("Security exception, host " + ip + " is trying to close session " + descriptor.getSessionId() + " created for " + descriptor.getSession().getClientIp());
            return false;
        }

        closeSessionDesc(descriptor);

        return true;
    }

    @Override
    public UserSessionDescriptor getUserSessionDescriptor(HttpServletRequest request) {
        return ((UserSessionDescriptor)request.getSession().getAttribute( "auth-session-data" ));
    }

    private void closeSessionDesc(UserSessionDescriptor descriptor) {
        sessionDAO.remove(descriptor.getSession());
        sessionCache.remove(descriptor.getSessionId());
        descriptor.close();
    }
}
