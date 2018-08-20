package ru.protei.portal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.core.utils.config.utils.PropertiesWrapper;
import ru.protei.winter.core.utils.duration.DurationUtils;
import ru.protei.winter.core.utils.duration.IncorrectDurationException;

import java.net.Inet4Address;
import java.util.concurrent.TimeUnit;

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
    private final SvnConfig svnConfig;
    private final LuceneConfig luceneConfig;
    private final ReportConfig reportConfig;
    private final CaseLinkConfig caseLinkConfig;
    private final MailNotificationConfig mailNotificationConfig;

    private final String loginSuffixConfig;

    public PortalConfigData (PropertiesWrapper wrapper) throws ConfigException {
        smtpConfig = new SmtpConfig(wrapper);
        cloudConfig = new CloudConfig(wrapper);
        eventAssemblyConfig = new EventAssemblyConfig(wrapper);
        legacySystemConfig = new LegacySystemConfig(wrapper);
        integrationConfig = new IntegrationConfig(wrapper);
        svnConfig = new SvnConfig(wrapper);
        luceneConfig = new LuceneConfig(wrapper);
        reportConfig = new ReportConfig(wrapper);
        caseLinkConfig = new CaseLinkConfig(wrapper);
        mailNotificationConfig = new MailNotificationConfig(wrapper);

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

    public String getLoginSuffix() {
        return loginSuffixConfig;
    }

    public CloudConfig cloud() {
        return cloudConfig;
    }

    public EventAssemblyConfig eventAssemblyConfig() {
        return eventAssemblyConfig;
    }

    public SvnConfig svn() {
        return svnConfig;
    }

    public LuceneConfig lucene() {
        return luceneConfig;
    }
    
    public ReportConfig reportConfig() {
        return reportConfig;
    }

    public CaseLinkConfig getCaseLinkConfig() {
        return caseLinkConfig;
    }

    public MailNotificationConfig getMailNotificationConfig() {
        return mailNotificationConfig;
    }


    public static class MailNotificationConfig {
        private final String crmUrlInternal;
        private final String crmUrlExternal;
        private final String crmCaseUrlInternal;
        private final String crmCaseUrlExternal;

        public MailNotificationConfig(PropertiesWrapper properties) throws ConfigException {
            crmUrlInternal = properties.getProperty( "crm.url.internal", "http://newportal/crm/" );
            crmUrlExternal = properties.getProperty( "crm.url.external", "http://newportal/crm/" );
            crmCaseUrlInternal = properties.getProperty( "crm.case.url.internal", "http://127.0.0.1:8888/crm.html#issues/issue:id=%d;" );
            crmCaseUrlExternal = properties.getProperty( "crm.case.url.external", "http://127.0.0.1:8888/crm.html#issues/issue:id=%d;" );
        }

        public String getCrmUrlInternal() {
            return crmUrlInternal;
        }

        public String getCrmUrlExternal() {
            return crmUrlExternal;
        }

        public String getCrmCaseUrlInternal() {
            return crmCaseUrlInternal;
        }

        public String getCrmCaseUrlExternal() {
            return crmCaseUrlExternal;
        }
    }

    public static class SmtpConfig {
        private final String host;
        private final String defaultCharset;
        private final int port;
        private final String fromAddress;
        private final String fromAddressAlias;
        private final boolean blockExternalRecipients;
        private final String messageIdPattern;

        public SmtpConfig(PropertiesWrapper properties) throws ConfigException{
            host = properties.getProperty("smtp.host", "smtp.protei.ru");
            port = properties.getProperty("smtp.port", Integer.class, 2525);
            fromAddress = properties.getProperty("smtp.from", "PORTAL");
            fromAddressAlias = properties.getProperty("smtp.from.alias", "DO_NOT_REPLY");
            defaultCharset = properties.getProperty("smtp.charset", "utf-8");
            blockExternalRecipients = properties.getProperty("smtp.block_external_recipients", Boolean.class, false);
            messageIdPattern = properties.getProperty("smtp.message_id_pattern", "%id%@smtp.protei.ru");
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

        public String getMessageIdPattern() {
            return messageIdPattern;
        }

        public String getFromAddressAlias() {
            return fromAddressAlias;
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

    public static class SvnConfig {
        private final String url, username, password, commitMessage;

        public SvnConfig(PropertiesWrapper properties) throws ConfigException {
            this.url = properties.getProperty("svn.url");
            this.username = properties.getProperty("svn.username");
            this.password = properties.getProperty("svn.password");
            this.commitMessage = properties.getProperty("svn.commit_message", "Add document №%2$s to project №%1$s");
        }

        public String getUrl() {
            return url;
        }

        public String getPassword() {
            return password;
        }

        public String getUsername() {
            return username;
        }

        public String getCommitMessage() {
            return commitMessage;
        }
    }

    public static class LuceneConfig {
        private final String indexPath;

        public LuceneConfig(PropertiesWrapper propertiesWrapper) {
            this.indexPath = propertiesWrapper.getProperty("lucene.index_path", "./index");
        }

        public String getIndexPath() {
            return indexPath;
        }
    }

    public static class ReportConfig {
        private final int threadsNumber;
        private final int chunkSize;
        private final long liveTime;
        private final long hangInterval;
        private final String storagePath;

        public ReportConfig(PropertiesWrapper properties) throws ConfigException {
            try {
                this.threadsNumber = properties.getProperty("report.threads", Integer.class, 6);
                this.chunkSize = properties.getProperty("report.chunk.size", Integer.class, 20);
                this.liveTime = DurationUtils.getDuration(properties.getProperty("report.live_time_duration", "3d"), TimeUnit.MILLISECONDS);
                this.hangInterval = TimeUnit.SECONDS.toMillis(properties.getProperty("report.hang_interval_sec", Integer.class, 30 * 60));
                this.storagePath = properties.getProperty("report.storage.path", "reports");
            } catch (IncorrectDurationException e) {
                throw new ConfigException(e);
            }
        }

        public int getThreadsNumber() {
            return threadsNumber;
        }

        public int getChunkSize() {
            return chunkSize;
        }

        public long getLiveTime() {
            return liveTime;
        }

        public long getHangInterval() {
            return hangInterval;
        }

        public String getStoragePath() {
            return storagePath;
        }
    }

    public static class CaseLinkConfig {
        private final String linkCrm;
        private final String linkOldCrm;
        private final String linkYouTrack;

        public CaseLinkConfig(PropertiesWrapper properties) throws ConfigException {
            this.linkCrm = properties.getProperty("case.link.internal", "http://newportal/crm/#issues/issue:id=%id%");
            this.linkOldCrm = properties.getProperty("case.link.internal.old", "http://portal/crm/session/session_support.jsp?id=%id%&&action_ref=SessionManageBean_Support.applyFilterAction_Support");
            this.linkYouTrack = properties.getProperty("case.link.youtrack", "https://youtrack.protei/issue/%id%");
        }

        public String getLinkCrm() {
            return linkCrm;
        }

        public String getLinkOldCrm() {
            return linkOldCrm;
        }

        public String getLinkYouTrack() {
            return linkYouTrack;
        }
    }
}
