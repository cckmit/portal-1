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
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.employee.client.activity.list.AbstractEmployeeTableActivity;
import ru.protei.portal.ui.employee.client.activity.list.AbstractEmployeeTableView;

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
        animation.setContainers(tableContainer, null, filterContainer);
    }

    private void initTable() {
        name = new DynamicColumn<>(
                lang.employeeEmployeeFullNameColumnHeader(),
                "employee-info",
                this::getEmployeeInfoBlock
        );
        contacts = new DynamicColumn<>(
                lang.contactInfo(),
                "employee-contacts",
                this::getEmployeeContactsBlock
        );
        department = new DynamicColumn<>(
                lang.department(),
                "employee-department",
                this::getEmployeeDepartmentBlock
        );

        table.addColumn(name.header, name.values);
        table.addColumn(contacts.header, contacts.values);
        table.addColumn(department.header, department.values);
    }

    private String getEmployeeInfoBlock(EmployeeShortView employee) {
        Element employeeInfo = DOM.createDiv();
        employeeInfo.appendChild(buildElement("fa fa-user-circle", employee.getDisplayName()));
        employeeInfo.appendChild(buildElement("fa fa-birthday-cake", DateFormatter.formatDateMonth(employee.getBirthday())));

        return employeeInfo.getString();
    }

    private String getEmployeeContactsBlock(EmployeeShortView employee) {
        Element employeeContacts = DOM.createDiv();

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(employee.getContactInfo());
        String phones = infoFacade.allPhonesAsString();
        String emails = infoFacade.allEmailsAsString();

        if (!phones.isEmpty()) {
            employeeContacts.appendChild(buildElement("fa fa-phone", phones));
        }

        if (!emails.isEmpty()) {
            employeeContacts.appendChild(buildElement("fa fa-envelope", emails));
        }

        return employeeContacts.getString();
    }

    private String getEmployeeDepartmentBlock(EmployeeShortView employee) {
        Element employeeDepartment = DOM.createDiv();
        employeeDepartment.addClassName("department");
        Element department = DOM.createDiv();
        Element departmentParent = DOM.createDiv();

        WorkerEntryFacade entryFacade = new WorkerEntryFacade( employee.getWorkerEntries() );
        WorkerEntryShortView mainEntry = entryFacade.getMainEntry();

        if (mainEntry != null) {
            if (mainEntry.getDepartmentParentName() != null) {
                departmentParent.appendChild(buildElement("fa fa-sitemap", mainEntry.getDepartmentParentName()));
                department.appendChild(buildElement("fa fa-th-large", mainEntry.getDepartmentName()));

                employeeDepartment.appendChild(departmentParent);
            } else {
                department.appendChild(buildElement("fa fa-sitemap", mainEntry.getDepartmentName()));
            }
            employeeDepartment.appendChild(department);
        }

        return employeeDepartment.getString();
    }

    private Element buildElement(String iconClass, String contacts) {
        Element data = DOM.createSpan();
        data.setInnerText(contacts);
        return buildElement(iconClass, data);
    }

    private Element buildElement(String iconClass, Element element) {
        Element icon = DOM.createElement("i");
        icon.addClassName(iconClass);

        Element wrapper = DOM.createDiv();
        wrapper.addClassName("contacts");
        wrapper.appendChild(icon);
        wrapper.appendChild(element);

        return wrapper;
    }

    @UiField
    InfiniteTableWidget<EmployeeShortView> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel filterContainer;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    Lang lang;


    ClickColumnProvider<EmployeeShortView> columnProvider = new ClickColumnProvider<>();
    DynamicColumn<EmployeeShortView> name;
    DynamicColumn<EmployeeShortView> contacts;
    DynamicColumn<EmployeeShortView> department;

    AbstractEmployeeTableActivity activity;

    private static EmployeeListViewUiBinder ourUiBinder = GWT.create(EmployeeListViewUiBinder.class);

    interface EmployeeListViewUiBinder extends UiBinder<HTMLPanel, EmployeeTableView> {
    }
}