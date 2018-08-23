package ru.protei.portal.ui.employee.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.employee.client.activity.filter.AbstractEmployeeFilterView;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemView;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemView;
import ru.protei.portal.ui.employee.client.activity.list.AbstractEmployeeListView;
import ru.protei.portal.ui.employee.client.activity.list.EmployeeListActivity;
import ru.protei.portal.ui.employee.client.activity.page.EmployeePage;
import ru.protei.portal.ui.employee.client.activity.preview.AbstractEmployeePreviewView;
import ru.protei.portal.ui.employee.client.activity.preview.EmployeePreviewActivity;
import ru.protei.portal.ui.employee.client.view.filter.EmployeeFilterView;
import ru.protei.portal.ui.employee.client.view.item.EmployeeItemView;
import ru.protei.portal.ui.employee.client.view.item.PositionItemView;
import ru.protei.portal.ui.employee.client.view.list.EmployeeListView;
import ru.protei.portal.ui.employee.client.view.preview.EmployeePreviewView;

/**
 * Описание классов фабрики
 */
public class EmployeeClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind( EmployeePage.class ).asEagerSingleton();

        bind( EmployeeListActivity.class ).asEagerSingleton();
        bind( AbstractEmployeeListView.class ).to( EmployeeListView.class ).in( Singleton.class );
        bind( AbstractEmployeeItemView.class ).to( EmployeeItemView.class );

        bind( AbstractEmployeeFilterView.class ).to( EmployeeFilterView.class ).in( Singleton.class );

        bind( EmployeePreviewActivity.class ).asEagerSingleton();
        bind( AbstractEmployeePreviewView.class ).to( EmployeePreviewView.class ).in( Singleton.class );
        bind( AbstractPositionItemView.class ).to( PositionItemView.class );
    }
}