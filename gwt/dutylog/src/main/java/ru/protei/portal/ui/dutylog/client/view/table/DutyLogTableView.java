package ru.protei.portal.ui.dutylog.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.brainworm.factory.widget.table.client.helper.StaticTextColumn;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.ui.common.client.lang.DutyTypeLang;
import ru.protei.portal.ui.dutylog.client.activity.table.AbstractDutyLogTableActivity;
import ru.protei.portal.ui.dutylog.client.activity.table.AbstractDutyLogTableView;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.List;

public class DutyLogTableView extends Composite implements AbstractDutyLogTableView {

    @Inject
    public void onInit(EditClickColumn<DutyLog> editClickColumn, RemoveClickColumn<DutyLog> removeClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractDutyLogTableActivity activity) {
        this.activity = activity;

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
    }

    @Override
    public void clearRecords() {
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
    public void addRecords(List<DutyLog> value) {
       value.forEach(val -> table.addRow(val));
    }

    private void initTable() {
        editClickColumn.setDisplayPredicate(value -> policyService.hasPrivilegeFor(En_Privilege.DUTY_LOG_EDIT));

        StaticTextColumn<DutyLog> dateColumn = new StaticTextColumn<DutyLog>(lang.dutyLogRange()) {
            @Override
            public String getColumnValue(DutyLog dutyLog) {
                return DateFormatter.formatDateOnly(dutyLog.getFrom()) + " – " + DateFormatter.formatDateOnly(dutyLog.getTo());
            }
        };

        StaticTextColumn<DutyLog> employeeColumn = new StaticTextColumn<DutyLog>(lang.dutyLogEmployee()) {
            @Override
            public String getColumnValue(DutyLog dutyLog) {
                return StringUtils.emptyIfNull(dutyLog.getPersonDisplayName());
            }
        };

        StaticTextColumn<DutyLog> dutyColumn = new StaticTextColumn<DutyLog>(lang.dutyLogDuty()) {
            @Override
            public String getColumnValue(DutyLog dutyLog) {
                return StringUtils.emptyIfNull(dutyLog.getPersonDisplayName());
            }
        };

        StaticTextColumn<DutyLog> typeColumn = new StaticTextColumn<DutyLog>(lang.dutyLogType()) {
            @Override
            public String getColumnValue(DutyLog dutyLog) {
                return typeLang.getName(dutyLog.getType());
            }
        };

        table.addColumn(dateColumn.header, dateColumn.values);
        table.addColumn(employeeColumn.header, employeeColumn.values);
        table.addColumn(dutyColumn.header, dutyColumn.values);
        table.addColumn(typeColumn.header, typeColumn.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
    }

    @UiField
    Lang lang;
    @UiField
    HasWidgets filterContainer;
    @UiField
    TableWidget<DutyLog> table;
    @UiField
    HTMLPanel pagerContainer;

    @Inject
    PolicyService policyService;
    @Inject
    DutyTypeLang typeLang;

    AbstractDutyLogTableActivity activity;
    EditClickColumn<DutyLog> editClickColumn;

    private static DutyLogTableViewUiBinder ourUiBinder = GWT.create(DutyLogTableViewUiBinder.class);
    interface DutyLogTableViewUiBinder extends UiBinder<HTMLPanel, DutyLogTableView> {}
}