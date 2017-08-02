package ru.protei.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import ru.protei.portal.core.aspect.ServiceLayerInterceptor;
import ru.protei.portal.core.controller.auth.AuthInterceptor;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;
import ru.protei.portal.core.service.*;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.portal.core.service.user.AuthServiceImpl;
import ru.protei.portal.core.service.user.LDAPAuthProvider;
import ru.protei.portal.core.utils.SessionIdGen;
import ru.protei.portal.core.utils.SimpleSidGenerator;
import ru.protei.winter.core.utils.config.exception.ConfigException;

@EnableAspectJAutoProxy
@Configuration
public class MainConfiguration {

    /**
     * Config
     * @return
     */
    @Bean
    public PortalConfig getPortalConfig () throws ConfigException{
        return new PortalConfig("portal.properties");
    }

    @Bean
    public LDAPAuthProvider getLDAPAuthProvider() {
        return new LDAPAuthProvider();
    }

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
    public CaseObjectDAO getCaseDAO() {
        return new CaseObjectDAO_Impl();
    }

    @Bean
    public CaseShortViewDAO getCaseShortDAO() {
        return new CaseShortViewDAO_Impl();
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
/**
 *
 *
 *
 * SERVICES
 *
 *
 *
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

    /** ASPECT/INTERCEPTORS **/
    @Bean
    public ServiceLayerInterceptor getServiceLayerInterceptor () {
        return new ServiceLayerInterceptor();
    }
}
