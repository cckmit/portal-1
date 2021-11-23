package ru.protei.portal.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.protei.portal.core.model.converter.MoneyJdbcConverter;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.*;

import static org.mockito.Mockito.mock;

@Configuration
public class DaoTestsConfiguration {


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
    public PersonSqlBuilder getPersonSqlBuilder() {
        return new PersonSqlBuilder();
    }

    @Bean
    public ContactSqlBuilder getContactSqlBuilder() {
        return new ContactSqlBuilder();
    }

    @Bean
    public DeliverySqlBuilder deliverySqlBuilder() {
        return new DeliverySqlBuilder();
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
    public PcbOrderSqlBuilder pcbOrderSqlBuilder() {
        return new PcbOrderSqlBuilder();
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
    public PersonShortViewDAO getPersonShortViewDAO() {
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
    public CompanyImportanceItemDAO getCompanyImportanceItemDAO() {
        return new CompanyImportanceItemDAO_Impl();
    }

    @Bean
    public SubnetDAO getSubnetDAO() { return new SubnetDAO_Impl(); }

    @Bean
    public ReservedIpDAO getReservedIpDAO() { return new ReservedIpDAO_Impl(); }

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
    public PersonFavoriteIssuesDAO getPersonFavoritesIssuesDAO() {
        return new PersonFavoriteIssuesDAO_Impl();
    }

    @Bean
    public ContactItemDAO getContactItemDAO() {
        return new ContactItemDAO_Impl();
    }

    @Bean
    public ImportanceLevelDAO getImportanceLevelDAO() {
        return new ImportanceLevelDAO_Impl();
    }

    @Bean
    public BootstrapAppDAO getBootstrapAppDAO() {
        return new BootstrapAppDAO_Impl();
    }

    @Bean
    public DutyLogDAO getDutyLogDAO() {
        return new DutyLogDAO_Impl();
    }

    @Bean
    public ProjectTechnicalSupportValidityReportInfoDAO getProjectTechnicalSupportValidityReportInfoDAO() {
        return new ProjectTechnicalSupportValidityReportInfoDAO_Impl();
    }

    @Bean
    public DeliveryDAO getDeliveryDAO() {
        return new DeliveryDAO_Impl();
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

    @Bean
    public CardDAO getCardDAO() {
        return new CardDAO_Impl();
    }

    @Bean
    public CardBatchDAO getCardBatchDAO() {
        return new CardBatchDAO_Impl();
    }

    @Bean
    public PcbOrderDAO getPcbOrderDAO() {
        return new PcbOrderDAO_Impl();
    }

    @Bean
    public RFIDLabelDAO getRFIDLabelDAO() {
        return new RFIDLabelDAO_Impl();
    }
}
