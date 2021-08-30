package ru.protei.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Lazy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
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
import ru.protei.portal.core.report.ytwork.ReportYtWork;
import ru.protei.portal.core.report.ytwork.ReportYtWorkImpl;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.auth.AuthService;
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
import ru.protei.portal.mock.*;
import ru.protei.portal.nrpe.NRPEExecutorTest;
import ru.protei.portal.nrpe.NRPEProcessor;
import ru.protei.portal.schedule.PortalScheduleTasks;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.portal.tools.migrate.sybase.SybConnProvider;
import ru.protei.portal.tools.migrate.sybase.SybConnWrapperImpl;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.impl.LockServiceImpl;
import ru.protei.portal.core.service.UitsService;
import ru.protei.portal.core.service.UitsServiceImpl;

import java.util.concurrent.Executor;

import static org.mockito.Mockito.mock;
import static ru.protei.portal.config.MainConfiguration.REPORT_TASKS;

@Configuration
@EnableAspectJAutoProxy
public class ServiceTestsConfiguration {

    @Bean(name = REPORT_TASKS)
    public Executor getReportThreadPoolTaskExecutor(@Autowired PortalConfig config) {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(config.data().reportConfig().getThreadsNumber());
        executor.setMaxPoolSize(config.data().reportConfig().getThreadsNumber());
        return executor;
    }

    @Bean
    public FileStorage getFileStorage (@Autowired PortalConfig config){
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
    public YoutrackService getYoutrackService() {
        return new YoutrackServiceImpl();
    }

    @Bean
    public UitsService getUitsService() {
        return new UitsServiceImpl();
    }

    /* SERVICES */

    @Bean
    public SessionIdGen getSessionIdGenerator() {
        return new SimpleSidGenerator();
    }

    @Bean
    public AuthService getAuthService() {
        return new AuthServiceMock();
    }

    @Bean
    public EmployeeService getEmployeeService () { return new EmployeeServiceImpl(); }

    @Bean
    public LegacySystemDAO getLegacySystemDAO() {
        return new LegacySystemDAO();
    }
    @Bean
    public SybConnProvider getSybConnProvider(@Autowired PortalConfig config) throws Throwable {
        return new SybConnWrapperImpl(
                config.data().legacySysConfig().getJdbcDriver(),
                config.data().legacySysConfig().getJdbcURL(),
                "fakeUser",
                "fakePassword"
        );
    }

    @Bean
    public CompanyService getCompanyService() {
        return new CompanyServiceImpl();
    }

    @Bean
    public ProductService getProductService() {
        return new ProductServiceImpl();
    }

    @Bean
    public ContactService getContactService () {
        return new ContactServiceImpl();
    }

    @Bean
    public CaseService getCaseService() { return new CaseServiceImpl(); }

    @Bean
    public CaseStateService getCaseStateService() { return new CaseStateServiceImpl(); }

    @Bean
    public AuditService getAuditService() {
        return new AuditServiceImpl();
    }

    @Bean
    public AttachmentService getAttachmentService() { return new AttachmentServiceImpl(); }

    @Bean
    public LocationService getLocationService() { return new LocationServiceImpl(); }

    @Bean
    @Lazy
    public ProjectService getProjectService() { return new ProjectServiceImpl(); }

    @Bean
    public EquipmentService getEquipmentService() { return new EquipmentServiceImpl(); }

    @Bean
    public CaseSubscriptionService getCaseSubscriptionService () {
        return new CaseSubscriptionServiceImpl();
    }

    @Bean
    public UserRoleService getUserRoleService () {
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
    public TemplateService getTemplateService() { return new TemplateServiceImpl(); }

    @Bean
    public PolicyService getPolicyService() { return new PolicyServiceImpl(); }

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
    public AssemblerService getAssemblerService() {
        return new AssemblerServiceStub();
    }

    @Bean
    public AssemblerProjectService getAssemblerProjectService() {
        return new AssemblerProjectServiceStub();
    }

    @Bean
    public AssemblerDeliveryService getAssemblerDeliveryService() {
        return new AssemblerDeliveryServiceStub();
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
    public CaseFilterService getIssueFilterService () {
        return new CaseFilterServiceImpl();
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
        return new ReportControlServiceMock();
    }

    @Bean(name = "portalScheduler")
    public PortalScheduleTasks getPortalScheduleTasks() {
        return new PortalScheduleTasksStub();
    }

    @Bean
    public CaseLinkService getCaseLinkService() { return new CaseLinkServiceImpl(); }

    @Bean
    public SiteFolderService getSiteFolderService() {
        return new SiteFolderServiceImpl();
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
    public IpReservationService getIpReservationService() { return new IpReservationServiceImpl(); }

    @Bean
    public PlanService getPlanService() {
        return new PlanServiceImpl();
    }

    @Bean
    public HistoryService getHistoryService() {
        return new HistoryServiceImpl();
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
    public AutoOpenCaseTaskHandler getAutoOpenCaseTaskHandler() {
        return new AutoOpenCaseServiceTaskHandlerImpl();
    }

    @Bean
    public EmployeeRegistrationYoutrackSynchronizer getEmployeeRegistrationYoutrackSynchronizer() {
        return new EmployeeRegistrationYoutrackSynchronizerImpl();
    }

    @Bean
    public BootstrapService getBootstrapService() {
        return new BootstrapServiceImpl();
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
    public MailReceiverService getMailReceiverService() {
        return mock(MailReceiverService.class);
    }

    @Bean
    public ReportCase getReportCase() {
        return new ReportCaseImpl();
    }

    @Bean
    public ReportNightWork getReportNightWork() {
        return new ReportNightWorkImpl();
    }

    @Bean
    public ReportCaseTimeElapsed getReportCaseTimeElapsed() {
        return new ReportCaseTimeElapsedImpl();
    }

    @Bean
    public ReportProject getReportProject() {
        return new ReportProjectImpl();
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
    public ReportYtWork getReportYtWork() {
        return new ReportYtWorkImpl();
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
    public NRPEService getNRPEService() {
        return new NRPEServiceImpl();
    }

    @Bean
    public DeliveryService getDeliveryService() {
        return new DeliveryServiceImpl();
    }

    /* ASPECT/INTERCEPTORS */

    @Bean
    public ServiceLayerInterceptor getServiceLayerInterceptor () {
        return new ServiceLayerInterceptor();
    }

    @Bean
    public ServiceLayerInterceptorLogging getServiceLayerInterceptorLogging() {
        return new ServiceLayerInterceptorLogging();
    }

    @Bean
    public NRPEProcessor getNRPERequest() {
        return new NRPEProcessor(new NRPEExecutorTest());
    }

    @Bean
    public ModuleService getModuleService() {
        return new ModuleServiceImpl();
    }
}
