package ru.protei.portal.core.controller.api;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.StringUtils;
import java.util.Base64;

/**
 *  Данные аутентификации
 */
public class Credentials {
    private static Logger log = LoggerFactory.getLogger(Credentials.class);

    public String login;
    public String password;

    boolean isValid()
    {
        return (!StringUtils.isEmpty(login)) && (!StringUtils.isEmpty(password));
    }

    public static Credentials parse(String auth) {
        if (auth == null)
            return null;

        try
        {
            int index = auth.indexOf(' ');
            if (index <= 0)
                return null;

            String auth_type = auth.substring(0, index);
            if (!auth_type.equalsIgnoreCase("basic"))
                return null;

            String xdata = new String(Base64.getDecoder().decode(auth.substring(index + 1)));

            index = xdata.indexOf(':');
            if (index < 0)
                return null;

            Credentials cr = new Credentials();
            cr.login = xdata.substring(0, index);
            cr.password = xdata.substring(index + 1);
            return cr;
        }
        catch (Throwable ex)
        {
            log.error("Error while parsing credentials {}", auth, ex);
            return null;
        }
    }
}