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
    private final LegacySystemConfig legacySystemConfig;
    private final IntegrationConfig integrationConfig;

    private final String crmCaseUrl;
    private final String loginSuffixConfig;

    public PortalConfigData (PropertiesWrapper wrapper) throws ConfigException {
        smtpConfig = new SmtpConfig(wrapper);
        cloudConfig = new CloudConfig(wrapper);
        eventAssemblyConfig = new EventAssemblyConfig(wrapper);
        legacySystemConfig = new LegacySystemConfig(wrapper);
        integrationConfig = new IntegrationConfig(wrapper);

        crmCaseUrl = wrapper.getProperty( "crm.case.url", "http://127.0.0.1:8888/crm.html#issues/issue:id=%d;" );
        loginSuffixConfig = wrapper.getProperty("auth.login.suffix", "");
    }

    public IntegrationConfig integrationConfig() {
        return integrationConfig;
    }

    public LegacySystemConfig legacySysConfig() {
        return legacySystemConfig;
    }

    public SmtpConfig smtp () {
        return this.smtpConfig;
    }

    public String getCrmCaseUrl() {
        return crmCaseUrl;
    }

    public String getLoginSuffix() {
        return loginSuffixConfig;
    }

    public CloudConfig cloud() {
        return cloudConfig;
    }

    public EventAssemblyConfig eventAssemblyConfig() {
        return eventAssemblyConfig;
    }

    public static class SmtpConfig {
        private final String host;
        private final String defaultCharset;
        private final int port;
        private final String fromAddress;
        private final boolean blockExternalRecipients;

        public SmtpConfig(PropertiesWrapper properties) throws ConfigException{
            host = properties.getProperty("smtp.host", "smtp.protei.ru");
            port = properties.getProperty("smtp.port", Integer.class, 2525);
            fromAddress = properties.getProperty("smtp.from", "PORTAL");
            defaultCharset = properties.getProperty("smtp.charset", "utf-8");
            blockExternalRecipients = properties.getProperty("smtp.block_external_recipients", Boolean.class, false);
        }

        public boolean isBlockExternalRecipients() {
            return blockExternalRecipients;
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


    public static class LegacySystemConfig {
        private final String jdbcDriver;
        private final String jdbcURL;
        private final String login;
        private final String passwd;
        private final boolean exportEnabled;

        // create a normal implementation of import-service
        private final boolean importEnabled;
        private final boolean importEmployeesEnabled;

        private final String instanceId;

        public LegacySystemConfig(PropertiesWrapper properties) throws ConfigException {
            this.jdbcDriver = properties.getProperty("syb.jdbc.driver", "net.sourceforge.jtds.jdbc.Driver");
            this.jdbcURL = properties.getProperty("syb.jdbc.url", "jdbc:sybase:Tds:192.168.1.55:2638/PORTAL2017");
            this.login = properties.getProperty("syb.jdbc.login", "dba");
            this.passwd = properties.getProperty("syb.jdbc.pwd", "sql");

            this.exportEnabled = properties.getProperty("syb.export.enabled", Boolean.class,false);
            this.importEnabled = properties.getProperty("syb.import.enabled", Boolean.class,true);

            this.importEmployeesEnabled = properties.getProperty("syb.import.employees", Boolean.class,false);

            try {
                this.instanceId = properties.getProperty("syb.export.identity", Inet4Address.getLocalHost().getHostAddress());
            }
            catch (Exception e) {
                logger.error("unable to get local ip address", e);
                throw new ConfigException(e);
            }

            logger.debug("legacy config, driver={}, url={}, export={}, import={}", jdbcDriver, jdbcURL, exportEnabled, importEnabled);
        }

        public String getJdbcDriver() {
            return jdbcDriver;
        }

        public boolean isImportEnabled() {
            return importEnabled;
        }

        public boolean isImportEmployeesEnabled() {
            return importEmployeesEnabled;
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

        public boolean isExportEnabled() {
            return exportEnabled;
        }
    }


    public static class IntegrationConfig {
        private final boolean hpsmEnabled;
        private final boolean redmineEnabled;

        public IntegrationConfig(PropertiesWrapper properties) throws ConfigException {
            hpsmEnabled = properties.getProperty("integration.hpsm", Boolean.class, false);
            redmineEnabled = properties.getProperty("integration.redmine", Boolean.class, false);
        }


        public boolean isHpsmEnabled() {
            return hpsmEnabled;
        }

        public boolean isRedmineEnabled() {
            return redmineEnabled;
        }
    }
}
