package ru.protei.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.annotation.Scope;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.io.Resource;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.aspect.ServiceLayerInterceptor;
import ru.protei.portal.core.aspect.ServiceLayerInterceptorLogging;
import ru.protei.portal.core.client.youtrack.api.YoutrackApiClient;
import ru.protei.portal.core.client.youtrack.api.YoutrackApiClientImpl;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClient;
import ru.protei.portal.core.client.youtrack.http.YoutrackHttpClientImpl;
import ru.protei.portal.core.client.youtrack.rest.YoutrackRestClient;
import ru.protei.portal.core.client.youtrack.rest.YoutrackRestClientImpl;
import ru.protei.portal.core.controller.auth.AuthInterceptor;
import ru.protei.portal.core.controller.document.DocumentStorageIndex;
import ru.protei.portal.core.controller.document.DocumentStorageIndexImpl;
import ru.protei.portal.core.model.dao.impl.CaseCommentSqlBuilder;
import ru.protei.portal.core.model.dao.impl.CaseObjectSqlBuilder;
import ru.protei.portal.core.model.dao.impl.EmployeeSqlBuilder;
import ru.protei.portal.core.model.dao.impl.ServerSqlBuilder;
import ru.protei.portal.core.renderer.HTMLRenderer;
import ru.protei.portal.core.renderer.JiraWikiMarkupRenderer;
import ru.protei.portal.core.renderer.MarkdownRenderer;
import ru.protei.portal.core.renderer.impl.HTMLRendererImpl;
import ru.protei.portal.core.renderer.impl.JiraWikiMarkupRendererImpl;
import ru.protei.portal.core.renderer.impl.MarkdownRendererImpl;
import ru.protei.portal.core.report.caseobjects.ReportCase;
import ru.protei.portal.core.report.caseobjects.ReportCaseImpl;
import ru.protei.portal.core.report.casetimeelapsed.ReportCaseTimeElapsed;
import ru.protei.portal.core.report.casetimeelapsed.ReportCaseTimeElapsedImpl;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.*;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.policy.PolicyServiceImpl;
import ru.protei.portal.core.service.template.TemplateService;
import ru.protei.portal.core.service.template.TemplateServiceImpl;
import ru.protei.portal.core.utils.EventExpirationControl;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.portal.core.utils.SimpleSidGenerator;
import ru.protei.portal.mock.AuthServiceMock;
import ru.protei.portal.mock.ReportControlServiceMock;
import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.impl.LockServiceImpl;

import static ru.protei.winter.core.CoreConfigurationContext.SCHEDULER;

@Configuration
@EnableAspectJAutoProxy
public class ServiceTestsConfiguration {

    @Bean(name = SCHEDULER)
    @Scope(BeanDefinition.SCOPE_SINGLETON)
    public TaskScheduler getTaskScheduler() throws ConfigException {
        return new ThreadPoolTaskScheduler();
    }

    @Value("classpath:portal.properties")
    private Resource props;

    /**
     * Config
     * @return
     */
    @Bean
    public PortalConfig getPortalConfig () throws ConfigException {
        return new TestPortalConfig( props );
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

    /* DAO SQL builders */

    @Bean
    public CaseObjectSqlBuilder sqlDefaultBuilder () {
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
    public YoutrackApiClient getYoutrackApiClient() {
        return new YoutrackApiClientImpl();
    }

    @Bean
    public YoutrackHttpClient getYoutrackHttpClient() {
        return new YoutrackHttpClientImpl();
    }

    @Bean
    public YoutrackService getYoutrackService() {
        return new YoutrackServiceImpl();
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
    public AuthInterceptor getAuthInterceptor() {
        return new AuthInterceptor();
    }

    @Bean
    public CaseControlService getCaseControlService () {
        return new CaseControlServiceImpl();
    }

    @Bean
    public EmployeeService getEmployeeService () { return new EmployeeServiceImpl(); }

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
    public ProjectService getProjectService() { return new ProjectServiceImpl(); }

    @Bean
    public EquipmentService getEquipmentService() { return new EquipmentServiceImpl(); }

    @Bean
    public EventPublisherService getEventPublisherService () {
        return new AsyncEventPublisherService();
    }

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
    public OfficialService getOfficialService() { return new OfficialServiceImpl(); }

    @Bean
    public EventAssemblerService getEventAssemblerService() {
        return new EventAssemblerServiceImpl();
    }

    @Bean
    public EventExpirationControl getEventExpirationControl() {
        return new EventExpirationControl();
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
    public IssueFilterService getIssueFilterService () {
        return new IssueFilterServiceImpl();
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
    public DocumentSvnService getDocumentSvnService() {
        return new DocumentSvnServiceImpl();
    }
    
    @Bean
    public ReportService getReportService() {
        return new ReportServiceImpl();
    }

    @Bean
    public ReportControlService getReportControlService() {
        return new ReportControlServiceMock();
    }

    @Bean
    public CaseLinkService getCaseLinkService() { return new CaseLinkServiceImpl(); }

    @Bean
    public SiteFolderService getSiteFolderService() {
        return new SiteFolderServiceImpl();
    }

    @Bean
    public YoutrackRestClient getYoutrackRestClient() {
        return new YoutrackRestClientImpl();
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
    public ReportCase getReportCase() {
        return new ReportCaseImpl();
    }

    @Bean
    public ReportCaseTimeElapsed getReportCaseTimeElapsed() {
        return new ReportCaseTimeElapsedImpl();
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
    public JiraWikiMarkupRenderer getJiraWikiMarkupRenderer() {
        return new JiraWikiMarkupRendererImpl();
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
}
