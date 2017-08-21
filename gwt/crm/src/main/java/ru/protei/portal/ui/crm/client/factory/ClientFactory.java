package ru.protei.portal.ui.crm.client.factory;


import com.google.gwt.inject.client.GinModules;
import ru.brainworm.factory.generator.injector.client.FactoryInjector;
import ru.protei.portal.ui.account.client.factory.AccountClientModule;
import ru.protei.portal.ui.equipment.client.factory.EquipmentClientModule;
import ru.protei.portal.ui.contact.client.factory.ContactClientModule;
import ru.protei.portal.ui.common.client.factory.CommonClientModule;
import ru.protei.portal.ui.company.client.factory.CompanyClientModule;
import ru.protei.portal.ui.crm.client.activity.app.AppActivity;
import ru.protei.portal.ui.issue.client.factory.IssueClientModule;
import ru.protei.portal.ui.product.client.factory.ProductClientModule;
import ru.protei.portal.ui.project.client.factory.ProjectClientModule;
import ru.protei.portal.ui.region.client.factory.RegionClientModule;
import ru.protei.portal.ui.role.client.factory.RoleClientModule;
import ru.protei.portal.ui.decision.client.factory.DecisionClientModule;

/**
 * Фабрика
 */
@GinModules({
        ClientModule.class, CommonClientModule.class, CompanyClientModule.class, ContactClientModule.class,
        ProductClientModule.class, ProjectClientModule.class, RegionClientModule.class,
        IssueClientModule.class, EquipmentClientModule.class, RoleClientModule.class, AccountClientModule.class,
        DecisionClientModule.class
})
public interface ClientFactory
        extends FactoryInjector
{
        AppActivity getAppActivity();
}
