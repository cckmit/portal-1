package ru.protei.portal.ui.crm.client.factory;


import com.google.gwt.inject.client.GinModules;
import ru.brainworm.factory.generator.injector.client.FactoryInjector;
//import ru.protei.portal.ui.crm.client.factory.ClientModule;
import ru.protei.portal.ui.common.client.factory.CommonClientModule;
import ru.protei.portal.ui.company.client.factory.CompanyClientModule;
import ru.protei.portal.ui.crm.client.activity.app.AppActivity;
import ru.protei.portal.ui.product.client.factory.ProductClientModule;

/**
 * Фабрика
 */
@GinModules({
        ClientModule.class, CommonClientModule.class, CompanyClientModule.class, ProductClientModule.class,
        ClientModule.class
})
public interface ClientFactory
        extends FactoryInjector
{
        AppActivity getAppActivity();
}
