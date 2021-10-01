package ru.protei.portal.redmine.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.transaction.support.TransactionTemplate;
import ru.protei.portal.core.model.converter.MoneyJdbcConverter;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dao.impl.CardSqlBuilder;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import static org.mockito.Mockito.mock;

@Configuration
public class RedmineTestConfigurationMockDao {

    @Bean
    public JdbcTemplate getJJdbcTemplate() {
        return mock( JdbcTemplate.class );
    }

    @Bean
    public JdbcManyRelationsHelper getJdbcManyRelationsHelper() {
        return mock( JdbcManyRelationsHelper.class );
    }

    @Bean
    public TransactionTemplate getTransactionTemplate() {
        return mock( TransactionTemplate.class );
    }

    @Bean
    public RedminePriorityMapEntryDAO getRedminePriorityMapEntryDAO(  ) {
        return mock(RedminePriorityMapEntryDAO.class);
    }
    @Bean
    public RedmineToCrmStatusMapEntryDAO geRedmineToCrmStatusMapEntryDAO(  ) {
        return mock(RedmineToCrmStatusMapEntryDAO.class);
    }
    @Bean
    public RedmineStatusMapEntryDAO geRedmineStatusMapEntryDAO(  ) {
        return mock(RedmineStatusMapEntryDAO.class);
    }

    @Bean
    public RedmineEndpointDAO geRedmineEndpointDAO(  ) {
        return mock(RedmineEndpointDAO.class);
    }

    @Bean
    public CaseLinkDAO getCaseLinkDAO() {
        return mock( CaseLinkDAO.class );
    }

    @Bean
    public CaseObjectDAO getCaseDAO() {
        return mock( CaseObjectDAO.class );
    }

    @Bean
    public CaseObjectMetaDAO getCaseMetaDAO() {
        return mock( CaseObjectMetaDAO.class );
    }

    @Bean
    public CaseObjectMetaNotifiersDAO getCaseMetaNotifiersDAO() {
        return mock( CaseObjectMetaNotifiersDAO.class );
    }


    @Bean
    public MigrationEntryDAO getMigrationEntryDAO() {
        return mock( MigrationEntryDAO.class );
    }

    @Bean
    public CompanyGroupHomeDAO getCompanyGroupHomeDAO() {
        return mock( CompanyGroupHomeDAO.class );
    }


    @Bean
    public PersonAbsenceDAO getPersonAbsenceDAO() {
        return mock( PersonAbsenceDAO.class );
    }

    @Bean
    public CompanyDAO getCompanyDAO() {
        return mock( CompanyDAO.class );
    }

    @Bean
    public PersonDAO getPersonDAO() {
        return mock( PersonDAO.class );
    }

    @Bean
    public PersonShortViewDAO getPersonShortViewDAO() {
        return mock( PersonShortViewDAO.class );
    }

    @Bean
    public DevUnitDAO getDevUnitDAO() {
        return mock( DevUnitDAO.class );
    }

    @Bean
    public CaseCommentDAO getCaseCommentDAO() {
        return mock( CaseCommentDAO.class );
    }

    @Bean
    public CaseCommentShortViewDAO getCaseCommentShortViewDAODAO() {
        return mock( CaseCommentShortViewDAO.class );
    }

    @Bean
    public CaseStateMatrixDAO getStateMatrixDAO() {
        return mock( CaseStateMatrixDAO.class );
    }

    @Bean
    public CaseStateDAO getStateDAO() {
        return mock( CaseStateDAO.class );
    }

    @Bean
    public CaseShortViewDAO getCaseShortDAO() {
        return mock( CaseShortViewDAO.class );
    }

    @Bean
    public AuditObjectDAO getAuditDAO() {
        return mock( AuditObjectDAO.class );
    }

    @Bean
    public UserRoleDAO getUserRoleDAO() {
        return mock( UserRoleDAO.class );
    }

    @Bean
    public UserCaseAssignmentDAO getUserCaseAssignmentDAO() {
        return mock( UserCaseAssignmentDAO.class );
    }

