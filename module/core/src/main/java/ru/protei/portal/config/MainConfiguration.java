package ru.protei.portal.config;

import org.springframework.core.env.Environment;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.*;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import ru.protei.portal.api.struct.FileStorage;
import ru.protei.portal.core.Lang;
import ru.protei.portal.core.aspect.ServiceLayerInterceptor;
import ru.protei.portal.core.aspect.ServiceLayerInterceptorLogging;
import ru.protei.portal.core.controller.auth.AuthInterceptor;
import ru.protei.portal.core.controller.document.DocumentStorageIndex;
import ru.protei.portal.core.controller.document.DocumentStorageIndexImpl;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;
import ru.protei.portal.core.model.ent.CaseInfo;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.bootstrap.BootstrapService;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.portal.core.service.user.AuthServiceImpl;
import ru.protei.portal.core.service.user.LDAPAuthProvider;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.portal.core.utils.SimpleSidGenerator;
import ru.protei.portal.tools.migrate.export.ActiveExportDataService;
import ru.protei.portal.tools.migrate.export.DummyExportDataService;
import ru.protei.portal.tools.migrate.export.ExportDataService;
import ru.protei.portal.tools.migrate.imp.ImportDataService;
import ru.protei.portal.tools.migrate.imp.ImportDataServiceImpl;
import ru.protei.portal.tools.migrate.imp.MigrationRunner;
import ru.protei.portal.tools.migrate.sybase.LegacySystemDAO;
import ru.protei.portal.tools.migrate.sybase.SybConnProvider;
import ru.protei.portal.tools.migrate.sybase.SybConnWrapperImpl;
import ru.protei.winter.core.utils.config.exception.ConfigException;
import ru.protei.winter.core.utils.services.lock.LockService;
import ru.protei.winter.core.utils.services.lock.impl.LockServiceImpl;
import ru.protei.winter.jdbc.JdbcObjectMapperRegistrator;

import javax.annotation.PostConstruct;
import javax.inject.Inject;


@Configuration
@EnableAspectJAutoProxy
@EnableTransactionManagement
@EnableScheduling
@PropertySource("classpath:spring.properties")
public class MainConfiguration {

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
        return new PortalConfig("portal.properties");
    }

    @Bean
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


    @Bean
    public LegacySystemDAO getLegacySystemDAO() {
        return new LegacySystemDAO();
    }

    @Bean
    public LDAPAuthProvider getLDAPAuthProvider() {
        return new LDAPAuthProvider();
    }


    @Bean
    public CaseObjectSqlBuilder sqlDefaultBuilder() {
        return new CaseObjectSqlBuilder();
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

    /**
     * SERVICES
     **/
    @Bean
    public SessionIdGen getSessionIdGenerator() {
        return new SimpleSidGenerator();
    }

    @Bean
    public AuthService getAuthService() {
        return new AuthServiceImpl();
    }

    @Bean
    public AuthInterceptor getAuthInterceptor() {
        return new AuthInterceptor();
    }

    @Bean
    public CaseControlService getCaseControlService() {
        return new CaseControlServiceImpl();
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
    public OfficialService getOfficialService() {
        return new OfficialServiceImpl();
    }

    @Bean
    public BootstrapService getBootstrapService() {
        return new BootstrapService();
    }

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
    public IssueFilterService getIssueFilterService() {
        return new IssueFilterServiceImpl();
    }

    @Bean
    public ExportDataService getExportDataService(@Autowired PortalConfig config) {
        return config.data().legacySysConfig().isExportEnabled() ? new ActiveExportDataService() : new DummyExportDataService();
    }

    @Bean
    public MigrationRunner getImportDataRunner(@Autowired PortalConfig config) {
        return config.data().legacySysConfig().isImportEnabled() ? new MigrationRunner() : null;
    }

    @Bean
    public ImportDataService getImportDataService(@Autowired PortalConfig config) {
        return new ImportDataServiceImpl();
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
        return new ReportControlServiceImpl();
    }

    @Bean
    public CaseLinkService getCaseLinkService() {
        return new CaseLinkServiceImpl();
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
    public EmployeeRegistrationService getEmployeeRegistrationService() {
        return new EmployeeRegistrationServiceImpl();
    }


    /**
     * ASPECT/INTERCEPTORS
     **/
    @Bean
    public ServiceLayerInterceptor getServiceLayerInterceptor() {
        return new ServiceLayerInterceptor();
    }

    @Bean
    public ServiceLayerInterceptorLogging getServiceLayerInterceptorLogging() {
        return new ServiceLayerInterceptorLogging();
    }

}
