package ru.protei.portal.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ResourceBundleMessageSource;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.aspect.ServiceLayerInterceptor;
import ru.protei.portal.core.aspect.ServiceLayerInterceptorLogging;
import ru.protei.portal.core.controller.auth.AuthInterceptor;
import ru.protei.portal.core.controller.document.DocumentStorageIndex;
import ru.protei.portal.core.controller.document.DocumentStorageIndexImpl;
import ru.protei.portal.core.dao.YoutrackRestDAO;
import ru.protei.portal.core.dao.YoutrackRestDaoImpl;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;
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
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.portal.core.utils.SimpleSidGenerator;
import ru.protei.portal.mock.AuthServiceMock;
import ru.protei.portal.mock.ReportControlServiceMock;
import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.impl.LockServiceImpl;

@Configuration
@EnableAspectJAutoProxy
public class MainTestsConfiguration {

    @Bean
    public PortalConfig getPortalConfig() throws ConfigException {
        return new PortalConfig("portal.properties");
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
    public AbsenceReasonDAO getAbsenceReasonDAO() {
        return new AbsenceReasonDAO_Impl();
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
    public CaseTaskDAO getCaseTaskDAO() {
        return new CaseTaskDAO_Impl();
    }

    @Bean
    public CaseTermDAO getCaseTermDAO() {
        return new CaseTermDAO_Impl();
    }

    @Bean
    public DevUnitDAO getDevUnitDAO() {
        return new DevUnitDAO_Impl();
    }

    @Bean
    public DevUnitVersionDAO getDevUnitVersionDAO() {
        return new DevUnitVersionDAO_Impl();
    }

    @Bean
    public DevUnitBranchDAO getDevUnitBranchDAO() {
        return new DevUnitBranchDAO_Impl();
    }

    @Bean
    public CaseCommentDAO getCaseCommentDAO() {
        return new CaseCommentDAO_Impl();
    }

    @Bean
    public CaseDocumentDAO getCaseDocumentDAO() {
        return new CaseDocumentDAO_Impl();
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
    public CaseShortViewDAO getCaseShortDAO() {
        return new CaseShortViewDAO_Impl();
    }

    @Bean
    public AuditObjectDAO getAuditDAO() {
        return new AuditObjectDAO_Impl();
    }

    @Bean
    public UserSessionDAO getUserSessionDAO() {
        return new UserSessionDAO_Impl();
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
    public PersonCompanyEntryDAO getPersonCompanyEntryDAO() {
        return new PersonCompanyEntryDAO_Impl();
    }

    @Bean
    public CompanyCategoryDAO getCompanyCategoryDAO() {
        return new CompanyCategoryDAO_Impl();
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
    public EquipmentDAO getEquipmentDAO() { return new EquipmentDAO_Impl(); }

    @Bean
    public LocationDAO getLocationDAO() {
        return new LocationDAO_Impl();
    }

    @Bean
    public CaseMemberDAO getCaseMemberDAO() { return new CaseMemberDAO_Impl(); }

    @Bean
    public CaseLocationDAO getCaseLocationDAO() { return new CaseLocationDAO_Impl(); }

    @Bean
    public CaseTypeDAO getCaseTypeDAO() { return new CaseTypeDAO_Impl(); }

    @Bean
    public DecimalNumberDAO getDecimalNumberDAO() { return new DecimalNumberDAO_Impl(); }

    @Bean
    public CompanySubscriptionDAO getCompanySubscriptionDAO () {
        return new CompanySubscriptionDAO_Impl ();
    }

    @Bean
    public ExternalCaseAppDAO getExternalCaseAppDAO () {
        return new ExternalCaseAppDAO_Impl();
    }

    @Bean
    public ExportSybEntryDAO getExportSybEntryDAO () {
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
    public CaseFilterDAO getIssueFilterDAO() { return new CaseFilterDAO_Impl(); }

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
    public YoutrackRestDAO getYoutrackDAO() {
        return new YoutrackRestDaoImpl();
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
    public YoutrackRestDAO getYoutrackService() {
        return new YoutrackRestDaoImpl();
    }

    @Bean
    public EmployeeRegistrationService getEmployeeRegistrationService() {
        return new EmployeeRegistrationServiceImpl();
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
