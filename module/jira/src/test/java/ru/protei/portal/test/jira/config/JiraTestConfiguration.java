package ru.protei.portal.test.jira.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.scheduling.annotation.EnableAsync;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.config.PortalConfig;
import ru.protei.portal.config.PortalConfigData;
import ru.protei.portal.config.PortalConfigReloadable;
import ru.protei.portal.core.client.youtrack.api.YoutrackApi;
import ru.protei.portal.core.client.youtrack.api.YoutrackApiImpl;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClientImpl;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapper;
import ru.protei.portal.core.client.youtrack.mapper.YtDtoFieldsMapperImpl;
import ru.protei.portal.core.mail.MailSendChannel;
import ru.protei.portal.core.mail.VirtualMailSendChannel;
import ru.protei.portal.core.model.converter.MoneyJdbcConverter;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.portal.core.renderer.JiraWikiMarkupRenderer;
import ru.protei.portal.core.renderer.MarkdownRenderer;
import ru.protei.portal.core.renderer.impl.HTMLRendererImpl;
import ru.protei.portal.core.renderer.impl.JiraWikiMarkupRendererImpl;
import ru.protei.portal.core.renderer.impl.MarkdownRendererImpl;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.auth.AuthServiceImpl;
import ru.protei.portal.core.service.auth.LDAPAuthProvider;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseService;
import ru.protei.portal.core.service.autoopencase.AutoOpenCaseTaskHandler;
import ru.protei.portal.core.service.events.*;
import ru.protei.portal.core.service.nrpe.NRPEService;
import ru.protei.portal.core.service.nrpe.NRPEServiceImpl;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.policy.PolicyServiceImpl;
import ru.protei.portal.jira.aspect.JiraServiceLayerInterceptorLogging;
import ru.protei.portal.jira.factory.JiraClientFactory;
import ru.protei.portal.jira.factory.JiraClientFactoryImpl;
import ru.protei.portal.jira.service.JiraBackchannelHandler;
import ru.protei.portal.jira.service.JiraBackchannelHandlerImpl;
import ru.protei.portal.jira.service.JiraIntegrationService;
import ru.protei.portal.jira.service.JiraIntegrationServiceImpl;
import ru.protei.portal.jira.utils.JiraQueueSingleThreadPoolTaskExecutor;
import ru.protei.portal.nrpe.NRPEExecutorTest;
import ru.protei.portal.nrpe.NRPEProcessor;
import ru.protei.portal.schedule.PortalScheduleTasks;
import ru.protei.portal.test.jira.mock.JiraEndpointDAO_ImplMock;
import ru.protei.portal.test.jira.mock.JiraPriorityMapEntryDAO_ImplMock;
import ru.protei.portal.test.jira.mock.JiraStatusMapEntryDAO_ImplMock;
import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.impl.LockServiceImpl;
import ru.protei.portal.core.service.UitsService;
import ru.protei.portal.core.service.UitsServiceImpl;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import static org.mockito.Mockito.mock;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.jira.config.JiraConfigurationContext.JIRA_INTEGRATION_SINGLE_TASK_QUEUE;

@EnableAspectJAutoProxy
@EnableAsync
@Configuration
public class JiraTestConfiguration {

    @Autowired
    PortalConfig config;

    /**
     * Запуск фоновых задач
     */
    @Bean(name = JIRA_INTEGRATION_SINGLE_TASK_QUEUE)
    public Executor threadPoolTaskExecutor() {
        int queueLimit = config.data().jiraConfig().getQueueLimit();
        return new JiraQueueSingleThreadPoolTaskExecutor( queueLimit );
    }

    @Bean(name = BACKGROUND_TASKS)
    public Executor backgroundTaskExecutor() {
        return Executors.newCachedThreadPool();
    }

    @Bean
    public MailSendChannel getMailChannel() {
        return new VirtualMailSendChannel();
    }

    @Bean
    public PortalConfig getPortalConfig() throws ConfigException {
        return new PortalConfigReloadable("portal.properties");
    }

    @Bean
    public FileStorage getFileStorage(@Autowired PortalConfig config) {
        PortalConfigData.CloudConfig cloud = config.data().cloud();
        return new FileStorage(cloud.getStoragePath(), cloud.getUser(), cloud.getPassword());
    }

