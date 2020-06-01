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

    private final CommonConfig commonConfig;
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
    private final YoutrackConfig youtrackConfig;
    private final JiraConfig jiraConfig;
    private final EmployeeConfig employeeConfig;
    private final LdapConfig ldapConfig;
    private final MarkupHelpLink markupHelpLink;
    private final UiConfig uiConfig;

    private final String loginSuffixConfig;
    private final boolean taskSchedulerEnabled;

    private final Long maxFileSize;

    public PortalConfigData (PropertiesWrapper wrapper) throws ConfigException {
        commonConfig = new CommonConfig(wrapper);
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
        youtrackConfig = new YoutrackConfig(wrapper);
        jiraConfig = new JiraConfig(wrapper);
        employeeConfig = new EmployeeConfig(wrapper);
        ldapConfig = new LdapConfig(wrapper);
        markupHelpLink = new MarkupHelpLink(wrapper);
        uiConfig = new UiConfig(wrapper);

        loginSuffixConfig = wrapper.getProperty("auth.login.suffix", "");
        taskSchedulerEnabled = wrapper.getProperty("task.scheduler.enabled", Boolean.class,false);
        maxFileSize = wrapper.getProperty("max.file.size", Long.class, DEFAULT_FILE_SIZE_MEGABYTES);
    }

    public CommonConfig getCommonConfig() {
        return commonConfig;
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

    public YoutrackConfig youtrack() {
        return youtrackConfig;
    }

    public JiraConfig jiraConfig() {
        return jiraConfig;
    }

    public EmployeeConfig getEmployee() {
        return employeeConfig;
    }

    public LdapConfig getLdapConfig() {
        return ldapConfig;
    }

    public MarkupHelpLink getMarkupHelpLink() {
        return markupHelpLink;
    }

    public UiConfig getUiConfig() {
        return uiConfig;
    }

    public boolean isTaskSchedulerEnabled() {
        return taskSchedulerEnabled;
    }

    public Long getMaxFileSize() {return maxFileSize;}

    public static class CommonConfig {
        public CommonConfig( PropertiesWrapper properties ) {
            crmUrlInternal = properties.getProperty( "crm.url.internal", "http://newportal/crm/" );
            crmUrlExternal = properties.getProperty( "crm.url.external", "http://newportal/crm/" );
            crmUrlCurrent = properties.getProperty( "crm.url.current", "http://newportal/crm/" );
            crmUrlFiles = properties.getProperty( "crm.url.files", "http://newportal/crm/" );
            isProductionServer = properties.getProperty( "is.production.server", Boolean.class, false );
        }
        public String getCrmUrlInternal() {
            return crmUrlInternal;
        }

        public String getCrmUrlExternal() {
            return crmUrlExternal;
        }

        public String getCrmUrlCurrent() {
            return crmUrlCurrent;
        }

        public String getCrmUrlFiles() {
            return crmUrlFiles;
        }

        public boolean isProductionServer() {
            return isProductionServer;
        }

        private final String crmUrlInternal;
        private final String crmUrlExternal;
        private final String crmUrlCurrent;
        private final String crmUrlFiles;
        private final Boolean isProductionServer;
    }

    public static class MailNotificationConfig extends CommonConfig {
        private final String crmCaseUrl;
        private final String crmProjectUrl;
        private final String contractUrl;
        private final String crmDocumentPreviewUrl;
        private final String crmEmployeeRegistrationUrl;
        private final String[] crmEmployeeRegistrationNotificationsRecipients;
        private final String[] crmRoomReservationNotificationsRecipients;

        public MailNotificationConfig(PropertiesWrapper properties) throws ConfigException {
            super(properties);
            crmCaseUrl = properties.getProperty( "crm.case.url", "#issues/issue:id=%d;" );
            crmProjectUrl = properties.getProperty("crm.project.url", "#project_preview:id=%d");
            contractUrl = properties.getProperty( "crm.contract.url", "#contracts/contract:id=%d;" );
            crmDocumentPreviewUrl = properties.getProperty( "crm.document.url.preview", "#doc_preview:id=%d");
            crmEmployeeRegistrationUrl = properties.getProperty( "crm.employee_registration.url");
            crmEmployeeRegistrationNotificationsRecipients = properties.getProperty( "crm.employee_registration.recipients", "" ).split(",");
            crmRoomReservationNotificationsRecipients = properties.getProperty("crm.room_reservation.recipients", "").split(",");
        }

        public String getCrmCaseUrl() {
            return crmCaseUrl;
        }

        public String getCrmProjectUrl() {
            return crmProjectUrl;
        }

        public String getContractUrl() {
            return contractUrl;
        }

        public String getCrmDocumentPreviewUrl() {
            return crmDocumentPreviewUrl;
        }

        public String getCrmEmployeeRegistrationUrl() {
            return crmEmployeeRegistrationUrl;
        }

        public String[] getCrmEmployeeRegistrationNotificationsRecipients() {
            return crmEmployeeRegistrationNotificationsRecipients;
        }

        public String[] getCrmRoomReservationNotificationsRecipients() {
            return crmRoomReservationNotificationsRecipients;
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
            this.jdbcURL = properties.getProperty("syb.jdbc.url", "jdbc:sybase:Tds:192.168.101.140:2642/RESV3");
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

            logger.info("legacy config, driver={}, url={}, export={}, import={}", jdbcDriver, jdbcURL, exportEnabled, importEnabled);
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
        private final boolean redmineEnabled;
        private final boolean redmineBackchannelEnabled;
        private final boolean youtrackEnabled;
        private final boolean youtrackCompanySyncEnabled;
        private final boolean youtrackEmployeeSyncEnabled;
        private final boolean youtrackLinksMigrationEnabled;
        private final boolean jiraEnabled;
        private final boolean jiraBackchannelEnabled;

        private final boolean redminePatchEnabled;

        public IntegrationConfig(PropertiesWrapper properties) throws ConfigException {
            redmineEnabled = properties.getProperty("integration.redmine", Boolean.class, false);
            redmineBackchannelEnabled = properties.getProperty("integration.redmine.backchannel", Boolean.class, false);
            youtrackEnabled = properties.getProperty("integration.youtrack", Boolean.class, false);
            youtrackCompanySyncEnabled = properties.getProperty("integration.youtrack.companies", Boolean.class, false);
            youtrackEmployeeSyncEnabled = properties.getProperty("integration.youtrack.employees", Boolean.class, false);
            youtrackLinksMigrationEnabled = properties.getProperty("migration.youtrack.links", Boolean.class, false);
            jiraEnabled = properties.getProperty("integration.jira", Boolean.class, false);
            jiraBackchannelEnabled = properties.getProperty("integration.jira.backchannel", Boolean.class, false);

            redminePatchEnabled = properties.getProperty("integration.redmine.patch", Boolean.class, false);
        }

        public boolean isRedmineEnabled() {
            return redmineEnabled;
        }

        public boolean isRedmineBackchannelEnabled() {
            return redmineBackchannelEnabled;
        }

        public boolean isYoutrackEnabled() {
            return youtrackEnabled;
        }

        public boolean isYoutrackCompanySyncEnabled() {
            return youtrackCompanySyncEnabled;
        }
        public boolean isYoutrackEmployeeSyncEnabled() {
            return youtrackEmployeeSyncEnabled;
        }
        public boolean isYoutrackLinksMigrationEnabled() {
            return youtrackLinksMigrationEnabled;
        }

        public boolean isJiraEnabled() {
            return jiraEnabled;
        }

        public boolean isRedminePatchEnabled() {
            return redminePatchEnabled;
        }

        public boolean isJiraBackchannelEnabled() {
            return jiraBackchannelEnabled;
        }
    }

    public static class SvnConfig {
        private final String url, username, password, commitMessageAdd, commitMessageUpdate, commitMessageRemove;

        public SvnConfig(PropertiesWrapper properties) throws ConfigException {
            this.url = properties.getProperty("svn.url");
            this.username = properties.getProperty("svn.username");
            this.password = properties.getProperty("svn.password");
            this.commitMessageAdd = properties.getProperty("svn.commit_message", "Add document №%2$s to project №%1$s (%3$s)");
            this.commitMessageUpdate = properties.getProperty("svn.commit_message.update", "Update document №%2$s at project №%1$s (%3$s)");
            this.commitMessageRemove = properties.getProperty("svn.commit_message.remove", "Remove document №%2$s at project №%1$s (%3$s)");
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

        public String getCommitMessageAdd() {
            return commitMessageAdd;
        }

        public String getCommitMessageUpdate() {
            return commitMessageUpdate;
        }

        public String getCommitMessageRemove() {
            return commitMessageRemove;
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
        private final String linkYouTrack;

        public CaseLinkConfig(PropertiesWrapper properties) throws ConfigException {
            this.linkCrm = properties.getProperty("case.link.internal", "http://newportal/crm/#issues/issue_preview:id=%id%");
            this.linkYouTrack = properties.getProperty("case.link.youtrack", "https://youtrack.protei.ru/issue/%id%");
        }

        public String getLinkCrm() {
            return linkCrm;
        }

        public String getLinkYouTrack() {
            return linkYouTrack;
        }
    }

    public static class YoutrackConfig {
        private final String apiBaseUrl;
        private final String login;
        private final String authToken;
        private final String employeeRegistrationSyncSchedule;
        private final String equipmentProject;
        private final String adminProject;
        private final String phoneProject;
        private final Long youtrackUserId;
        private final String youtrackCustomFieldCompanyId;

        public YoutrackConfig(PropertiesWrapper properties) {
            apiBaseUrl = properties.getProperty("youtrack.api.baseurl");
            login = properties.getProperty("youtrack.api.login", "portal");
            authToken = properties.getProperty("youtrack.api.auth_token");
            employeeRegistrationSyncSchedule = properties.getProperty("youtrack.employee_registration.sync_schedule", "0 */15 * * * *");
            equipmentProject = properties.getProperty("youtrack.employee_registration.equipment_project");
            adminProject = properties.getProperty("youtrack.employee_registration.admin_project");
            phoneProject = properties.getProperty("youtrack.employee_registration.phone_project");
            youtrackUserId = properties.getProperty("youtrack.user_id_for_synchronization", Long.class);
            youtrackCustomFieldCompanyId = properties.getProperty("youtrack.custom_field_company_id");
        }

        public String getApiBaseUrl() {
            return apiBaseUrl;
        }

        public String getLogin() {
            return login;
        }

        public String getAuthToken() {
            return authToken;
        }

        public String getEmployeeRegistrationSyncSchedule() {
            return employeeRegistrationSyncSchedule;
        }

        public String getEquipmentProject() {
            return equipmentProject;
        }

        public String getAdminProject() {
            return adminProject;
        }

        public String getPhoneProject() {
            return phoneProject;
        }

        public Long getYoutrackUserId() {
            return youtrackUserId;
        }

        public String getYoutrackCustomFieldCompanyId() {
            return youtrackCustomFieldCompanyId;
        }
    }

    public static class JiraConfig {

        private final String jiraUrl;
        private final int queueLimit;

        public JiraConfig(PropertiesWrapper properties) throws ConfigException {
            jiraUrl = properties.getProperty("jira.url",  "");
            queueLimit = properties.getProperty("integration.jira.queue.limit", Integer.class, 0);
        }

        public String getJiraUrl() {
            return jiraUrl;
        }

        public int getQueueLimit() {
            return queueLimit;
        }
    }

    public static class EmployeeConfig {

        private final String avatarPath;

        public EmployeeConfig(PropertiesWrapper propertiesWrapper) {
            avatarPath = propertiesWrapper.getProperty( "employee.avatar.path", "/usr/protei/shared/avatars/" );
        }

        public String getAvatarPath() {
            return avatarPath;
        }
    }

    public static class LdapConfig {
        private final String url;

        public LdapConfig(PropertiesWrapper properties) {
            url = properties.getProperty("ldap.url", "ldap://ldap_1.protei");
        }

        public String getUrl() {
            return url;
        }
    }

    public static class MarkupHelpLink {
        private final String markdown;
        private final String jiraMarkup;

        public MarkupHelpLink(PropertiesWrapper properties) {
            markdown = properties.getProperty("markup.markdown");
            jiraMarkup = properties.getProperty("markup.jira_markup");
        }

        public String getMarkdown() {
            return markdown;
        }

        public String getJiraMarkup() {
            return jiraMarkup;
        }
    }

    public static class UiConfig {
        private final Long issueAssignmentDeskLimit;

        public UiConfig(PropertiesWrapper properties) {
            issueAssignmentDeskLimit = properties.getProperty("ui.issue-assignment.desk.limit", Long.class, 200L);
        }

        public Long getIssueAssignmentDeskLimit() {
            return issueAssignmentDeskLimit;
        }
    }

    private final static Long DEFAULT_FILE_SIZE_MEGABYTES = 10L;
}
