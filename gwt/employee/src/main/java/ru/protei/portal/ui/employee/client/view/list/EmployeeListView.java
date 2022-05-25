package ru.protei.portal.ui.employee.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.platelist.PlateList;
import ru.protei.portal.ui.employee.client.activity.list.AbstractEmployeeListActivity;
import ru.protei.portal.ui.employee.client.activity.list.AbstractEmployeeListView;

/**
 * Представление списка сотрудников
 */
public class EmployeeListView extends Composite implements AbstractEmployeeListView {

    @Inject
    public void onInit() {
        initWidget( ourUiBinder.createAndBindUi( this ) );
    }

    public void setActivity( AbstractEmployeeListActivity activity ) { this.activity = activity;  }

    @Override
    public HasWidgets getChildContainer() {
        return employeeContainer;
    }

    @Override
    public HasWidgets getFilterContainer () {
        return filterContainer;
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void showLoader( boolean isShow ) {
        loader.setVisible( isShow );
    }

    @UiField
    PlateList childContainer;

    @UiField
    HTMLPanel filterContainer;

    @UiField
    HTMLPanel loader;

    @Inject
    @UiField
    Lang lang;

    @UiField
    HTMLPanel pagerContainer;
    @UiField
    HTMLPanel employeeContainer;

    AbstractEmployeeListActivity activity;

    private static EmployeeListViewUiBinder ourUiBinder = GWT.create( EmployeeListViewUiBinder.class );
    interface EmployeeListViewUiBinder extends UiBinder< HTMLPanel, EmployeeListView > {}
}