package ru.protei.portal.ui.employee.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.employee.client.activity.birthday.AbstractEmployeeBirthdayView;
import ru.protei.portal.ui.employee.client.activity.birthday.EmployeeBirthdayActivity;
import ru.protei.portal.ui.employee.client.activity.edit.AbstractEmployeeEditView;
import ru.protei.portal.ui.employee.client.activity.edit.EmployeeEditActivity;
import ru.protei.portal.ui.employee.client.activity.filter.AbstractEmployeeFilterView;
import ru.protei.portal.ui.employee.client.activity.item.AbstractEmployeeItemView;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionEditItemView;
import ru.protei.portal.ui.employee.client.activity.item.AbstractPositionItemView;
import ru.protei.portal.ui.employee.client.activity.item.AbstractTopBrassItemView;
import ru.protei.portal.ui.employee.client.activity.list.*;
import ru.protei.portal.ui.employee.client.activity.page.*;
import ru.protei.portal.ui.employee.client.activity.preview.AbstractEmployeePreviewView;
import ru.protei.portal.ui.employee.client.activity.preview.EmployeePreviewActivity;
import ru.protei.portal.ui.employee.client.activity.topbrass.AbstractTopBrassActivity;
import ru.protei.portal.ui.employee.client.activity.topbrass.AbstractTopBrassView;
import ru.protei.portal.ui.employee.client.activity.topbrass.TopBrassActivity;
import ru.protei.portal.ui.employee.client.view.birthday.EmployeeBirthdayView;
import ru.protei.portal.ui.employee.client.view.edit.EmployeeEditView;
import ru.protei.portal.ui.employee.client.view.filter.EmployeeFilterView;
import ru.protei.portal.ui.employee.client.view.item.EmployeeItemView;
import ru.protei.portal.ui.employee.client.view.item.PositionEditItemView;
import ru.protei.portal.ui.employee.client.view.item.PositionItemView;
import ru.protei.portal.ui.employee.client.view.item.TopBrassItemView;
import ru.protei.portal.ui.employee.client.view.list.EmployeeListView;
import ru.protei.portal.ui.employee.client.view.preview.EmployeePreviewView;
import ru.protei.portal.ui.employee.client.view.table.EmployeeTableView;
import ru.protei.portal.ui.employee.client.view.topbrass.TopBrassView;

/**
 * Описание классов фабрики
 */
public class EmployeeClientModule extends AbstractGinModule {

    @Override
    protected void configure() {
        bind( EmployeePage.class ).asEagerSingleton();
        bind( ListPage.class ).asEagerSingleton();
        bind( TopBrassPage.class ).asEagerSingleton();
        bind( AbsencePage.class ).asEagerSingleton();
        bind( BirthdayPage.class ).asEagerSingleton();

        bind( EmployeeGridActivity.class ).asEagerSingleton();
        bind( EmployeeTableActivity.class ).asEagerSingleton();
        bind( EmployeeListActivity.class ).asEagerSingleton();
        bind( EmployeeEditActivity.class ).asEagerSingleton();
        bind( EmployeeBirthdayActivity.class ).asEagerSingleton();
        bind( AbstractEmployeeListView.class ).to( EmployeeListView.class ).in( Singleton.class );
        bind( AbstractEmployeeItemView.class ).to( EmployeeItemView.class );
        bind( AbstractEmployeeTableView.class ).to( EmployeeTableView.class ).in( Singleton.class );
        bind( AbstractEmployeeEditView.class ).to( EmployeeEditView.class ).in( Singleton.class );
        bind( AbstractEmployeeBirthdayView.class ).to( EmployeeBirthdayView.class ).in( Singleton.class );

        bind( AbstractEmployeeFilterView.class ).to( EmployeeFilterView.class ).in( Singleton.class );

        bind( EmployeePreviewActivity.class ).asEagerSingleton();
        bind( AbstractEmployeePreviewView.class ).to( EmployeePreviewView.class ).in( Singleton.class );
        bind( AbstractPositionItemView.class ).to( PositionItemView.class );
        bind( AbstractPositionEditItemView.class ).to( PositionEditItemView.class );

        bind(AbstractTopBrassView.class).to(TopBrassView.class).in(Singleton.class);
        bind(AbstractTopBrassActivity.class).to(TopBrassActivity.class).asEagerSingleton();
        bind(AbstractTopBrassItemView.class).to(TopBrassItemView.class);
    }
}
