package ru.protei.portal.ui.employeeregistration.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.employeeregistration.client.activity.edit.AbstractEmployeeRegistrationEditView;
import ru.protei.portal.ui.employeeregistration.client.activity.edit.EmployeeRegistrationEditActivity;
import ru.protei.portal.ui.employeeregistration.client.activity.filter.AbstractEmployeeRegistrationFilterView;
import ru.protei.portal.ui.employeeregistration.client.activity.page.EmployeeRegistrationPage;
import ru.protei.portal.ui.employeeregistration.client.activity.preview.AbstractEmployeeRegistrationPreviewView;
import ru.protei.portal.ui.employeeregistration.client.activity.preview.EmployeeRegistrationPreviewActivity;
import ru.protei.portal.ui.employeeregistration.client.activity.table.AbstractEmployeeRegistrationTableView;
import ru.protei.portal.ui.employeeregistration.client.activity.table.EmployeeRegistrationTableActivity;
import ru.protei.portal.ui.employeeregistration.client.view.edit.EmployeeRegistrationEditView;
import ru.protei.portal.ui.employeeregistration.client.view.filter.EmployeeRegistrationFilterView;
import ru.protei.portal.ui.employeeregistration.client.view.preview.EmployeeRegistrationPreviewView;
import ru.protei.portal.ui.employeeregistration.client.view.table.EmployeeRegistrationTableView;

public class EmployeeRegistrationClientModule extends AbstractGinModule {
    @Override
    protected void configure()    {
        bind(EmployeeRegistrationPage.class).asEagerSingleton();

        bind(EmployeeRegistrationTableActivity.class).asEagerSingleton();
        bind(AbstractEmployeeRegistrationTableView.class).to(EmployeeRegistrationTableView.class).in(Singleton.class);

        bind(EmployeeRegistrationEditActivity.class).asEagerSingleton();
        bind(AbstractEmployeeRegistrationEditView.class).to(EmployeeRegistrationEditView.class).in(Singleton.class);

        bind(EmployeeRegistrationPreviewActivity.class).asEagerSingleton();
        bind(AbstractEmployeeRegistrationPreviewView.class).to(EmployeeRegistrationPreviewView.class).in(Singleton.class);

        bind(AbstractEmployeeRegistrationFilterView.class).to(EmployeeRegistrationFilterView.class).in(Singleton.class);
    }
}