    @Bean
    public CaseObjectSqlBuilder sqlDefaultBuilder() {
        return new CaseObjectSqlBuilder();
    }

    @Bean
    public CaseCommentSqlBuilder getCaseCommentSqlBuilder() {
        return new CaseCommentSqlBuilder();
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

    @Bean
    public AccountingEmployeeSqlBuilder getAccountingEmployeeSqlBuilder() {
        return new AccountingEmployeeSqlBuilder();
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
    public PersonShortViewDAO getPersonShortViewDAO() {
        return new PersonShortViewDAOImpl();
    }

    @Bean
    public CompanyGroupHomeDAO getCompanyGroupHomeDAO() {
        return new CompanyGroupHomeDAO_Impl();
    }

    @Bean
    public CaseObjectDAO getCaseDAO() {
        return new CaseObjectDAO_Impl();
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
    public CaseStateMatrixDAO getStateMatrixDAO() {
        return new CaseStateMatrixDAO_Impl();
    }

    @Bean
    public CaseTypeDAO getCaseTypeDAO() {
        return new CaseTypeDAO_Impl();
    }

    @Bean
    public JiraPriorityMapEntryDAO getJiraPriorityMapEntryDAO() {
        return new JiraPriorityMapEntryDAO_ImplMock();
    }

    @Bean
    public JiraEndpointDAO getJiraEndpointDAO() {
        return new JiraEndpointDAO_ImplMock();
    }

    @Bean
    public JiraStatusMapEntryDAO getJiraStatusMapEntryDAO() {
        return new JiraStatusMapEntryDAO_ImplMock();
    }

    @Bean
    public ExternalCaseAppDAO getExternalCaseAppDAO() {
        return new ExternalCaseAppDAO_Impl();
    }

    @Bean
    public CaseCommentDAO getCaseCommentDAO() {
        return new CaseCommentDAO_Impl();
    }

    @Bean
    public CaseCommentShortViewDAO getCaseCommentShortViewDAO() {
        return new CaseCommentShortViewDAO_Impl();
    }

    @Bean
    public CaseAttachmentDAO getCaseAttachmentDAO() {
        return new CaseAttachmentDAO_Impl();
    }

    @Bean
    public AttachmentDAO getAttachmentDAO() {
        return new AttachmentDAO_Impl();
    }

    @Bean
    public CaseNotifierDAO getCaseNotifierDAO() {
        return new CaseNotifierDAO_Impl();
    }

    @Bean
    public CaseObjectTagDAO getCaseObjectTagDAO() {
        return new CaseObjectTagDAO_Impl();
    }

    @Bean
    public CaseTagDAO getCaseTagDAO() {
        return new CaseTagDAO_Impl();
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
    public CaseLinkDAO getCaseLinkDAO() {
        return new CaseLinkDAO_Impl();
    }

    @Bean
    public CaseStateDAO getCaseStateDAO() {
        return new CaseStateDAO_Impl();
    }

    @Bean
    public AuditObjectDAO getAuditDAO() {
        return new AuditObjectDAO_Impl();
    }

    @Bean
    public CaseStateWorkflowDAO getCaseStateWorkflowDAO() {
        return new CaseStateWorkflowDAO_Impl();
    }

    @Bean
    public LDAPAuthProvider getLDAPAuthProvider() {
        return new LDAPAuthProvider();
    }

    @Bean
    public EventPublisherService getEventPublisherService() {
        return new AsyncEventPublisherService();
    }

    @Bean
    public CaseService getCaseService() {
        return new CaseServiceImpl();
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

    @Bean
    public CompanyService getCompanyService() {
        return new CompanyServiceImpl();
    }

    @Bean
    public CompanyGroupDAO getCompanyGroupDao() {
        return new CompanyGroupDAO_Impl();
    }

    @Bean
    public CompanySubscriptionDAO getCompanySubscriptionDao() {
        return new CompanySubscriptionDAO_Impl();
    }

    @Bean
    public AttachmentService getAttachmentService() {
        return new AttachmentServiceImpl();
    }

    @Bean
    public JiraClientFactory getJiraClientFactory () {
        return new JiraClientFactoryImpl();
    }

    @Bean
    public JiraIntegrationService getJiraService () {
        return new JiraIntegrationServiceImpl();
    }

    @Bean
    public JiraBackchannelHandler getJiraBackchannelHandler() {
        return new JiraBackchannelHandlerImpl();
    }

    @Bean
    public JiraServiceLayerInterceptorLogging getJiraServiceLayerInterceptorLogging() {
        return new JiraServiceLayerInterceptorLogging();
    }

    @Bean
    public PolicyService getPolicyService() {
        return new PolicyServiceImpl();
    }

    @Bean
    public AuthService getAuthService() {
        return new AuthServiceImpl();
    }

    @Bean
    public CaseLinkService getCaseLinkService() {
        return new CaseLinkServiceImpl();
    }

    @Bean
    public CaseCommentService getCaseCommentService() {
        return new CaseCommentServiceImpl();
    }

/*
    @Bean
    public ClientEventService getClientEventService() {
        return new ClientEventServiceImpl();
    }
*/

    @Bean
    public CaseStateWorkflowService getCaseStateWorkflowService() {
        return new CaseStateWorkflowServiceImpl();
    }

    @Bean
    public CaseTagService getCaseTagService() {
        return new CaseTagServiceImpl();
    }

    @Bean
    public EventAssemblerService getEventAssemblerService() {
        return new EventAssemblerServiceImpl();
    }

    @Bean
    public EventProjectAssemblerService getProjectPublisherService() {
        return new EventProjectAssemblerServiceImpl();
    }

    @Bean
    public EventDeliveryAssemblerService getDeliveryPublisherService() {
        return new EventDeliveryAssemblerServiceImpl();
    }

    @Bean
    public EventEmployeeRegistrationAssemblerService getEventEmployeeRegistrationAssemblerService() {
        return new EventEmployeeRegistrationAssemblerServiceImpl();
    }

    @Bean
    public AssemblerDeliveryService getAssemblerDeliveryService() {
        return new AssemblerDeliveryServiceImpl();
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
    @Lazy
    public DeliveryService getDeliveryService() {
        return new DeliveryServiceImpl();
    }

    @Bean
    public CardService getCardService() {
        return new CardServiceImpl();
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
    public AssemblerService getAssemblerService() {
        return new AssemblerServiceImpl();
    }

    @Bean
    public AssemblerProjectService getAssemblerProjectService() {
        return new AssemblerProjectServiceImpl();
    }

    @Bean
    public AssemblerEmployeeRegistrationService getAssemblerEmployeeRegistrationService() {
        return new AssemblerEmployeeRegistrationServiceImpl();
    }

    @Bean
    public ProjectDAO getProjectDAO() {
        return new ProjectDAO_Impl();
    }

    @Bean
    public EmployeeRegistrationDAO getEmployeeRegistrationDAO() {
        return new EmployeeRegistrationDAO_Impl();
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
    public MailReceiverService getMailReceiverService() {
        return mock(MailReceiverService.class);
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
    public YoutrackProjectDAO getYoutrackProjectDAO() {
        return new YoutrackProjectDAO_Impl();
    }

    @Bean
    public YoutrackWorkDictionaryDAO getYoutrackWorkDictionaryDAO() {
        return new YoutrackWorkDictionaryDAO_Impl();
    }
    
    @Bean
    public YtDtoFieldsMapper getYtDtoFieldsMapper() {
        return new YtDtoFieldsMapperImpl();
    }

    @Bean
    public UitsService getUitsService() {
        return new UitsServiceImpl();
    }

    @Bean
    public JiraSLAMapEntryDAO getJiraSLAMapEntryDAO() {
        return new JiraSLAMapEntryDAO_Impl();
    }

    @Bean
    public LockService getLockService() {
        return new LockServiceImpl();
    }

    @Bean
    public JiraCompanyGroupDAO getJiraCompanyGroupDAO() {
        return new JiraCompanyGroupDAO_Impl();
    }

    @Bean
    public CompanyImportanceItemDAO getCompanyImportanceItemDAO() {
        return new CompanyImportanceItemDAO_Impl();
    }

    @Bean
    public SiteFolderService getSiteFolderService() {
        return new SiteFolderServiceImpl();
    }

    @Bean
    public ProductService getProductService() {
        return new ProductServiceImpl();
    }

    @Bean
    @Lazy
    public ProjectService getProjectService() {
        return new ProjectServiceImpl();
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
    public ServerSqlBuilder getServerSqlBuilder() {
        return new ServerSqlBuilder();
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
    public ProductSubscriptionDAO getProductSubscriptionDAO() {
        return new ProductSubscriptionDAO_Impl();
    }

    @Bean
    public DevUnitDAO getDevUnitDAO() {
        return new DevUnitDAO_Impl();
    }

    @Bean
    public DevUnitChildRefDAO getDevUnitChildRefDAO() {
        return new DevUnitChildRefDAO_Impl();
    }

    @Bean
    public AutoOpenCaseService getAutoOpenCaseService() {
        return mock(AutoOpenCaseService.class);
    }

    @Bean
    public AutoOpenCaseTaskHandler getAutoOpenCaseTaskHandler() {
        return mock(AutoOpenCaseTaskHandler.class);
    }

    @Bean
    public PortalScheduleTasks getPortalScheduleTasks() {
        return mock(PortalScheduleTasks.class);
    }

    @Bean
    public UserRoleDAO getUserRoleDAO() {
        return new UserRoleDAO_impl();
    }

    @Bean
    public PlanService getPlanService() {
        return new PlanServiceImpl();
    }

    @Bean
    public PlanDAO getPlanDAO() {
        return new PlanDAO_Impl();
    }

    @Bean
    public HistoryService getHistoryService() {
        return new HistoryServiceImpl();
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
    public PlanToCaseObjectDAO getPlanToCaseObjectDAO() {
        return new PlanToCaseObjectDAO_Impl();
    }

    @Bean
    public PersonFavoriteIssuesDAO getPersonFavoritesIssuesDAO() {
        return new PersonFavoriteIssuesDAO_Impl();
    }

    @Bean
    public ContactItemDAO getContactItemDAO() {
        return new ContactItemDAO_Impl();
    }

    @Bean
    public EmployeeShortViewDAO getEmployeeShortViewDAO() {
        return new EmployeeShortViewDAO_Impl();
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
    public ProjectToProductDAO getProjectToProductDAO() {
        return new ProjectToProductDAO_Impl();
    }

    @Bean
    public CommonManagerToNotifyListDAO getCommonManagerToNotifyListDAO() {
        return new CommonManagerToNotifyListDAO_Impl();
    }

    @Bean
    public ContractDAO getContractDAO() {
        return new ContractDAO_Impl();
    }

    @Bean
    public ImportanceLevelDAO getImportanceLevelDAO() {
        return new ImportanceLevelDAO_Impl();
    }

    @Bean
    public ProjectTechnicalSupportValidityReportInfoDAO getProjectTechnicalSupportValidityReportInfoDAO() {
        return new ProjectTechnicalSupportValidityReportInfoDAO_Impl();
    }

    @Bean
    public CardSqlBuilder cardSqlBuilder() {
        return new CardSqlBuilder();
    }

    @Bean
    public CardTypeDAO getCardTypeDAO() {
        return new CardTypeDAO_Impl();
    }

    @Bean
    public CardDAO getCardDAO() {
        return new CardDAO_Impl();
    }

    @Bean
    public CardBatchDAO getCardBatchDAO() {
        return new CardBatchDAO_Impl();
    }

    @Bean
    public CardBatchSqlBuilder cardBatchSqlBuilder() {
        return new CardBatchSqlBuilder();
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
    public RFIDLabelDAO getRFIDLabelDAO() {
        return new RFIDLabelDAO_Impl();
    }

    @Bean
    public RFIDDeviceDAO getRFIDDeviceDAO() {
        return new RFIDDeviceDAO_Impl();
    }

    @Bean
    public CaseElapsedTimeApiDAO getCaseElapsedTimeApiDAO() {
        return new CaseElapsedTimeApiDAO_Impl();
    }

    @Bean
    public CommonManagerDAO getCommonManagerDAO() {
        return new CommonManagerDAO_Impl();
    }

    /* DAO converters */

    @Bean
    public MoneyJdbcConverter moneyJdbcConverter() {
        return new MoneyJdbcConverter();
    }

    @Bean
    public NRPEService getNRPEService() {
        return new NRPEServiceImpl();
    }

    /* NRPE */
    @Bean
    public NRPEProcessor getNRPERequest() {
        return new NRPEProcessor(new NRPEExecutorTest());
    }
}
