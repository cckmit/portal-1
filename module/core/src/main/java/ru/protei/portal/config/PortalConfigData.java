package ru.protei.portal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.core.utils.config.utils.PropertiesWrapper;
import ru.protei.winter.core.utils.duration.DurationUtils;
import ru.protei.winter.core.utils.duration.IncorrectDurationException;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.StringUtils.isNotEmpty;

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
    private final UitsConfig uitsConfig;
    private final Enterprise1CConfig enterprise1CConfig;
    private final JiraConfig jiraConfig;
    private final EmployeeConfig employeeConfig;
    private final LdapConfig ldapConfig;
    private final UiConfig uiConfig;
    private final MailCommentConfig mailCommentConfig;
    private final NRPEConfig nrpeConfig;
    private final AutoOpenConfig autoOpenConfig;

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
        uitsConfig = new UitsConfig(wrapper);
        enterprise1CConfig = new Enterprise1CConfig(wrapper);
        jiraConfig = new JiraConfig(wrapper);
        employeeConfig = new EmployeeConfig(wrapper);
        ldapConfig = new LdapConfig(wrapper);
        uiConfig = new UiConfig(wrapper);
        mailCommentConfig = new MailCommentConfig(wrapper);
        nrpeConfig = new NRPEConfig(wrapper);
        autoOpenConfig = new AutoOpenConfig(wrapper);

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

    public UitsConfig uits() {
        return uitsConfig;
    }

    public Enterprise1CConfig enterprise1C() {
        return enterprise1CConfig;
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

    public UiConfig getUiConfig() {
        return uiConfig;
    }

    public MailCommentConfig getMailCommentConfig() {
        return mailCommentConfig;
    }

    public NRPEConfig getNrpeConfig() {
        return nrpeConfig;
    }

    public AutoOpenConfig getAutoOpenConfig() {
        return autoOpenConfig;
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
            systemId = properties.getProperty( "system.id", "" );
            systemUserId = properties.getProperty("system.user.id", Long.class, null);
            cardbatchCompanyPartnerId = properties.getProperty("cardbatch.company.partner.id", Long.class, null);
            contractCuratorsDepartmentsIds = properties.getProperty("contract.curators_departments_ids", String.class, "").split(",");
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

        public String getSystemId() {
            return systemId;
        }

        public Long getSystemUserId() {
            return systemUserId;
        }

        public Long getCardbatchCompanyPartnerId() {
            return cardbatchCompanyPartnerId;
        }

        public String[] getContractCuratorsDepartmentsIds() {
            return contractCuratorsDepartmentsIds;
        }

        private final String crmUrlInternal;
        private final String crmUrlExternal;
        private final String crmUrlCurrent;
        private final String crmUrlFiles;
        private final Boolean isProductionServer;
        private final String systemId;
        private final Long systemUserId;
        private final Long cardbatchCompanyPartnerId;
        private final String[] contractCuratorsDepartmentsIds;
    }

    public static class MailNotificationConfig extends CommonConfig {
        private final String crmCaseUrl;
        private final String crmProjectUrl;
        private final String contractUrl;
        private final String deliveryUrl;
        private final String crmDocumentPreviewUrl;
        private final String crmEmployeeRegistrationUrl;
        private final String crmReservedIpsUrl;
        private final String[] crmEmployeeRegistrationNotificationsRecipients;
        private final String[] crmEmployeeRegistrationCommentNotificationsRecipients;
        private final String[] crmRoomReservationNotificationsRecipients;
        private final String[] crmIpReservationNotificationsRecipients;
        private final String[] crmBirthdaysNotificationsRecipients;
        private final String[] crmEducationRequestCourseRecipients;
        private final String[] crmEducationRequestConferenceRecipients;
        private final String[] crmEducationRequestLiteratureRecipients;
        private final String[] crmEducationRequestApprovedCourseRecipients;
        private final String[] crmEducationRequestApprovedConferenceRecipients;
        private final String[] crmEducationRequestApprovedLiteratureRecipients;
        private final boolean deliveryNotificationEnabled;

        public MailNotificationConfig(PropertiesWrapper properties) throws ConfigException {
            super(properties);
            crmCaseUrl = properties.getProperty( "crm.case.url", "#issues/issue:id=%d;" );
            crmProjectUrl = properties.getProperty("crm.project.url", "#project_preview:id=%d");
            contractUrl = properties.getProperty( "crm.contract.url", "#contracts/contract:id=%d;" );
            deliveryUrl = properties.getProperty( "crm.delivery.url", "#deliveries/delivery:id=%d" );
            crmDocumentPreviewUrl = properties.getProperty( "crm.document.url.preview", "#doc_preview:id=%d");
            crmEmployeeRegistrationUrl = properties.getProperty( "crm.employee_registration.url");
            crmReservedIpsUrl = properties.getProperty("crm.reserved_ips.url", "#reserved_ips");
            crmEmployeeRegistrationNotificationsRecipients = properties.getProperty( "crm.employee_registration.recipients", "" ).split(",");
            crmEmployeeRegistrationCommentNotificationsRecipients = properties.getProperty("crm.employee_registration.comment.recipients", "").split(",");
            crmRoomReservationNotificationsRecipients = properties.getProperty("crm.room_reservation.recipients", "").split(",");
            crmIpReservationNotificationsRecipients = properties.getProperty("crm.ip_reservation.recipients", "").split(",");
            crmBirthdaysNotificationsRecipients = properties.getProperty("crm.birthdays.recipients", "").split(",");
            crmEducationRequestCourseRecipients = properties.getProperty("crm.education.request.course.recipients", "").split(",");
            crmEducationRequestConferenceRecipients = properties.getProperty("crm.education.request.conference.recipients", "").split(",");
            crmEducationRequestLiteratureRecipients = properties.getProperty("crm.education.request.literature.recipients", "").split(",");
            crmEducationRequestApprovedCourseRecipients = properties.getProperty("crm.education.request.approved.course.recipients", "").split(",");
            crmEducationRequestApprovedConferenceRecipients = properties.getProperty("crm.education.request.approved.conference.recipients", "").split(",");
            crmEducationRequestApprovedLiteratureRecipients = properties.getProperty("crm.education.request.approved.literature.recipients", "").split(",");
            deliveryNotificationEnabled = properties.getProperty("delivery.notification.enabled", Boolean.class, false);
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

        public String getDeliveryUrl() {
            return deliveryUrl;
        }

        public String getCrmDocumentPreviewUrl() {
            return crmDocumentPreviewUrl;
        }

        public String getCrmEmployeeRegistrationUrl() {
            return crmEmployeeRegistrationUrl;
        }

        public String getCrmReservedIpsUrl() {
            return crmReservedIpsUrl;
        }

        public String[] getCrmEmployeeRegistrationNotificationsRecipients() {
            return crmEmployeeRegistrationNotificationsRecipients;
        }

        public String[] getCrmEmployeeRegistrationCommentNotificationsRecipients() {
            return crmEmployeeRegistrationCommentNotificationsRecipients;
        }

        public String[] getCrmRoomReservationNotificationsRecipients() {
            return crmRoomReservationNotificationsRecipients;
        }

        public String[] getCrmIpReservationNotificationsRecipients() {
            return crmIpReservationNotificationsRecipients;
        }

        public String[] getCrmBirthdaysNotificationsRecipients() { return crmBirthdaysNotificationsRecipients;
        }

        public String[] getCrmEducationRequestCourseRecipients() {
            return crmEducationRequestCourseRecipients;
        }

        public String[] getCrmEducationRequestConferenceRecipients() {
            return crmEducationRequestConferenceRecipients;
        }

        public String[] getCrmEducationRequestLiteratureRecipients() {
            return crmEducationRequestLiteratureRecipients;
        }

        public String[] getCrmEducationRequestApprovedCourseRecipients() {
            return crmEducationRequestApprovedCourseRecipients;
        }

        public String[] getCrmEducationRequestApprovedConferenceRecipients() {
            return crmEducationRequestApprovedConferenceRecipients;
        }

        public String[] getCrmEducationRequestApprovedLiteratureRecipients() {
            return crmEducationRequestApprovedLiteratureRecipients;
        }

        public boolean isDeliveryNotificationEnabled() {
            return deliveryNotificationEnabled;
        }
    }

    public static class SmtpConfig {
        private final String host;
        private final String defaultCharset;
        private final int port;
        private final String fromAddressCrm;
        private final String fromAddressPortal;
        private final String fromAddressAbsence;
        private final String fromAddressReport;
        private final String fromAddressCrmAlias;
        private final String fromAddressPortalAlias;
        private final String fromAddressAbsenceAlias;
        private final String fromAddressReportAlias;
        private final boolean blockExternalRecipients;
        private final String messageIdPattern;

        public SmtpConfig(PropertiesWrapper properties) throws ConfigException{
            host = properties.getProperty("smtp.host", "smtp.protei.ru");
            port = properties.getProperty("smtp.port", Integer.class, 2525);
            fromAddressCrm = properties.getProperty("smtp.from.crm", "CRM");
            fromAddressPortal = properties.getProperty("smtp.from.portal", "PORTAL");
            fromAddressAbsence = properties.getProperty("smtp.from.absence", "ABSENCE");
            fromAddressReport = properties.getProperty("smtp.from.report", "REPORT");
            fromAddressCrmAlias = properties.getProperty("smtp.from.crm.alias", "CRM");
            fromAddressPortalAlias = properties.getProperty("smtp.from.portal.alias", "DO_NOT_REPLY");
            fromAddressAbsenceAlias = properties.getProperty("smtp.from.absence.alias", "DO_NOT_REPLY");
            fromAddressReportAlias = properties.getProperty("smtp.from.report.alias", "DO_NOT_REPLY");
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

        public String getFromAddressCrm() {
            return fromAddressCrm;
        }

        public String getFromAddressPortal() {
            return fromAddressPortal;
        }

        public String getFromAddressAbsence() {
            return fromAddressAbsence;
        }

        public String getFromAddressReport() {
            return fromAddressReport;
        }

        public String getMessageIdPattern() {
            return messageIdPattern;
        }

        public String getFromAddressCrmAlias() {
            return fromAddressCrmAlias;
        }

        public String getFromAddressPortalAlias() {
            return fromAddressPortalAlias;
        }

        public String getFromAddressAbsenceAlias() {
            return fromAddressAbsenceAlias;
        }

        public String getFromAddressReportAlias() {
            return fromAddressReportAlias;
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
        private final String instanceId;

        public LegacySystemConfig(PropertiesWrapper properties) throws ConfigException {
            this.jdbcDriver = properties.getProperty("syb.jdbc.driver", "net.sourceforge.jtds.jdbc.Driver");
            this.jdbcURL = properties.getProperty("syb.jdbc.url", "jdbc:sybase:Tds:192.168.101.140:2642/RESV3");
            this.login = properties.getProperty("syb.jdbc.login", "dba");
            this.passwd = properties.getProperty("syb.jdbc.pwd", "sql");

            this.exportEnabled = properties.getProperty("syb.export.enabled", Boolean.class,false);

            try {
                this.instanceId = properties.getProperty("syb.export.identity", Inet4Address.getLocalHost().getHostAddress());
            }
            catch (Exception e) {
                logger.error("unable to get local ip address", e);
                throw new ConfigException(e);
            }

            logger.info("legacy config, driver={}, url={}, export={}", jdbcDriver, jdbcURL, exportEnabled);
        }

        public String getJdbcDriver() {
            return jdbcDriver;
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
        private final boolean youtrackBackchannelEnabled;
        private final boolean youtrackCompanySyncEnabled;
        private final boolean youtrackEmployeeSyncEnabled;
        private final boolean youtrackLinksMigrationEnabled;
        private final boolean youtrackProjectLinksMigrationEnabled;
        private final boolean jiraEnabled;
        private final boolean jiraBackchannelEnabled;

        private final boolean redminePatchEnabled;

        public IntegrationConfig(PropertiesWrapper properties) throws ConfigException {
            redmineEnabled = properties.getProperty("integration.redmine", Boolean.class, false);
            redmineBackchannelEnabled = properties.getProperty("integration.redmine.backchannel", Boolean.class, false);
            youtrackEnabled = properties.getProperty("integration.youtrack", Boolean.class, false);
            youtrackBackchannelEnabled = properties.getProperty("integration.youtrack.backchannel", Boolean.class, false);
            youtrackCompanySyncEnabled = properties.getProperty("integration.youtrack.companies", Boolean.class, false);
            youtrackEmployeeSyncEnabled = properties.getProperty("integration.youtrack.employees", Boolean.class, false);
            youtrackLinksMigrationEnabled = properties.getProperty("migration.youtrack.links", Boolean.class, false);
            youtrackProjectLinksMigrationEnabled = properties.getProperty("project.migration.youtrack.links", Boolean.class, false);
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

        public boolean isYoutrackBackchannelEnabled() {
            return youtrackBackchannelEnabled;
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

        public boolean isYoutrackProjectLinksMigrationEnabled() {
            return youtrackProjectLinksMigrationEnabled;
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
        private final int projectLimitCommentsNumber;

        public ReportConfig(PropertiesWrapper properties) throws ConfigException {
            try {
                this.threadsNumber = properties.getProperty("report.threads", Integer.class, 6);
                this.chunkSize = properties.getProperty("report.chunk.size", Integer.class, 20);
                this.liveTime = DurationUtils.getDuration(properties.getProperty("report.live_time_duration", "3d"), TimeUnit.MILLISECONDS);
                this.hangInterval = TimeUnit.SECONDS.toMillis(properties.getProperty("report.hang_interval_sec", Integer.class, 30 * 60));
                this.storagePath = properties.getProperty("report.storage.path", "reports");
                this.projectLimitCommentsNumber = properties.getProperty("report.project.limit_comments_number", Integer.class, 30);
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

        public int getProjectLimitCommentsNumber() {
            return projectLimitCommentsNumber;
        }
    }

    public static class CaseLinkConfig {
        private final String linkCrm;
        private final String linkYouTrack;
        private final String linkUits;
        private final String crossCrmLinkYoutrack;
        private final String crossProjectLinkYoutrack;

        public CaseLinkConfig(PropertiesWrapper properties) throws ConfigException {
            this.linkCrm = properties.getProperty("case.link.internal", "http://newportal/crm/#issues/issue_preview:id=%id%");
            this.linkYouTrack = properties.getProperty("case.link.youtrack", "https://youtrack.protei.ru/issue/%id%");
            this.linkUits = properties.getProperty("case.link.uits", "https://support.uits.spb.ru/crm/deal/details/%id%/");
            this.crossCrmLinkYoutrack = properties.getProperty("case.crm.crosslink.youtrack", "http://newportal/crm/#issues/issue:id=%id%");
            this.crossProjectLinkYoutrack = properties.getProperty("case.project.crosslink.youtrack", "http://newportal/crm/#project_preview:id=%id%");
        }

        public String getLinkCrm() {
            return linkCrm;
        }

        public String getLinkYouTrack() {
            return linkYouTrack;
        }

        public String getLinkUits() {
            return linkUits;
        }

        public String getCrossCrmLinkYoutrack() {
            return crossCrmLinkYoutrack;
        }

        public String getCrossProjectLinkYoutrack() {
            return crossProjectLinkYoutrack;
        }
    }

    public static class UitsConfig {
        private final String apiUrl;

        public UitsConfig(PropertiesWrapper properties) {
            apiUrl = properties.getProperty("uits.api.url");
        }

        public String getApiUrl() {
            return apiUrl;
        }
    }

    public static class YoutrackConfig {
        private final String apiBaseUrl;
        private final String login;
        private final String authToken;
        private final String employeeRegistrationSyncSchedule;
        private final String equipmentProject;
        private final String supportProject;
        private final String phoneProject;
        private final Long youtrackUserId;
        private final String youtrackCustomFieldCompanyId;

        public YoutrackConfig(PropertiesWrapper properties) {
            apiBaseUrl = properties.getProperty("youtrack.api.baseurl");
            login = properties.getProperty("youtrack.api.login", "portal");
            authToken = properties.getProperty("youtrack.api.auth_token");
            employeeRegistrationSyncSchedule = properties.getProperty("youtrack.employee_registration.sync_schedule", "0 */15 * * * *");
            equipmentProject = properties.getProperty("youtrack.employee_registration.equipment_project");
            supportProject = properties.getProperty("youtrack.employee_registration.support_project");
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

        public String getSupportProject() {
            return supportProject;
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

    public static class Enterprise1CConfig {
        private final String apiBaseProteiUrl;
        private final String apiBaseProteiStUrl;
        private final String apiBaseProteiStZiupCopyUrl;
        private final String login;
        private final String password;
        private final String parentKeyST;
        private final String parentKeyResident;
        private final String parentKeyNotResident;
        private final boolean contractSyncEnabled;
        private final String workLogin;
        private final String workPassword;
        private final String workProteiUrl;
        private final String workProteiStUrl;

        public Enterprise1CConfig(PropertiesWrapper properties) {
            apiBaseProteiUrl = properties.getProperty("enterprise1c.api.base_protei_url");
            apiBaseProteiStUrl = properties.getProperty("enterprise1c.api.base_protei_st_url");
            apiBaseProteiStZiupCopyUrl = properties.getProperty("enterprise1c.api.base_protei_ziup_copy_url");
            login = properties.getProperty("enterprise1c.api.login");
            password = properties.getProperty("enterprise1c.api.password");
            parentKeyST = properties.getProperty("enterprise1c.api.parent_key_st");
            parentKeyResident = properties.getProperty("enterprise1c.api.parent_key_resident");
            parentKeyNotResident = properties.getProperty("enterprise1c.api.parent_key_not_resident");
            contractSyncEnabled = properties.getProperty("enterprise1c.api.contract.sync.enabled", Boolean.class, false);
            workLogin = properties.getProperty("enterprise1c.api.work.login");
            workPassword = properties.getProperty("enterprise1c.api.work.password");
            workProteiUrl = properties.getProperty("enterprise1c.api.work.protei_url");
            workProteiStUrl = properties.getProperty("enterprise1c.api.work.protei_st_url");
        }

        public String getApiBaseProteiUrl() {
            return apiBaseProteiUrl;
        }

        public String getApiBaseProteiStUrl() {
            return apiBaseProteiStUrl;
        }

        public String getApiBaseProteiStZiupCopyUrl() {
            return apiBaseProteiStZiupCopyUrl;
        }

        public String getLogin() {
            return login;
        }

        public String getPassword() {
            return password;
        }

        public String getParentKeyST() {
            return parentKeyST;
        }

        public String getParentKeyResident() {
            return parentKeyResident;
        }

        public String getParentKeyNotResident() {
            return parentKeyNotResident;
        }

        public boolean isContractSyncEnabled() { return contractSyncEnabled; }

        public String getWorkLogin() {
            return workLogin;
        }

        public String getWorkPassword() {
            return workPassword;
        }

        public String getWorkProteiUrl() {
            return workProteiUrl;
        }

        public String getWorkProteiStUrl() {
            return workProteiStUrl;
        }
    }

    public static class JiraConfig {

        private final String jiraUrl;
        private final int queueLimit;
        private final List<String> jiraProjects;

        public JiraConfig(PropertiesWrapper properties) throws ConfigException {
            jiraUrl = properties.getProperty("jira.url",  "");
            queueLimit = properties.getProperty("integration.jira.queue.limit", Integer.class, 0);
            String temp = properties.getProperty("jira.projects", "");
            if (isNotEmpty(temp)) {
                jiraProjects = Arrays.stream(temp.split(",")).collect(Collectors.toList());
            } else {
                jiraProjects = new ArrayList<>();
            }
        }

        public String getJiraUrl() {
            return jiraUrl;
        }

        public int getQueueLimit() {
            return queueLimit;
        }

        public List<String> getJiraProjects() {
            return jiraProjects;
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

    public static class UiConfig {
        private final Long issueAssignmentDeskLimit;

        public UiConfig(PropertiesWrapper properties) {
            issueAssignmentDeskLimit = properties.getProperty("ui.issue-assignment.desk.limit", Long.class, 200L);
        }

        public Long getIssueAssignmentDeskLimit() {
            return issueAssignmentDeskLimit;
        }
    }

    public static class MailCommentConfig {
        final String user;
        final String pass;
        final String host;

        final boolean enable;
        final List<String> blackList;
        final boolean enableForwardMail;
        final String forwardMail;

        public MailCommentConfig(PropertiesWrapper properties) {
            user = properties.getProperty("imap.user", "crm@protei.ru");
            pass = properties.getProperty("imap.pass");
            host = properties.getProperty("imap.host", "imap.protei.ru");

            enable = properties.getProperty("mail.comment.enable", Boolean.class, false);
            String temp = properties.getProperty("mail.comment.subject.black.list", "");
            if (isNotEmpty(temp)) {
                blackList = Arrays.stream(temp.split(",")).collect(Collectors.toList());
            } else {
                blackList = new ArrayList<>();
            }
            enableForwardMail = properties.getProperty("mail.comment.forward.enable", Boolean.class, false);
            forwardMail = properties.getProperty("mail.comment.forward.email", "support@protei.ru");
        }

        public String getUser() {
            return user;
        }

        public String getPass() {
            return pass;
        }

        public String getHost() {
            return host;
        }

        public boolean isEnable() {
            return enable;
        }

        public List<String> getBlackList() {
            return blackList;
        }

        public String getForwardMail() {
            return forwardMail;
        }

        public boolean isEnableForwardMail() {
            return enableForwardMail;
        }
    }

    public static class NRPEConfig {
        final String template;
        final Boolean enable;
        final List<String> adminMails;

        public NRPEConfig(PropertiesWrapper properties) {
            this.template = properties.getProperty("nrpe.template",
                    "/usr/lib64/nagios/plugins/check_nrpe -H router.protei.ru -c check_arping_lan -a %s ; echo $?");
            this.enable = properties.getProperty("nrpe.enable", Boolean.class, false);
            String temp = properties.getProperty("nrpe.admin.mails");
            if (isNotEmpty(temp)) {
                adminMails = Arrays.stream(temp.split(",")).collect(Collectors.toList());
            } else {
                adminMails = new ArrayList<>();
            }
        }

        public String getTemplate() {
            return template;
        }

        public Boolean getEnable() {
            return enable;
        }

        public List<String> getAdminMails() {
            return adminMails;
        }
    }

    public static class AutoOpenConfig {
        final Boolean enable;
        final Boolean enableDelay;

        public AutoOpenConfig(PropertiesWrapper properties) {
            this.enable = properties.getProperty("autoopen.enable", Boolean.class, false);
            this.enableDelay = properties.getProperty("autoopen.delay.enable", Boolean.class, true);
        }

        public Boolean getEnable() {
            return enable;
        }

        public Boolean getEnableDelay() {
            return enableDelay;
        }
    }

    private final static Long DEFAULT_FILE_SIZE_MEGABYTES = 10L;
}