    @Bean
    public UserLoginDAO getUserLoginDAO() {
        return mock( UserLoginDAO.class );
    }

    @Bean
    public UserLoginShortViewDAO getUserLoginShortViewDAO() {
        return mock(UserLoginShortViewDAO.class);
    }

    @Bean
    public UserDashboardDAO getUserDashboardDAO() {
        return mock( UserDashboardDAO.class );
    }

    @Bean
    public CompanyDepartmentDAO getCompanyDepartmentDAO() {
        return mock( CompanyDepartmentDAO.class );
    }

    @Bean
    public WorkerPositionDAO getWorkerPositionDAO() {
        return mock( WorkerPositionDAO.class );
    }

    @Bean
    public WorkerEntryDAO getWorkerEntryDAO() {
        return mock( WorkerEntryDAO.class );
    }

    @Bean
    public CompanyGroupDAO getCompanyGroupDAO() {
        return mock( CompanyGroupDAO.class );
    }

    @Bean
    public CompanyGroupItemDAO getCompanyGroupItemDAO() {
        return mock( CompanyGroupItemDAO.class );
    }

    @Bean
    public PersonCompanyEntryDAO getPersonCompanyEntryDAO() {
        return mock( PersonCompanyEntryDAO.class );
    }

    @Bean
    public CaseAttachmentDAO getCaseAttachmentDAO() {
        return mock( CaseAttachmentDAO.class );
    }

    @Bean
    public CaseNotifierDAO getCaseNotifierDAO() {
        return mock( CaseNotifierDAO.class );
    }

    @Bean
    public AttachmentDAO getAttachmentDAO() {
        return mock( AttachmentDAO.class );
    }

    @Bean
    public EquipmentDAO getEquipmentDAO() {
        return mock( EquipmentDAO.class );
    }

    @Bean
    public LocationDAO getLocationDAO() {
        return mock( LocationDAO.class );
    }

    @Bean
    public CaseMemberDAO getCaseMemberDAO() {
        return mock( CaseMemberDAO.class );
    }

    @Bean
    public CaseLocationDAO getCaseLocationDAO() {
        return mock( CaseLocationDAO.class );
    }

    @Bean
    public CaseTypeDAO getCaseTypeDAO() {
        return mock( CaseTypeDAO.class );
    }

    @Bean
    public DecimalNumberDAO getDecimalNumberDAO() {
        return mock( DecimalNumberDAO.class );
    }

    @Bean
    public CompanySubscriptionDAO getCompanySubscriptionDAO() {
        return mock( CompanySubscriptionDAO.class );
    }

    @Bean
    public ExternalCaseAppDAO getExternalCaseAppDAO() {
        return mock( ExternalCaseAppDAO.class );
    }

    @Bean
    public ExportSybEntryDAO getExportSybEntryDAO() {
        return mock( ExportSybEntryDAO.class );
    }

    @Bean
    public DocumentDAO getDocumentDAO() {
        return mock( DocumentDAO.class );
    }

    @Bean
    public DocumentTypeDAO getDocumentTypeDAO() {
        return mock( DocumentTypeDAO.class );
    }

    @Bean
    public CaseFilterDAO getIssueFilterDAO() {
        return mock( CaseFilterDAO.class );
    }

    @Bean
    public ProductSubscriptionDAO getProductSubscriptionDAO() {
        return mock( ProductSubscriptionDAO.class );
    }

    @Bean
    public ReportDAO getReportDAO() {
        return mock( ReportDAO.class );
    }


    @Bean
    public ProjectToProductDAO getProjectToProductDAO() {
        return mock( ProjectToProductDAO.class );
    }

    @Bean
    public PlatformDAO getPlatformDAO() {
        return mock( PlatformDAO.class );
    }

    @Bean
    public ProjectDAO getProjectDAO() {
        return mock( ProjectDAO.class );
    }

    @Bean
    public ServerDAO getServerDAO() {
        return mock( ServerDAO.class );
    }

    @Bean
    public ServerGroupDAO getServerGroupDAO() {
        return mock(ServerGroupDAO.class);
    }

    @Bean
    public ApplicationDAO getApplicationDAO() {
        return mock( ApplicationDAO.class );
    }

