package ru.protei.portal.core.service.auth;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.core.model.dict.En_ResultStatus;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

import static ru.protei.portal.core.service.auth.AuthServiceImpl.makePasswordString;

/**
 * Created by michael on 30.06.16.
 */
public class LDAPAuthProvider {

    @Autowired
    PortalConfig config;

    public static String BASE_DN = "ou=Users,dc=protei,dc=ru";

    Logger logger = LoggerFactory.getLogger("logger-security");

    public En_ResultStatus checkAuth (String username, String pwd) {
        logger.info( "checkAuth(): username={} password={}", username, makePasswordString(pwd) );
        Hashtable<Object,Object> env = new Hashtable<>();

        env.put(Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, config.data().getLdapConfig().getUrl());
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        env.put(Context.SECURITY_PRINCIPAL, "uid="+username+",ou=Users,dc=protei,dc=ru");
        env.put(Context.SECURITY_CREDENTIALS, pwd);

        // enable dump of packets
//            env.put("com.sun.jndi.ldap.trace.ber", System.err);

        DirContext rootCtx = null;
        try {
            rootCtx = new InitialDirContext(env);

            NamingEnumeration<SearchResult> renum = rootCtx.search(BASE_DN, "(&(uid={0})(objectClass={1}))", new Object[]{username,"posixAccount"}, null);

            while (renum.hasMoreElements()) {
                SearchResult result = renum.next();

                Attribute uidAttr = result.getAttributes().get("uid");
                if (uidAttr != null
                        && uidAttr.get() instanceof String
                        && ((String)uidAttr.get()).equals(username))
                    return En_ResultStatus.OK;
            }
        }
        catch (AuthenticationException e) {
            logger.debug("invalid login or password for " + username +"/"+ e.getMessage());
            return En_ResultStatus.INVALID_LOGIN_OR_PWD;
        }
        catch (NamingException e) {
            logger.error("LDAP auth error", e);
            return En_ResultStatus.INTERNAL_ERROR;
        }
        finally {
            try { rootCtx.close(); } catch (Throwable e) {}
        }

        return En_ResultStatus.INVALID_LOGIN_OR_PWD;
    }
}
