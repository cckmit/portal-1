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
import ru.protei.portal.core.model.ent.EmployeeRegistration;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.animation.TableAnimation;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_CaseStateLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.employeeregistration.client.activity.table.AbstractEmployeeRegistrationTableActivity;
import ru.protei.portal.ui.employeeregistration.client.activity.table.AbstractEmployeeRegistrationTableView;
import ru.protei.winter.core.utils.beans.SearchResult;

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

        table.setLoadHandler(activity);
    }

    @Override
    public void setAnimation(TableAnimation animation) {
        animation.setContainers(tableContainer, previewContainer, filterContainer);
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

    private void initTable() {
        ClickColumn<EmployeeRegistration> state = new ClickColumn<EmployeeRegistration>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.addClassName("number");
                columnHeader.setInnerText(lang.employeeRegistrationNumberColumn());
            }

            @Override
            public void fillColumnValue(Element cell, EmployeeRegistration value) {
                if (value.getState() == null) {
                    cell.setInnerText("");
                    return;
                }

                cell.addClassName("number");
                cell.setInnerHTML( StringUtils.join(
                        "<div>",
                        "<p class='number-size'>", String.valueOf( value.getId() ), "</p>",
                        "<p class='label label-", value.getState().toString().toLowerCase(), "'>", caseStateLang.getStateName( value.getState() ), "</p>",
                        "</div>"
                ).toString() );
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
                columnHeader.setInnerText(lang.employeeRegistrationHeadOfDepartmentColumnHeader());
            }

            @Override
            public void fillColumnValue(Element cell, EmployeeRegistration value) {
                if (value.getHeadOfDepartment() == null) {
                    cell.setInnerText("");
                    return;
                }
                cell.setInnerText(value.getHeadOfDepartment().getDisplayShortName());
            }
        };

        ClickColumn<EmployeeRegistration> employmentDate = new ClickColumn<EmployeeRegistration>() {
            @Override
            protected void fillColumnHeader(Element columnHeader) {
                columnHeader.setInnerText(lang.employeeRegistrationEmploymentDateColumnHeader());
            }

            @Override
            public void fillColumnValue(Element cell, EmployeeRegistration value) {
                cell.setInnerText(DateFormatter.formatDateOnly(value.getEmploymentDate()));
            }
        };

        clickColumns.add(state);
        clickColumns.add(fullName);
        clickColumns.add(headOfDepartment);
        clickColumns.add(employmentDate);

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
    En_CaseStateLang caseStateLang;

    private ClickColumnProvider<EmployeeRegistration> columnProvider = new ClickColumnProvider<>();

    private List<ClickColumn<EmployeeRegistration>> clickColumns = new LinkedList<>();

    private static TableViewUiBinder ourUiBinder = GWT.create(TableViewUiBinder.class);
    interface TableViewUiBinder extends UiBinder<HTMLPanel, EmployeeRegistrationTableView> {
    }
}