    @Bean
    public ServerApplicationDAO getServerApplicationDAO() {
        return mock( ServerApplicationDAO.class );
    }

    @Bean
    public DevUnitChildRefDAO getDevUnitChildRefDAO() {
        return mock( DevUnitChildRefDAO.class );
    }

    @Bean
    public EmployeeShortViewDAO getEmployeeShortViewDAO() {
        return mock( EmployeeShortViewDAO.class );
    }

    @Bean
    public EmployeeRegistrationDAO getEmployeeRegistrationDAO() {
        return mock( EmployeeRegistrationDAO.class );
    }

    @Bean
    public CaseCommentTimeElapsedSumDAO getCaseCommentCaseObjectDAO() {
        return mock( CaseCommentTimeElapsedSumDAO.class );
    }

    @Bean
    public ContractDAO getContractDAO() {
        return mock( ContractDAO.class );
    }

    @Bean
    public ContractDateDAO getContractDateDAO() {
        return mock( ContractDateDAO.class );
    }

    @Bean
    public CaseTagDAO getCaseTagDAO() {
        return mock( CaseTagDAO.class );
    }

    @Bean
    public CaseObjectTagDAO getCaseObjectTagDAO() {
        return mock( CaseObjectTagDAO.class );
    }

    @Bean
    public CaseStateWorkflowDAO getCaseStateWorkflowDAO() {
        return mock( CaseStateWorkflowDAO.class );
    }

    @Bean
    public WorkerEntryShortViewDAO getWorkerEntryShortViewDAO() {
        return mock( WorkerEntryShortViewDAO.class );
    }

    @Bean
    public JiraEndpointDAO getJiraEndpointDAO() {
        return mock( JiraEndpointDAO.class );
    }

    @Bean
    public JiraStatusMapEntryDAO getJiraStatusMapEntryDAO() {
        return mock( JiraStatusMapEntryDAO.class );
    }

    @Bean
    public JiraSLAMapEntryDAO getJiraSLAMapEntryDAO() {
        return mock( JiraSLAMapEntryDAO.class );
    }

    @Bean
    public CompanyImportanceItemDAO getCompanyImportanceItemDAO() {
        return mock(CompanyImportanceItemDAO.class);
    }

    @Bean
    public HistoryDAO getHistoryDAO() {
        return mock(HistoryDAO.class);
    }

    @Bean
    public EmployeeRegistrationHistoryDAO getEmployeeRegistrationHistoryDAO() {
        return mock(EmployeeRegistrationHistoryDAO.class);
    }

    @Bean
    public PersonFavoriteIssuesDAO getPersonFavoriteIssuesDAO() {
        return mock(PersonFavoriteIssuesDAO.class);
    }

    @Bean
    public ContactItemDAO getContactItemDAO() {
        return mock(ContactItemDAO.class);
    }

    @Bean
    public ImportanceLevelDAO getImportanceLevelDAO() {
        return mock(ImportanceLevelDAO.class);
    }

    @Bean
    public ProjectTechnicalSupportValidityReportInfoDAO getProjectTechnicalSupportValidityReportInfoDAO() {
        return mock( ProjectTechnicalSupportValidityReportInfoDAO.class);
    }

    @Bean
    public DeliveryDAO getDeliveryDAO() {
        return mock(DeliveryDAO.class);
    }

    @Bean
    public KitDAO getKitDAO() {
        return mock(KitDAO.class);
    }

    /* DAO converters */

    @Bean
    public MoneyJdbcConverter moneyJdbcConverter() {
        return new MoneyJdbcConverter();
    }

    @Bean
    public CardSqlBuilder cardSqlBuilder() {
        return mock(CardSqlBuilder.class);
    }

    @Bean
    public CardDAO getCardDAO() {
        return mock(CardDAO.class);
    }

    @Bean
    public CardTypeDAO getCardTypeDAO() {
        return mock(CardTypeDAO.class);
    }

    @Bean
    public CardBatchDAO getCardBatchDAO() {
        return mock(CardBatchDAO.class);
    }
}
