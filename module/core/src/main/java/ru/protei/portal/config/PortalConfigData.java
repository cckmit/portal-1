package ru.protei.portal.config;

import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.core.utils.config.utils.PropertiesWrapper;

/**
 * Created by michael on 31.05.17.
 */
public class PortalConfigData {

    private SmtpConfig smtpConfig;
    private CloudConfig cloudConfig;

    private final String crmCaseUrl;

    public PortalConfigData (PropertiesWrapper wrapper) throws ConfigException {
        smtpConfig = new SmtpConfig(wrapper);
        cloudConfig = new CloudConfig(wrapper);

        crmCaseUrl = wrapper.getProperty( "crm.case.url", "http://127.0.0.1:8888/crm.html#issues/issue:id=%d;" );
    }

    public SmtpConfig smtp () {
        return this.smtpConfig;
    }

    public String getCrmCaseUrl() {
        return crmCaseUrl;
    }

    public CloudConfig cloud() {
        return cloudConfig;
    }

    public static class SmtpConfig {
        String host;
        String defaultCharset;
        int port;
        String fromAddress;

        public SmtpConfig(PropertiesWrapper properties) throws ConfigException{
            host = properties.getProperty("smtp.host", "smtp.protei.ru");
            port = properties.getProperty("smtp.port", Integer.class, 2525);
            fromAddress = properties.getProperty("smtp.from", "PORTAL");
            defaultCharset = properties.getProperty("smtp.charset", "utf-8");
        }

        public String getHost() {
            return host;
        }

        public String getDefaultCharset() {
            return defaultCharset;
        }

        public int getPort() {
            return port;
        }

        public String getFromAddress() {
            return fromAddress;
        }
    }

    public static class CloudConfig {
        private final String storagePath;
        private final String user;
        private final String password;

        public CloudConfig(PropertiesWrapper properties) throws ConfigException{
            storagePath = properties.getProperty("cloud.path");
            user = properties.getProperty("cloud.user");
            password = properties.getProperty("cloud.password");
        }

        public String getStoragePath() {
            return storagePath;
        }

        public String getUser() {
            return user;
        }

        public String getPassword() {
            return password;
        }
    }

}
