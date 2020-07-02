package ru.protei.portal.app.portal.client.factory;


import com.google.gwt.inject.client.GinModules;
import ru.brainworm.factory.generator.injector.client.FactoryInjector;
import ru.protei.portal.app.portal.client.activity.app.AppActivity;
import ru.protei.portal.ui.absence.client.factory.AbsenceClientModule;
import ru.protei.portal.ui.absencereport.client.factory.AbsenceReportClientModule;
import ru.protei.portal.ui.account.client.factory.AccountClientModule;
import ru.protei.portal.ui.casestate.client.factory.CaseStateClientModule;
import ru.protei.portal.ui.common.client.factory.CommonClientModule;
import ru.protei.portal.ui.company.client.factory.CompanyClientModule;
import ru.protei.portal.ui.contact.client.factory.ContactClientModule;
import ru.protei.portal.ui.contract.client.factory.ContractClientModule;
import ru.protei.portal.ui.document.client.factory.DocumentClientModule;
import ru.protei.portal.ui.documenttype.client.factory.DocumentTypeClientModule;
import ru.protei.portal.ui.employee.client.factory.EmployeeClientModule;
import ru.protei.portal.ui.employeeregistration.client.factory.EmployeeRegistrationClientModule;
import ru.protei.portal.ui.equipment.client.factory.EquipmentClientModule;
import ru.protei.portal.ui.ipreservation.client.factory.IpReservationClientModule;
import ru.protei.portal.ui.issue.client.factory.IssueClientModule;
import ru.protei.portal.ui.issueassignment.client.factory.IssueAssignmentClientModule;
import ru.protei.portal.ui.issuereport.client.factory.IssueReportClientModule;
import ru.protei.portal.ui.product.client.factory.ProductClientModule;
import ru.protei.portal.ui.project.client.factory.ProjectClientModule;
import ru.protei.portal.ui.region.client.factory.RegionClientModule;
import ru.protei.portal.ui.role.client.factory.RoleClientModule;
import ru.protei.portal.ui.roomreservation.client.factory.RoomReservationClientModule;
import ru.protei.portal.ui.sitefolder.client.factory.SiteFolderClientModule;

/**
 * Фабрика
 */
@GinModules({
        ClientModule.class, CommonClientModule.class, IssueAssignmentClientModule.class,
        EmployeeClientModule.class, AbsenceReportClientModule.class, RoomReservationClientModule.class, CompanyClientModule.class, ContactClientModule.class,
        ProductClientModule.class, ProjectClientModule.class, RegionClientModule.class, EmployeeRegistrationClientModule.class,
        IssueClientModule.class, IssueReportClientModule.class, EquipmentClientModule.class, RoleClientModule.class, AccountClientModule.class,
        DocumentClientModule.class, DocumentTypeClientModule.class, CaseStateClientModule.class, SiteFolderClientModule.class,
        ContractClientModule.class, IpReservationClientModule.class, AbsenceClientModule.class
})
public interface ClientFactory
        extends FactoryInjector
{
        AppActivity getAppActivity();
}
