package ru.protei.portal.ui.employee.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.struct.PlainContactInfoFacade;
import ru.protei.portal.core.model.struct.WorkerEntryFacade;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.model.view.WorkerEntryShortView;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.DynamicColumn;
import ru.protei.portal.ui.common.client.common.LabelValuePairBuilder;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.common.EmailRender;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.employee.client.activity.list.AbstractEmployeeTableActivity;
import ru.protei.portal.ui.employee.client.activity.list.AbstractEmployeeTableView;
import ru.protei.portal.ui.employee.client.view.table.columns.EmployeeContactsColumn;
import ru.protei.portal.ui.employee.client.view.table.columns.EmployeeDepartmentColumn;
import ru.protei.portal.ui.employee.client.view.table.columns.EmployeeInfoColumn;

/**
 * Представление списка сотрудников
 */
public class EmployeeTableView extends Composite implements AbstractEmployeeTableView {
    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractEmployeeTableActivity activity) {
        this.activity = activity;

        name.setHandler(activity);
        name.setColumnProvider(columnProvider);
        contacts.setHandler(activity);
        contacts.setColumnProvider(columnProvider);
        department.setHandler(activity);
        department.setColumnProvider(columnProvider);
        table.setLoadHandler(activity);
        table.setPagerListener(activity);
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public HasWidgets getFilterContainer() {
        return filterContainer;
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
    }

    @Override
    public void setPersonsCount(Long issuesCount) {
        table.setTotalRecords(issuesCount.intValue());
    }

    @Override
    public void triggerTableLoad() {
        table.setTotalRecords(table.getPageSize());
    }

    @Override
    public void setTotalRecords(int totalRecords) {
        table.setTotalRecords(totalRecords);
    }

    @Override
    public int getPageCount() {
        return table.getPageCount();
    }

    @Override
    public void scrollTo(int page) {
        table.scrollToPage(page);
    }

    @Override
    public void updateRow(EmployeeShortView item) {
        if (item != null)
            table.updateRow(item);
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
    }

    private void initTable() {
        name = new EmployeeInfoColumn(lang, reasonLang);
        contacts = new EmployeeContactsColumn(lang, reasonLang);
        department = new EmployeeDepartmentColumn(lang, reasonLang);

        table.addColumn(name.header, name.values);
        table.addColumn(contacts.header, contacts.values);
        table.addColumn(department.header, department.values);
    }

    @UiField
    InfiniteTableWidget<EmployeeShortView> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    @UiField
    Lang lang;

    @Inject
    En_AbsenceReasonLang reasonLang;

    ClickColumnProvider<EmployeeShortView> columnProvider = new ClickColumnProvider<>();
    EmployeeInfoColumn name;
    EmployeeContactsColumn contacts;
    EmployeeDepartmentColumn department;

    AbstractEmployeeTableActivity activity;

    private static EmployeeListViewUiBinder ourUiBinder = GWT.create(EmployeeListViewUiBinder.class);
    interface EmployeeListViewUiBinder extends UiBinder<HTMLPanel, EmployeeTableView> {}
}