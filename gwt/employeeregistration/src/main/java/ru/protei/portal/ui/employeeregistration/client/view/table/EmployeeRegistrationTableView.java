package ru.protei.portal.ui.employeeregistration.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ActionIconClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.EditClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.employeeregistration.client.activity.table.AbstractEmployeeRegistrationTableActivity;
import ru.protei.portal.ui.employeeregistration.client.activity.table.AbstractEmployeeRegistrationTableView;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;

public class EmployeeRegistrationTableView extends Composite implements AbstractEmployeeRegistrationTableView {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        initTable();
    }

    @Override
    public void setActivity(AbstractEmployeeRegistrationTableActivity activity) {
        clickColumns.forEach(col -> {
            col.setHandler(activity);
            col.setColumnProvider(columnProvider);
        });

        editClickColumn.setEditHandler(activity);

        completeProbationClickColumn.setHandler(activity);
        completeProbationClickColumn.setActionHandler(activity::onCompleteProbationClicked);
        completeProbationClickColumn.setColumnProvider(columnProvider);

        table.setLoadHandler(activity);
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
        columnProvider.setChangeSelectionIfSelectedPredicate(employeeRegistration -> animation.isPreviewShow());
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
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
    public HasWidgets getPreviewContainer() {
        return previewContainer;
    }

    @Override
    public HTMLPanel getFilterContainer() {
        return filterContainer;
    }

    @Override
    public void clearSelection() {
        columnProvider.removeSelection();
    }

    @Override
    public void updateRow(EmployeeRegistration employeeRegistration) {
        if (employeeRegistration != null) {
            table.updateRow(employeeRegistration);
        }
    }

    private void initTable() {
        ClickColumn<EmployeeRegistration> state = new ClickColumn<EmployeeRegistration>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("number");
                columnHeader.setInnerText(lang.employeeRegistrationNumberColumn());
            }

            @Override
            public void fillColumnValue(Element cell, EmployeeRegistration value) {
                if (value.getStateId() == null) {
                    cell.setInnerText("");
                    return;
                }

                cell.addClassName("number");
                cell.setInnerHTML(StringUtils.join(
                        "<div>",
                        "<p class='number-size'>", String.valueOf(value.getId()), "</p>",
                        "<p class='label' style='background-color:", value.getStateColor() , "'>", value.getStateName(), "</p>",
                        "</div>"
                ).toString());
            }
        };

        ClickColumn<EmployeeRegistration> fullName = new ClickColumn<EmployeeRegistration>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.setInnerText(lang.employeeRegistrationEmployeeFullNameColumnHeader());
            }

            @Override
            public void fillColumnValue(Element cell, EmployeeRegistration value) {
                cell.setInnerText(value.getEmployeeFullName());
            }
        };

        ClickColumn<EmployeeRegistration> headOfDepartment = new ClickColumn<EmployeeRegistration>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("head-of-department");
                columnHeader.setInnerText(lang.employeeRegistrationHeadOfDepartmentColumnHeader());
            }

            @Override
            public void fillColumnValue(Element cell, EmployeeRegistration value) {
                if (value.getHeadOfDepartment() == null) {
                    cell.setInnerText("");
                    return;
                }
                cell.setInnerText(value.getHeadOfDepartment().getName());
            }
        };

        ClickColumn<EmployeeRegistration> employmentDate = new ClickColumn<EmployeeRegistration>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("employment-date");
                columnHeader.setInnerText(lang.employeeRegistrationEmploymentDateColumnHeader());
            }

            @Override
            public void fillColumnValue(Element cell, EmployeeRegistration value) {
                cell.setInnerText(DateFormatter.formatDateOnly(value.getEmploymentDate()));
            }
        };

        completeProbationClickColumn = new ActionIconClickColumn<>(
                "far fa-lg fa-check-circle", lang.completeProbation(), "complete");
        completeProbationClickColumn.setDisplayPredicate(
                v -> policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_REGISTRATION_EDIT));
        completeProbationClickColumn.setEnabledPredicate(
                v -> v.getProbationPeriodEndDate() != null && new Date().before(v.getProbationPeriodEndDate()));

        editClickColumn.setEnabledPredicate(employeeShortView -> policyService.hasPrivilegeFor(En_Privilege.EMPLOYEE_REGISTRATION_EDIT));

        clickColumns.add(state);
        clickColumns.add(fullName);
        clickColumns.add(headOfDepartment);
        clickColumns.add(employmentDate);
        clickColumns.add(editClickColumn);
        clickColumns.add(completeProbationClickColumn);

        clickColumns.forEach(c -> table.addColumn(c.header, c.values));
    }

    @UiField
    InfiniteTableWidget<EmployeeRegistration> table;

    @UiField
    HTMLPanel tableContainer;
    @UiField
    HTMLPanel previewContainer;
    @UiField
    HTMLPanel filterContainer;

    @Inject
    @UiField
    Lang lang;

    @Inject
    EditClickColumn<EmployeeRegistration> editClickColumn;

    @Inject
    PolicyService policyService;

    ActionIconClickColumn<EmployeeRegistration> completeProbationClickColumn;

    private ClickColumnProvider<EmployeeRegistration> columnProvider = new ClickColumnProvider<>();

    private List<ClickColumn<EmployeeRegistration>> clickColumns = new LinkedList<>();

    private static TableViewUiBinder ourUiBinder = GWT.create(TableViewUiBinder.class);
    interface TableViewUiBinder extends UiBinder<HTMLPanel, EmployeeRegistrationTableView> {
    }
}
