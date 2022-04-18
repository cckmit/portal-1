package ru.protei.portal.ui.employee.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.employee.client.activity.list.AbstractEmployeeTableActivity;
import ru.protei.portal.ui.employee.client.activity.list.AbstractEmployeeTableView;
import ru.protei.portal.ui.employee.client.view.table.columns.EmployeeAbsenceColumn;
import ru.protei.portal.ui.employee.client.view.table.columns.EmployeeContactsColumn;
import ru.protei.portal.ui.employee.client.view.table.columns.EmployeeDepartmentColumn;
import ru.protei.portal.ui.employee.client.view.table.columns.EmployeeInfoColumn;

import java.util.List;

/**
 * Представление списка сотрудников
 */
public class EmployeeTableView extends Composite implements AbstractEmployeeTableView {
    @Inject
    public void onInit(EditClickColumn<EmployeeShortView> editClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
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
        absence.setHandler(activity);
        absence.setColumnProvider(columnProvider);
        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);
    }

    @Override
    public void addRecords(List<EmployeeShortView> employees) {
        employees.forEach(employee -> table.addRow(employee));
    }

    @Override
    public void clearRecords() {
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
    public void updateRow(EmployeeShortView item) {
        if (item != null)
            table.updateRow(item);
    }

    @Override
    public void removeRow(EmployeeShortView item) {
        if (item != null)
            table.removeRow(item);
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
        columnProvider.setChangeSelectionIfSelectedPredicate(employeeShortView -> animation.isPreviewShow());
    }

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    @Override
    public void initTable(List<Long> employeeBirthdayHideIds) {
        name = new EmployeeInfoColumn(lang, reasonLang, employeeBirthdayHideIds);
        contacts = new EmployeeContactsColumn(lang, reasonLang);
        department = new EmployeeDepartmentColumn(lang, reasonLang);
        absence = new EmployeeAbsenceColumn(reasonLang, policyService);

        editClickColumn.setEnabledPredicate(v -> policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_EDIT) );

        table.addColumn(absence.header, absence.values);
        table.addColumn(name.header, name.values);
        table.addColumn(contacts.header, contacts.values);
        table.addColumn(department.header, department.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
    }

    @UiField
    TableWidget<EmployeeShortView> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;
    @Inject
    PolicyService policyService;

    @Inject
    @UiField
    Lang lang;

    @Inject
    En_AbsenceReasonLang reasonLang;

    ClickColumnProvider<EmployeeShortView> columnProvider = new ClickColumnProvider<>();
    EmployeeInfoColumn name;
    EmployeeContactsColumn contacts;
    EmployeeDepartmentColumn department;
    EmployeeAbsenceColumn absence;
    EditClickColumn<EmployeeShortView> editClickColumn;

    AbstractEmployeeTableActivity activity;

    private static EmployeeListViewUiBinder ourUiBinder = GWT.create(EmployeeListViewUiBinder.class);
    interface EmployeeListViewUiBinder extends UiBinder<HTMLPanel, EmployeeTableView> {}
}
