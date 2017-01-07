package ru.protei.portal.ui.crm.client.factory;


import com.google.gwt.inject.client.GinModules;
import ru.brainworm.factory.generator.injector.client.FactoryInjector;
import ru.protei.portal.ui.contact.client.factory.ContactClientModule;
import ru.protei.portal.ui.common.client.factory.CommonClientModule;
import ru.protei.portal.ui.company.client.factory.CompanyClientModule;
import ru.protei.portal.ui.crm.client.activity.app.AppActivity;
import ru.protei.portal.ui.issue.client.factory.IssueClientModule;
import ru.protei.portal.ui.product.client.factory.ProductClientModule;
import ru.protei.portal.ui.region.client.factory.RegionClientModule;

/**
 * Фабрика
 */
@GinModules({
        ClientModule.class, CommonClientModule.class, CompanyClientModule.class, ProductClientModule.class,
        ContactClientModule.class, IssueClientModule.class, RegionClientModule.class
})
public interface ClientFactory
        extends FactoryInjector
{
        AppActivity getAppActivity();
}
