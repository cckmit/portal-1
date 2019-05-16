package ru.protei.portal.test.ldap;

import javax.naming.AuthenticationException;
import javax.naming.Context;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.DirContext;
import javax.naming.directory.InitialDirContext;
import javax.naming.directory.SearchResult;
import java.util.Hashtable;

public class LdapAuthTest {

    public static void main(String[] args) {

        Hashtable<Object,Object> env = new Hashtable<>();

        env.put( Context.INITIAL_CONTEXT_FACTORY, "com.sun.jndi.ldap.LdapCtxFactory");
        env.put(Context.PROVIDER_URL, "ldaps://ldap_1.protei");
        env.put(Context.SECURITY_AUTHENTICATION, "simple");

        env.put(Context.SECURITY_PRINCIPAL, "uid=support,ou=Users,dc=protei,dc=ru");
        env.put(Context.SECURITY_CREDENTIALS, "elephant");

        DirContext rootCtx = null;
        try {
            rootCtx = new InitialDirContext(env);

/*
            NamingEnumeration< SearchResult > renum = rootCtx.search("ou=Users,dc=protei,dc=ru", "(&(uid={0})(objectClass={1}))", new Object[]{"support","posixAccount"}, null);

            while (renum.hasMoreElements()) {
                SearchResult result = renum.next();

                Attribute uidAttr = result.getAttributes().get("uid");
                if (uidAttr != null
                        && uidAttr.get() instanceof String
                        && ((String)uidAttr.get()).equals("support"))
                    System.out.println("OK!");
            }
*/
        }
        catch ( AuthenticationException e) {
            System.out.println("invalid login or password for support, "+ e.getMessage());
            e.printStackTrace();
        }
        catch ( NamingException e) {
            System.out.println("LDAP auth error, " +  e.getMessage());
            e.printStackTrace();
        }
        finally {
            try { rootCtx.close(); } catch (Throwable e) {}
        }
    }
}
