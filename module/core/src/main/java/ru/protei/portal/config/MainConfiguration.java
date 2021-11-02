package ru.protei.portal.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.aspect.ServiceLayerInterceptor;
import ru.protei.portal.core.aspect.ServiceLayerInterceptorLogging;
import ru.protei.portal.core.client.enterprise1c.api.Api1C;
import ru.protei.portal.core.client.enterprise1c.api.Api1CImpl;
import ru.protei.portal.core.client.enterprise1c.api.Api1CWork;
import ru.protei.portal.core.client.enterprise1c.api.Api1CWorkImpl;
import ru.protei.portal.core.client.enterprise1c.http.HttpClient1C;
import ru.protei.portal.core.client.enterprise1c.http.HttpClient1CImpl;
import ru.protei.portal.core.client.enterprise1c.http.HttpClient1CWork;
import ru.protei.portal.core.client.enterprise1c.http.HttpClient1CWorkImpl;
import ru.protei.portal.core.client.enterprise1c.mapper.FieldsMapper1C;
import ru.protei.portal.core.client.enterprise1c.mapper.FieldsMapper1CImpl;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.client.youtrack.api.YoutrackApiImpl;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClientImpl;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapperImpl;
import ru.protei.portal.core.index.document.DocumentStorageIndex;
import ru.protei.portal.core.index.document.DocumentStorageIndexImpl;
import ru.protei.portal.core.model.converter.MoneyJdbcConverter;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.portal.core.renderer.JiraWikiMarkupRenderer;
import ru.protei.portal.core.renderer.MarkdownRenderer;
import ru.protei.portal.core.renderer.impl.HTMLRendererImpl;
import ru.protei.portal.core.renderer.impl.JiraWikiMarkupRendererImpl;
import ru.protei.portal.core.renderer.impl.MarkdownRendererImpl;
import ru.protei.portal.core.report.absence.ReportAbsence;
import ru.protei.portal.core.report.absence.ReportAbsenceImpl;
import ru.protei.portal.core.report.caseobjects.ReportCase;
import ru.protei.portal.core.report.caseobjects.ReportCaseImpl;
import ru.protei.portal.core.report.casetimeelapsed.ReportCaseTimeElapsed;
import ru.protei.portal.core.report.casetimeelapsed.ReportCaseTimeElapsedImpl;
import ru.protei.portal.core.report.contract.ReportContract;
import ru.protei.portal.core.report.contract.ReportContractImpl;
import ru.protei.portal.core.report.dutylog.ReportDutyLog;
import ru.protei.portal.core.report.dutylog.ReportDutyLogImpl;
import ru.protei.portal.core.report.nightwork.ReportNightWork;
import ru.protei.portal.core.report.nightwork.ReportNightWorkImpl;
import ru.protei.portal.core.report.projects.ReportProject;
import ru.protei.portal.core.report.projects.ReportProjectImpl;
import ru.protei.portal.core.report.ytwork.ReportYoutrackWork;
import ru.protei.portal.core.report.ytwork.ReportYoutrackWorkImpl;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.auth.AuthServiceImpl;
import ru.protei.portal.core.service.auth.LDAPAuthProvider;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseService;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseServiceImpl;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseServiceTaskHandlerImpl;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseTaskHandler;
import ru.protei.portal.core.service.bootstrap.BootstrapService;
import ru.protei.portal.core.service.bootstrap.BootstrapServiceImpl;
import ru.protei.portal.core.service.events.*;
import ru.protei.portal.core.service.nrpe.NRPEService;
import ru.protei.portal.core.service.nrpe.NRPEServiceImpl;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.policy.PolicyServiceImpl;
import ru.protei.portal.core.service.syncronization.EmployeeRegistrationYoutrackSynchronizer;
import ru.protei.portal.core.service.syncronization.EmployeeRegistrationYoutrackSynchronizerImpl;
import ru.protei.portal.core.service.template.TemplateService;
import ru.protei.portal.core.service.template.TemplateServiceImpl;
import ru.protei.portal.core.svn.document.DocumentSvnApi;
import ru.protei.portal.core.svn.document.DocumentSvnApiImpl;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.portal.core.utils.SimpleSidGenerator;
import ru.protei.portal.nrpe.NRPEExecutorTerminal;
import ru.protei.portal.nrpe.NRPEProcessor;
import ru.protei.portal.schedule.PortalScheduleTasks;
import ru.protei.portal.schedule.PortalScheduleTasksImpl;
import ru.protei.portal.tools.migrate.export.ActiveExportDataService;
import ru.protei.portal.tools.migrate.export.DummyExportDataService;
import ru.protei.portal.tools.migrate.export.ExportDataService;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.portal.tools.migrate.sybase.SybConnProvider;
import ru.protei.portal.tools.migrate.sybase.SybConnWrapperImpl;
import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.impl.LockServiceImpl;
import ru.protei.winter.jdbc.JdbcObjectMapperRegistrator;
import ru.protei.winter.jdbc.config.JdbcConfig;
import ru.protei.winter.jdbc.config.JdbcConfigData;

