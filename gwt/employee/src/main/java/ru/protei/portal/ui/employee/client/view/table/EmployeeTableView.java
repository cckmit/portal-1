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
        additional.setHandler(activity);
        additional.setColumnProvider(columnProvider);
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
                lang.contactPosition(),
                "employee-department",
                this::getEmployeeDepartmentBlock
        );
        additional = new DynamicColumn<>(
                lang.employeeAdditionalInfoHeader(),
                "employee-additional",
                this::getEmployeeAdditionalBlock
        );

        table.addColumn(name.header, name.values);
        table.addColumn(contacts.header, contacts.values);
        table.addColumn(department.header, department.values);
        table.addColumn(additional.header, additional.values);
    }

    private String getEmployeeInfoBlock(EmployeeShortView employee) {
        Element employeeInfo = DOM.createDiv();

        if (employee.isFired()) employeeInfo.addClassName("fired");

        if (employee.isFired()){
            employeeInfo.appendChild(LabelValuePairBuilder.make()
                    .addIconValuePair("fa fa-ban text-danger", employee.getDisplayName(), "contacts fired")
                    .toElement());
        }
        else {
            employeeInfo.appendChild(LabelValuePairBuilder.make()
                    .addIconValuePair("fa fa-user-circle", employee.getDisplayName(), "contacts")
                    .toElement());
        }
        employeeInfo.appendChild(LabelValuePairBuilder.make()
                .addIconValuePair("fa fa-birthday-cake", DateFormatter.formatDateMonth(employee.getBirthday()), "contacts")
                .toElement());

        return employeeInfo.getString();
    }

    private String getEmployeeContactsBlock(EmployeeShortView employee) {
        Element employeeContacts = DOM.createDiv();

        if (employee.isFired()) employeeContacts.addClassName("fired");

        PlainContactInfoFacade infoFacade = new PlainContactInfoFacade(employee.getContactInfo());
        String phones = infoFacade.publicPhonesAsFormattedString(true);

        if (!phones.isEmpty()) {
            employeeContacts.appendChild(LabelValuePairBuilder.make()
                    .addIconValuePair("fa fa-phone", phones, "contacts")
                    .toElement());
        }

        if (!infoFacade.publicEmailsAsString().isEmpty())
            employeeContacts.appendChild(EmailRender
                    .renderToElement("fa fa-envelope", infoFacade.publicEmailsStream(), "contacts", false));


        return employeeContacts.getString();
    }

    private String getEmployeeDepartmentBlock(EmployeeShortView employee) {
        Element employeeDepartment = DOM.createDiv();

        if (employee.isFired()) employeeDepartment.addClassName("fired");

        employeeDepartment.addClassName("department");
        Element department = DOM.createDiv();
        Element departmentParent = DOM.createDiv();

        WorkerEntryFacade entryFacade = new WorkerEntryFacade( employee.getWorkerEntries() );
        WorkerEntryShortView mainEntry = entryFacade.getMainEntry();

        if (mainEntry != null) {
            if (mainEntry.getDepartmentParentName() != null) {
                departmentParent.appendChild(LabelValuePairBuilder.make()
                        .addIconValuePair("fa fa-sitemap", mainEntry.getDepartmentParentName(), "contacts")
                        .toElement());

                department.appendChild(LabelValuePairBuilder.make()
                        .addIconValuePair("fa fa-th-large", mainEntry.getDepartmentName(), "contacts")
                        .toElement());

                employeeDepartment.appendChild(departmentParent);
            } else {
                department.appendChild(LabelValuePairBuilder.make()
                        .addIconValuePair("fa fa-sitemap", mainEntry.getDepartmentName(), "contacts")
                        .toElement());
            }
            if (mainEntry.getPositionName() != null){
                department.appendChild(LabelValuePairBuilder.make()
                        .addIconValuePair("fa fa-street-view", mainEntry.getPositionName(), "contacts")
                        .toElement());
            }
            employeeDepartment.appendChild(department);
        }
        else if (employee.isFired()){
            department.appendChild(LabelValuePairBuilder.make()
                    .addIconValuePair("fa fa-info-circle", lang.employeeFired() + (employee.getFireDate() == null ? "" : " " + DateFormatter.formatDateOnly(employee.getFireDate())), "contacts")
                    .toElement());
            employeeDepartment.appendChild(department);
        }

        return employeeDepartment.getString();
    }

    private String getEmployeeAdditionalBlock(EmployeeShortView employee) {
        Element employeeAdditional = DOM.createDiv();

        if (employee.isFired()) employeeAdditional.addClassName("fired");

        employeeAdditional.appendChild(
                LabelValuePairBuilder.make()
                        .addLabelValuePair("IP", employee.getIpAddress(), "contacts")
                        .toElement());

        employeeAdditional.appendChild(
                LabelValuePairBuilder.make()
                        .addLabelValuePair("ID", String.valueOf(employee.getId()), "contacts")
                        .toElement());

        return employeeAdditional.getString();
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


    ClickColumnProvider<EmployeeShortView> columnProvider = new ClickColumnProvider<>();
    DynamicColumn<EmployeeShortView> name;
    DynamicColumn<EmployeeShortView> contacts;
    DynamicColumn<EmployeeShortView> department;
    DynamicColumn<EmployeeShortView> additional;

    AbstractEmployeeTableActivity activity;

    private static EmployeeListViewUiBinder ourUiBinder = GWT.create(EmployeeListViewUiBinder.class);

    interface EmployeeListViewUiBinder extends UiBinder<HTMLPanel, EmployeeTableView> {
    }
}