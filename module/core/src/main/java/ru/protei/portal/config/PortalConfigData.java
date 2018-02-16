package ru.protei.portal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.core.utils.config.utils.PropertiesWrapper;

import java.net.Inet4Address;

/**
 * Created by michael on 31.05.17.
 */
public class PortalConfigData {

    private static Logger logger = LoggerFactory.getLogger(PortalConfigData.class);

    private SmtpConfig smtpConfig;
    private CloudConfig cloudConfig;
    private final EventAssemblyConfig eventAssemblyConfig;
    private final ExportDataConfig exportDataConfig;

    private final String crmCaseUrl;

    public PortalConfigData (PropertiesWrapper wrapper) throws ConfigException {
        smtpConfig = new SmtpConfig(wrapper);
        cloudConfig = new CloudConfig(wrapper);
        eventAssemblyConfig = new EventAssemblyConfig(wrapper);
        exportDataConfig = new ExportDataConfig(wrapper);

        crmCaseUrl = wrapper.getProperty( "crm.case.url", "http://127.0.0.1:8888/crm.html#issues/issue:id=%d;" );
    }

    public ExportDataConfig exportConfig () {
        return exportDataConfig;
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

    public EventAssemblyConfig eventAssemblyConfig() {
        return eventAssemblyConfig;
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

    public static class EventAssemblyConfig {
        private final long waitingPeriod;

        public EventAssemblyConfig(PropertiesWrapper properties) throws ConfigException {
            long v = properties.getProperty("core.waiting_period", Long.class, 30L);

            if (v > java.util.concurrent.TimeUnit.MINUTES.toSeconds(2)) {
                v = java.util.concurrent.TimeUnit.MINUTES.toSeconds(2);
            }
            else if (v < 10 ){
                v = 10;
            }

            logger.debug("Use event assembly period = {}", v);
            waitingPeriod = v;
        }

        public long getWaitingPeriod() {
            return waitingPeriod;
        }

        public long getWaitingPeriodMillis() {
            return getWaitingPeriod() * 1000;
        }
    }


    public static class ExportDataConfig {
        private final String jdbcURL;
        private final String login;
        private final String passwd;
        private final boolean enabled;

        private final String instanceId;

        public ExportDataConfig(PropertiesWrapper properties) throws ConfigException {
            this.enabled = properties.getProperty("export.syb.enabled", Boolean.class,false);
            this.jdbcURL = properties.getProperty("export.syb.jdbc.url", "jdbc:sybase:Tds:192.168.1.55:2638/PORTAL2017");
            this.login = properties.getProperty("export.syb.jdbc.login", "dba");
            this.passwd = properties.getProperty("export.syb.jdbc.pwd", "sql");
            try {
                this.instanceId = properties.getProperty("export.syb.identity", Inet4Address.getLocalHost().getHostAddress());
            }
            catch (Exception e) {
                logger.error("unable to get local ip address", e);
                throw new ConfigException(e);
            }
        }

        public String getInstanceId() {
            return instanceId;
        }

        public String getJdbcURL() {
            return jdbcURL;
        }

        public String getLogin() {
            return login;
        }

        public String getPasswd() {
            return passwd;
        }

        public boolean isEnabled() {
            return enabled;
        }
    }
}