import javax.annotation.PostConstruct;
import javax.inject.Inject;
import java.util.concurrent.Executor;

@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableScheduling
@EnableAsync
@PropertySource("classpath:spring.properties")
public class MainConfiguration {

    @Autowired
    JdbcConfig jdbcConfig;
    @Inject
    private JdbcObjectMapperRegistrator objectMapperRegistrator;

    @PostConstruct
    private void init() {
        objectMapperRegistrator.registerMapper(CaseInfo.class);
    }

    /**
     * Config
     * @return
     */
    @Bean
    public PortalConfig getPortalConfig() throws ConfigException {
        return new PortalConfigReloadable("portal.properties");
    }

    /**
     * Запуск фоновых задач
     */
    @Bean(name = BACKGROUND_TASKS)
    public Executor threadPoolTaskExecutor() {
        JdbcConfigData.JdbcConnectionParam connectionParam = CollectionUtils.getFirst( jdbcConfig.data().getConnections() );
        int maxDbConnectionPoolSize = 50; //взять из winter.properties
        if (connectionParam != null)
            maxDbConnectionPoolSize = connectionParam.getMaxPoolSize();
        return new BackgroundTaskThreadPoolTaskExecutor( maxDbConnectionPoolSize );
    }

    @Bean(name = REPORT_TASKS)
    public Executor getReportThreadPoolTaskExecutor(@Autowired PortalConfig config) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.data().reportConfig().getThreadsNumber());
        executor.setMaxPoolSize(config.data().reportConfig().getThreadsNumber());
        return executor;
    }

    @Bean()
    public FileStorage getFileStorage(@Autowired PortalConfig config) {
        PortalConfigData.CloudConfig cloud = config.data().cloud();
        return new FileStorage(cloud.getStoragePath(), cloud.getUser(), cloud.getPassword());
    }

    @Bean
    public DocumentStorageIndex getDocumentStorage() {
        return new DocumentStorageIndexImpl();
    }

    @Bean
    public Lang lang() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("Lang");
        messageSource.setDefaultEncoding("UTF-8");
        return new Lang(messageSource);
    }

    @Bean
    public SybConnProvider getSybConnProvider(@Autowired PortalConfig config) throws Throwable {
        return new SybConnWrapperImpl(
                config.data().legacySysConfig().getJdbcDriver(),
                config.data().legacySysConfig().getJdbcURL(),
                config.data().legacySysConfig().getLogin(),
                config.data().legacySysConfig().getPasswd()
        );
    }


    @Bean(name = "portalScheduler")
    public PortalScheduleTasks getPortalScheduleTasks() {
        return new PortalScheduleTasksImpl();
    }

    @Bean
    public LegacySystemDAO getLegacySystemDAO() {
        return new LegacySystemDAO();
    }

    @Bean
    public LDAPAuthProvider getLDAPAuthProvider() {
        return new LDAPAuthProvider();
    }

    /* DAO SQL builders */

    @Bean
    public CaseObjectSqlBuilder sqlDefaultBuilder() {
        return new CaseObjectSqlBuilder();
    }

    @Bean
    public CaseCommentSqlBuilder getCaseCommentSqlBuilder() {
        return new CaseCommentSqlBuilder();
    }

    @Bean
    public ServerSqlBuilder serverSqlBuilder() {
        return new ServerSqlBuilder();
    }

    @Bean
    public EmployeeSqlBuilder employeeSqlBuilder() {
        return new EmployeeSqlBuilder();
    }

    @Bean
    public PersonSqlBuilder getPersonSqlBuilder() {
        return new PersonSqlBuilder();
    }

    @Bean
    public ContactSqlBuilder getContactSqlBuilder() {
        return new ContactSqlBuilder();
    }

    /* DAO converters */

    @Bean
    public MoneyJdbcConverter moneyJdbcConverter() {
        return new MoneyJdbcConverter();
    }

    /* DAO */

    @Bean
    public MigrationEntryDAO getMigrationEntryDAO() {
        return new MigrationEntryDAO_Impl();
    }

    @Bean
    public CompanyGroupHomeDAO getCompanyGroupHomeDAO() {
        return new CompanyGroupHomeDAO_Impl();
    }

    @Bean
    public PersonAbsenceDAO getPersonAbsenceDAO() {
        return new PersonAbsenceDAO_Impl();
    }

    @Bean
    public CompanyDAO getCompanyDAO() {
        return new CompanyDAO_Impl();
    }

    @Bean
    public PersonDAO getPersonDAO() {
        return new PersonDAO_Impl();
    }

    @Bean
    public PersonShortViewDAO gePersonShortViewDAO() {
        return new PersonShortViewDAOImpl();
    }

    @Bean
    public DevUnitDAO getDevUnitDAO() {
        return new DevUnitDAO_Impl();
    }

    @Bean
    public CaseCommentDAO getCaseCommentDAO() {
        return new CaseCommentDAO_Impl();
    }

    @Bean
    public CaseCommentShortViewDAO getCaseCommentShortViewDAODAO() {
        return new CaseCommentShortViewDAO_Impl();
    }

    @Bean
    public CaseStateMatrixDAO getStateMatrixDAO() {
        return new CaseStateMatrixDAO_Impl();
    }

    @Bean
    public CaseStateDAO getStateDAO() {
        return new CaseStateDAO_Impl();
    }

    @Bean
    public CaseObjectDAO getCaseDAO() {
        return new CaseObjectDAO_Impl();
    }

    @Bean
    public BootstrapAppDAO getBootstrapAppDAO() {
        return new BootstrapAppDAO_Impl();
    }

    @Bean
    public CaseObjectMetaDAO getCaseMetaDAO() {
        return new CaseObjectMetaDAO_Impl();
    }

    @Bean
    public CaseObjectMetaNotifiersDAO getCaseMetaNotifiersDAO() {
        return new CaseObjectMetaNotifiersDAO_Impl();
    }

    @Bean
    public CaseShortViewDAO getCaseShortDAO() {
        return new CaseShortViewDAO_Impl();
    }

    @Bean
    public AuditObjectDAO getAuditDAO() {
        return new AuditObjectDAO_Impl();
    }

    @Bean
    public UserRoleDAO getUserRoleDAO() {
        return new UserRoleDAO_impl();
    }

    @Bean
    public UserLoginDAO getUserLoginDAO() {
        return new UserLoginDAO_Impl();
    }

    @Bean
    public UserLoginShortViewDAO getUserLoginShortViewDAO() {
        return new UserLoginShortViewDAO_Impl();
    }

    @Bean
    public UserCaseAssignmentDAO getUserCaseAssignmentDAO() {
        return new UserCaseAssignmentDAO_Impl();
    }

    @Bean
    public UserDashboardDAO getUserDashboardDAO() {
        return new UserDashboardDAO_Impl();
    }

    @Bean
    public CompanyDepartmentDAO getCompanyDepartmentDAO() {
        return new CompanyDepartmentDAO_Impl();
    }

    @Bean
    public WorkerPositionDAO getWorkerPositionDAO() {
        return new WorkerPositionDAO_Impl();
    }

    @Bean
    public WorkerEntryDAO getWorkerEntryDAO() {
        return new WorkerEntryDAO_Impl();
    }

    @Bean
    public CompanyGroupDAO getCompanyGroupDAO() {
        return new CompanyGroupDAO_Impl();
    }

    @Bean
    public CompanyGroupItemDAO getCompanyGroupItemDAO() {
        return new CompanyGroupItemDAO_Impl();
    }

    @Bean
    public CompanyImportanceItemDAO getCompanyImportanceItemDAO() {
        return new CompanyImportanceItemDAO_Impl();
    }

    @Bean
    public ImportanceLevelDAO getImportanceLevelDAO() {
        return new ImportanceLevelDAO_Impl();
    }

    @Bean
    public PersonCompanyEntryDAO getPersonCompanyEntryDAO() {
        return new PersonCompanyEntryDAO_Impl();
    }

    @Bean
    public CaseAttachmentDAO getCaseAttachmentDAO() {
        return new CaseAttachmentDAO_Impl();
    }

    @Bean
    public CaseNotifierDAO getCaseNotifierDAO() {
        return new CaseNotifierDAO_Impl();
    }

    @Bean
    public AttachmentDAO getAttachmentDAO() {
        return new AttachmentDAO_Impl();
    }

    @Bean
    public EquipmentDAO getEquipmentDAO() {
        return new EquipmentDAO_Impl();
    }

    @Bean
    public LocationDAO getLocationDAO() {
        return new LocationDAO_Impl();
    }

    @Bean
    public CaseMemberDAO getCaseMemberDAO() {
        return new CaseMemberDAO_Impl();
    }

    @Bean
    public CaseLocationDAO getCaseLocationDAO() {
        return new CaseLocationDAO_Impl();
    }

    @Bean
    public CaseTypeDAO getCaseTypeDAO() {
        return new CaseTypeDAO_Impl();
    }

    @Bean
    public DecimalNumberDAO getDecimalNumberDAO() {
        return new DecimalNumberDAO_Impl();
    }

    @Bean
    public CompanySubscriptionDAO getCompanySubscriptionDAO() {
        return new CompanySubscriptionDAO_Impl();
    }

    @Bean
    public ExternalCaseAppDAO getExternalCaseAppDAO() {
        return new ExternalCaseAppDAO_Impl();
    }

    @Bean
    public ExportSybEntryDAO getExportSybEntryDAO() {
        return new ExportSybEntryDAO_Impl();
    }

    @Bean
    public DocumentDAO getDocumentDAO() {
        return new DocumentDAO_Impl();
    }

    @Bean
    public DocumentTypeDAO getDocumentTypeDAO() {
        return new DocumentTypeDAO_Impl();
    }

    @Bean
    public RedmineEndpointDAO getRedmineEndpointDAO() {
        return new RedmineEndpointDAO_Impl();
    }

    @Bean
    public RedmineStatusMapEntryDAO getRedmineStatusMapEntryDAO() {
        return new RedmineStatusMapEntryDAO_Impl();
    }

    @Bean
    public RedmineToCrmStatusMapEntryDAO getRedmineToCrmStatusMapEntryDAO() {
        return new RedmineToCrmStatusMapEntryDAO_Impl();
    }

    @Bean
    public JiraPriorityMapEntryDAO getJiraPriorityMapEntryDAO() {
        return new JiraPriorityMapEntryDAO_Impl();
    }

    @Bean
    public JiraEndpointDAO getJiraEndpointDAO() {
        return new JiraEnpointDAO_Impl();
    }

    @Bean
    public JiraStatusMapEntryDAO getJiraStatusMapEntryDAO() {
        return new JiraStatusMapEntryDAO_Impl();
    }

    @Bean
    public JiraSLAMapEntryDAO getJiraSLAMapEntryDAO() {
        return new JiraSLAMapEntryDAO_Impl();
    }

    @Bean
    public RedminePriorityMapEntryDAO getRedminePriorityMapEntryDAO() {
        return new RedminePriorityMapEntryDAO_Impl();
    }

    @Bean
    public CaseFilterDAO getIssueFilterDAO() {
        return new CaseFilterDAO_Impl();
    }

    @Bean
    public ProductSubscriptionDAO getProductSubscriptionDAO() {
        return new ProductSubscriptionDAO_Impl();
    }

    @Bean
    public ReportDAO getReportDAO() {
        return new ReportDAO_Impl();
    }

    @Bean
    public CaseLinkDAO getCaseLinkDAO() {
        return new CaseLinkDAO_Impl();
    }

    @Bean
    public PlanDAO getPlanDAO() {
        return new PlanDAO_Impl();
    }

    @Bean
    public PlanToCaseObjectDAO getPlanToCaseObjectDAO() {
        return new PlanToCaseObjectDAO_Impl();
    }

    @Bean
    public HistoryDAO getHistoryDAO() {
        return new HistoryDAO_Impl();
    }

    @Bean
    public EmployeeRegistrationHistoryDAO getEmployeeRegistrationHistoryDAO() {
        return new EmployeeRegistrationHistoryDAO_Impl();
    }

    @Bean
    public ProjectToProductDAO getProjectToProductDAO() {
        return new ProjectToProductDAO_Impl();
    }

    @Bean
    public PlatformDAO getPlatformDAO() {
        return new PlatformDAO_Impl();
    }

    @Bean
    public ServerDAO getServerDAO() {
        return new ServerDAO_Impl();
    }

    @Bean
    public ServerGroupDAO getServerGroupDAO() {
        return new ServerGroupDAO_Impl();
    }

    @Bean
    public ApplicationDAO getApplicationDAO() {
        return new ApplicationDAO_Impl();
    }

    @Bean
    public ServerApplicationDAO getServerApplicationDAO() {
        return new ServerApplicationDAO_Impl();
    }

    @Bean
    public DevUnitChildRefDAO getDevUnitChildRefDAO() {
        return new DevUnitChildRefDAO_Impl();
    }

    @Bean
    public EmployeeShortViewDAO getEmployeeShortViewDAO() {
        return new EmployeeShortViewDAO_Impl();
    }

    @Bean
    public EmployeeRegistrationDAO getEmployeeRegistrationDAO() {
        return new EmployeeRegistrationDAO_Impl();
    }

    @Bean
    public CaseCommentTimeElapsedSumDAO getCaseCommentCaseObjectDAO() {
        return new CaseCommentTimeElapsedSumDAO_Impl();
    }

    @Bean
    public ContractDAO getContractDAO() {
        return new ContractDAO_Impl();
    }

    @Bean
    public ProjectSlaDAO getProjectSlaDAO() {
        return new ProjectSlaDAO_Impl();
    }

    @Bean
    public ProjectDAO getProjectDAO() {
        return new ProjectDAO_Impl();
    }

    @Bean
    public ProjectTechnicalSupportValidityReportInfoDAO getProjectTechnicalSupportValidityReportInfoDAO() {
        return new ProjectTechnicalSupportValidityReportInfoDAO_Impl();
    }

    @Bean
    public ContractDateDAO getContractDateDAO() {
        return new ContractDateDAO_Impl();
    }

    @Bean
    public CaseTagDAO getCaseTagDAO() {
        return new CaseTagDAO_Impl();
    }

    @Bean
    public CaseObjectTagDAO getCaseObjectTagDAO() {
        return new CaseObjectTagDAO_Impl();
    }

    @Bean
    public CaseStateWorkflowDAO getCaseStateWorkflowDAO() {
        return new CaseStateWorkflowDAO_Impl();
    }

    @Bean
    public WorkerEntryShortViewDAO getWorkerEntryShortViewDAO() {
        return  new WorkerEntryShortViewDAO_Impl();
    }

    @Bean
    public ContactItemDAO getContactItemDAO() {
        return new ContactItemDAO_Impl();
    }

    @Bean
    public DeliveryDAO getDeliveryDAO() {
        return new DeliveryDAO_Impl();
    }

    @Bean
    public DeliverySqlBuilder deliverySqlBuilder() {
        return new DeliverySqlBuilder();
    }

    @Bean
    public KitDAO getKitDAO() {
        return new KitDAO_Impl();
    }

    @Bean
    public ModuleDAO getModuleDAO() {
        return new ModuleDAO_Impl();
    }

    @Bean
    public CardDAO getCardDAO() {
        return new CardDAO_Impl();
    }

    @Bean
    public PcbOrderSqlBuilder pcbOrderSqlBuilder() {
        return new PcbOrderSqlBuilder();
    }

    @Bean
    public PcbOrderDAO getPcbOrderDAO() {
        return new PcbOrderDAO_Impl();
    }

    @Bean
    public CardBatchDAO getCardBatchDAO() {
        return new CardBatchDAO_Impl();
    }

    @Bean
    public CardSqlBuilder cardSqlBuilder() {
        return new CardSqlBuilder();
    }

    @Bean
    public CardBatchSqlBuilder cardBatchSqlBuilder() {
        return new CardBatchSqlBuilder();
    }

    @Bean
    public YoutrackHttpClient getYoutrackHttpClient() {
        return new YoutrackHttpClientImpl();
    }

    @Bean
    public YoutrackApi getYoutrackApi() {
        return new YoutrackApiImpl();
    }

    @Bean
    public YtDtoFieldsMapper getYtDtoFieldsMapper() {
        return new YtDtoFieldsMapperImpl();
    }

    @Bean
    public FieldsMapper1C getFieldsMapper1C() {
        return new FieldsMapper1CImpl();
    }

    @Bean
    public HttpClient1C getHttpClient1C() {
        return new HttpClient1CImpl();
    }

    @Bean
    public HttpClient1CWork getHttpClient1CWork() {
        return new HttpClient1CWorkImpl();
    }

    @Bean
    public Api1C getApi1C() {
        return new Api1CImpl();
    }

    @Bean
    public Api1CWork getApi1CWork() {
        return new Api1CWorkImpl();
    }

    @Bean
    public JiraCompanyGroupDAO getJiraCompanyGroupDAO() {
        return new JiraCompanyGroupDAO_Impl();
    }

    @Bean
    public EducationWalletDAO getEducationWalletDAO() {
        return new EducationWalletDAO_Impl();
    }

    @Bean
    public EducationEntryDAO getEducationEntryDAO() {
        return new EducationEntryDAO_Impl();
    }

    @Bean
    public EducationEntryAttendanceDAO getEducationEntryAttendanceDAO() {
        return new EducationEntryAttendanceDAO_Impl();
    }

    @Bean
    public SubnetDAO getSubnetDAO() {
        return new SubnetDAO_Impl();
    }

    @Bean
    public ReservedIpDAO getReservedIpDAO() {
        return new ReservedIpDAO_Impl();
    }

    @Bean
    public RoomReservableDAO getRoomReservableDAO() {
        return new RoomReservableDAO_Impl();
    }

    @Bean
    public RoomReservationDAO getRoomReservationDAO() {
        return new RoomReservationDAO_Impl();
    }

    @Bean
    public PersonCaseFilterDAO getPersonToCaseFilterDAO() {
        return new PersonCaseFilterDAO_Impl();
    }

    @Bean
    public ContractSpecificationDAO getContractSpecificationDAO() {
        return new ContractSpecificationDAO_Impl();
    }

    @Bean
    public ContractorDAO getContractorDAO() {
        return new ContractorDAO_Impl();
    }

    @Bean
    public PersonNotifierDAO getPersonNotifierDAO() {
        return new PersonNotifierDAO_Impl();
    }

    @Bean
    public AbsenceFilterDAO getAbsenceFilterDAO() {
        return new AbsenceFilterDAO_Impl();
    }

    @Bean
    public DutyLogFilterDAO getDutyLogFilterDAO() {
        return new DutyLogFilterDAO_Impl();
    }

    @Bean
    public PersonFavoriteIssuesDAO getPersonFavoritesIssuesDAO() {
        return new PersonFavoriteIssuesDAO_Impl();
    }

    @Bean
    public DutyLogDAO getDutyLogDAO() {
        return new DutyLogDAO_Impl();
    }

    @Bean
    public YoutrackProjectDAO getYoutrackProjectDAO() {
        return new YoutrackProjectDAO_Impl();
    }

    @Bean
    public YoutrackWorkDictionaryDAO getYoutrackWorkDictionaryDAO() {
        return new YoutrackWorkDictionaryDAO_Impl();
    }

    @Bean
    public CardTypeDAO getCardTypeDAO() {
        return new CardTypeDAO_Impl();
    }

    /* SERVICES */

    @Bean
    public SessionIdGen getSessionIdGenerator() {
        return new SimpleSidGenerator();
    }

    @Bean
    public AuthService getAuthService() {
        return new AuthServiceImpl();
    }

    @Bean
    public EmployeeService getEmployeeService() {
        return new EmployeeServiceImpl();
    }

    @Bean
    public CompanyService getCompanyService() {
        return new CompanyServiceImpl();
    }

    @Bean
    public CompanyDepartmentService getCompanyDepartmentService() {
        return new CompanyDepartmentServiceImpl();
    }

    @Bean
    public WorkerPositionService getWorkerPositionService() {
        return new WorkerPositionServiceImpl();
    }

    @Bean
    public ProductService getProductService() {
        return new ProductServiceImpl();
    }

    @Bean
    public ContactService getContactService() {
        return new ContactServiceImpl();
    }

    @Bean
    public CaseService getCaseService() {
        return new CaseServiceImpl();
    }

    @Bean
    public CaseStateService getCaseStateService() {
        return new CaseStateServiceImpl();
    }

    @Bean
    public AuditService getAuditService() {
        return new AuditServiceImpl();
    }

    @Bean
    public AttachmentService getAttachmentService() {
        return new AttachmentServiceImpl();
    }

    @Bean
    public LocationService getLocationService() {
        return new LocationServiceImpl();
    }

    @Bean
    public ProjectService getProjectService() {
        return new ProjectServiceImpl();
    }

    @Bean
    public EquipmentService getEquipmentService() {
        return new EquipmentServiceImpl();
    }

    @Bean
    public EventPublisherService getEventPublisherService() {
        return new AsyncEventPublisherService();
    }

    @Bean
    public CaseSubscriptionService getCaseSubscriptionService() {
        return new CaseSubscriptionServiceImpl();
    }

    @Bean
    public UserRoleService getUserRoleService() {
        return new UserRoleServiceImpl();
    }

    @Bean
    public AccountService getAccountService() {
        return new AccountServiceImpl();
    }

    @Bean
    public PersonService getPersonService() {
        return new PersonServiceImpl();
    }

    @Bean
    public TemplateService getTemplateService() {
        return new TemplateServiceImpl();
    }

    @Bean
    public PolicyService getPolicyService() {
        return new PolicyServiceImpl();
    }

    @Bean
    public BootstrapService getBootstrapService() {
        return new BootstrapServiceImpl();
    }

    @Bean
    public EventAssemblerService getEventAssemblerService() {
        return new EventAssemblerServiceImpl();
    }

    @Bean
    public EventProjectAssemblerService getEventProjectAssemblerService() {
        return new EventProjectAssemblerServiceImpl();
    }

    @Bean
    public EventDeliveryAssemblerService getEventDeliveryAssemblerService(@Autowired PortalConfig config) {
        if (config.data().getMailNotificationConfig().isDeliveryNotificationEnabled()) {
            return new EventDeliveryAssemblerServiceImpl();
        } else {
            log.info("delivery notification disabled in configuration");
            return new EventDeliveryAssemblerServiceMock();
        }
    }

    @Bean
    public AssemblerService getAssemblerService() {
        return new AssemblerServiceImpl();
    }

    @Bean
    public AssemblerProjectService getAssemblerProjectService() {
        return new AssemblerProjectServiceImpl();
    }

    @Bean
    public AssemblerDeliveryService getAssemblerDeliveryService() {
        return new AssemblerDeliveryServiceImpl();
    }

    @Bean
    public DocumentService getDocumentService() {
        return new DocumentServiceImpl();
    }

    @Bean
    public DocumentTypeService getDocumentTypeService() {
        return new DocumentTypeServiceImpl();
    }

    @Bean
    public CaseFilterService getIssueFilterService() {
        return new CaseFilterServiceImpl();
    }

    @Bean
    public JiraStatusService getJiraSlaService() {
        return new JiraStatusServiceImpl();
    }

    @Bean
    public ExportDataService getExportDataService(@Autowired PortalConfig config) {
        return config.data().legacySysConfig().isExportEnabled() ? new ActiveExportDataService() : new DummyExportDataService();
    }

    @Bean
    public LockService getLockService() {
        return new LockServiceImpl();
    }

    @Bean
    public ReportStorageService getReportStorageService() {
        return new ReportStorageServiceImpl();
    }

    @Bean
    public DocumentSvnApi getDocumentSvnApi() {
        return new DocumentSvnApiImpl();
    }

    @Bean
    public ReportService getReportService() {
        return new ReportServiceImpl();
    }

    @Bean
    public ReportControlService getReportControlService() {
        return new ReportControlServiceImpl();
    }

    @Bean
    public CaseLinkService getCaseLinkService() {
        return new CaseLinkServiceImpl();
    }

    @Bean
    public PlanService getPlanService() {
        return new PlanServiceImpl();
    }

    @Bean
    public HistoryService getHistoryService() {
        return new HistoryServiceImpl();
    }

    @Bean
    public SiteFolderService getSiteFolderService() {
        return new SiteFolderServiceImpl();
    }

    @Bean
    public YoutrackService getYoutrackService() {
        return new YoutrackServiceImpl();
    }

    @Bean
    public YoutrackWorkDictionaryService getYoutrackWorkDictionaryService() {
        return new YoutrackWorkDictionaryServiceImpl();
    }

    @Bean
    public UitsService getUitsService() {
        return new UitsServiceImpl();
    }

    @Bean
    public EmployeeRegistrationService getEmployeeRegistrationService() {
        return new EmployeeRegistrationServiceImpl();
    }

    @Bean
    public EmployeeRegistrationReminderService getEmployeeRegistrationReminderService() {
        return new EmployeeRegistrationReminderServiceImpl();
    }

    @Bean
    public CaseCommentService getCaseCommentService() {
        return new CaseCommentServiceImpl();
    }

    @Bean
    public ContractService getContractService() {
        return new ContractServiceImpl();
    }

    @Bean
    public ContractReminderService getContractReminderService() {
        return new ContractReminderServiceImpl();
    }

    @Bean
    public CaseTagService getCaseTagService() {
        return new CaseTagServiceImpl();
    }

    @Bean
    public IpReservationService getIpReservationService() {
        return new IpReservationServiceImpl();
    }

    @Bean
    public CaseStateWorkflowService getCaseStateWorkflowService() {
        return new CaseStateWorkflowServiceImpl();
    }

    @Bean
    public SLAService getSLAService() {
        return new SLAServiceImpl();
    }

    @Bean
    public UserDashboardService getUserDashboardService() {
        return new UserDashboardServiceImpl();
    }

    @Bean
    public UserCaseAssignmentService getUserCaseAssignmentService() {
        return new UserCaseAssignmentServiceImpl();
    }

    @Bean
    public EducationService getEducationService() {
        return new EducationServiceImpl();
    }

    @Bean
    public RoomReservationService getRoomReservationService() {
        return new RoomReservationServiceImpl();
    }

    @Bean
    public AutoOpenCaseService getAutoOpenCaseService() {
        return new AutoOpenCaseServiceImpl();
    }

    @Bean
    public AutoOpenCaseTaskHandler getAutoOpenCaseHandler() {
        return new AutoOpenCaseServiceTaskHandlerImpl();
    }

    @Bean
    public EmployeeRegistrationYoutrackSynchronizer getEmployeeRegistrationYoutrackSynchronizer() {
        return new EmployeeRegistrationYoutrackSynchronizerImpl();
    }

    @Bean
    public PersonCaseFilterService getPersonCaseFilterService() {
        return new PersonCaseFilterServiceImpl();
    }

    @Bean
    public AbsenceService getAbsenceService() {
        return new AbsenceServiceImpl();
    }

    @Bean
    public AbsenceFilterService getAbsenceFilterService() {
        return new AbsenceFilterServiceImpl();
    }

    @Bean
    public DutyLogFilterService getDutyLogFilterService() {
        return new DutyLogFilterServiceImpl();
    }


    @Bean
    public PersonSubscriptionService getPersonSubscriptionService() {
        return new PersonSubscriptionServiceImpl();
    }

    @Bean
    public MailReceiverService getMailReceiverService() {
        return new MailReceiverServiceImpl();
    }

    @Bean
    public DutyLogService getDutyLogService() {
        return new DutyLogServiceImpl();
    }

    @Bean
    public ReportCase getReportCase() {
        return new ReportCaseImpl();
    }

    @Bean
    public ReportProject getReportProject() {
        return new ReportProjectImpl();
    }

    @Bean
    public ReportCaseTimeElapsed getReportCaseTimeElapsed() {
        return new ReportCaseTimeElapsedImpl();
    }

    @Bean
    public ReportNightWork getReportNightWork() {
        return new ReportNightWorkImpl();
    }

    @Bean
    public ReportAbsence getReportAbsence() {
        return new ReportAbsenceImpl();
    }

    @Bean
    public ReportDutyLog getReportDutyLog() {
        return new ReportDutyLogImpl();
    }

    @Bean
    public ReportContract getReportContract() {
        return new ReportContractImpl();
    }

    @Bean
    public ReportYoutrackWork getReportYoutrackWork() {
        return new ReportYoutrackWorkImpl();
    }

    @Bean
    public HTMLRenderer getHTMLRenderer() {
        return new HTMLRendererImpl();
    }

    @Bean
    public MarkdownRenderer getMarkdownRenderer() {
        return new MarkdownRendererImpl();
    }

    @Bean
    public JiraWikiMarkupRenderer getJiraWikiMarkupRenderer(PortalConfig config) {
        return new JiraWikiMarkupRendererImpl(config);
    }

    @Bean ExternalLinksHtml getExternalLinksHtml() {
        return new ExternalLinksHtml();
    }

    @Bean
    IssueCommentHelpTextConverter getHtmlToStringConverter() {
        return new IssueCommentHelpTextConverter();
    }

    @Bean
    public NRPEService getNRPEService() {
        return new NRPEServiceImpl();
    }

    @Bean
    public ImportanceLevelService getImportanceLevelService() {
        return new ImportanceLevelServiceImpl();
    }

    @Bean
    public DeliveryService getDeliveryService() {
        return new DeliveryServiceImpl();
    }

    @Bean
    public CardBatchService getCardBatchService() {
        return new CardBatchServiceImpl();
    }

    @Bean
    public PcbOrderService getPcbOrderService() {
        return new PcbOrderServiceImpl();
    }

    @Bean
    public ModuleService getModuleService() {
        return new ModuleServiceImpl();
    }

    @Bean
    public CardService getCardService() {
        return new CardServiceImpl();
    }

    /* ASPECT/INTERCEPTORS */

    @Bean
    public ServiceLayerInterceptor getServiceLayerInterceptor() {
        return new ServiceLayerInterceptor();
    }

    @Bean
    public ServiceLayerInterceptorLogging getServiceLayerInterceptorLogging() {
        return new ServiceLayerInterceptorLogging();
    }

    @Bean
    public NRPEProcessor getNRPERequest() {
        return new NRPEProcessor(new NRPEExecutorTerminal());
    }

    public static final String BACKGROUND_TASKS = "backgroundTasks";
    public static final String REPORT_TASKS = "reportTasks";

    private static final Logger log = LoggerFactory.getLogger( MainConfiguration.class );
}
