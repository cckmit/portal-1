package ru.protei.portal.ui.employee.client.view.list;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Anchor;
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
        return childContainer;
    }

    @Override
    public HasWidgets getFilterContainer () { return filterContainer; }

    @UiHandler("tableMode")
    public void tableModeClick(ClickEvent event) {
        plateMode.setVisible(true);tableMode.setVisible(false);
        childContainer.getElement().removeClassName("plate-list");
        childContainer.getElement().addClassName("table-list");
    }

    @UiHandler("plateMode")
    public void plateModeClick(ClickEvent event) {
        plateMode.setVisible(false);tableMode.setVisible(true);
        childContainer.getElement().removeClassName("table-list");
        childContainer.getElement().addClassName("plate-list");
    }

    @UiField
    PlateList childContainer;

    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;
    @UiField
    Anchor plateMode;
    @UiField
    Anchor tableMode;

    AbstractEmployeeListActivity activity;

    private static EmployeeListViewUiBinder ourUiBinder = GWT.create( EmployeeListViewUiBinder.class );
    interface EmployeeListViewUiBinder extends UiBinder< HTMLPanel, EmployeeListView > {}
}