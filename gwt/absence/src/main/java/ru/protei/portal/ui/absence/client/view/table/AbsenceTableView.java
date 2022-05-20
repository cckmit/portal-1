package ru.protei.portal.ui.absence.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.ui.absence.client.activity.table.AbstractAbsenceTableActivity;
import ru.protei.portal.ui.absence.client.activity.table.AbstractAbsenceTableView;
import ru.protei.portal.ui.absence.client.util.AccessUtil;
import ru.protei.portal.ui.absence.client.util.ScheduleFormatterClient;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AbsenceTableView extends Composite implements AbstractAbsenceTableView {

    @Inject
    public void onInit(EditClickColumn<PersonAbsence> editClickColumn, RemoveClickColumn<PersonAbsence> removeClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractAbsenceTableActivity activity) {
        this.activity = activity;

        completeClickColumn.setHandler(activity);
        completeClickColumn.setActionHandler(new ClickColumn.Handler<PersonAbsence>() {
            public void onItemClicked(PersonAbsence value) {
                activity.onCompleteAbsence(value);
            }
            public void onItemClicked(PersonAbsence value, com.google.gwt.dom.client.Element target) {}
        });
        completeClickColumn.setColumnProvider(columnProvider);

        editClickColumn.setHandler(activity);
        editClickColumn.setEditHandler(activity);
        editClickColumn.setColumnProvider(columnProvider);

        removeClickColumn.setHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setColumnProvider(columnProvider);

        columns.forEach(clickColumn -> {
            clickColumn.setHandler(activity);
            clickColumn.setColumnProvider(columnProvider);
        });
    }

    @Override
    public void clearRecords() {
        table.clearRows();
    }

    @Override
    public void addRecords(List<PersonAbsence> absences) {
        absences.forEach(absence -> table.addRow(absence));
    }

    private void initTable() {

        completeClickColumn = new ActionIconClickColumn<>("far fa-lg fa-check-circle", lang.absenceComplete(), "complete");
        completeClickColumn.setDisplayPredicate(value -> AccessUtil.isAllowedEdit(policyService, value));
        completeClickColumn.setEnabledPredicate(value -> new Date().after(value.getFromTime()) && new Date().before(value.getTillTime()));
        editClickColumn.setDisplayPredicate(value -> AccessUtil.isAllowedEdit(policyService, value));
        editClickColumn.setEnabledPredicate(value -> !value.isCreatedFrom1C());
        removeClickColumn.setDisplayPredicate(value -> AccessUtil.isAllowedRemove(policyService, value));
        removeClickColumn.setEnabledPredicate(value -> !value.isCreatedFrom1C());

        columns.add(reason);
        columns.add(absenceRange);
        columns.add(comment);

        table.addColumn(absenceRange.header, absenceRange.values);
        table.addColumn(reason.header, reason.values);
        table.addColumn(comment.header, comment.values);
        table.addColumn(completeClickColumn.header, completeClickColumn.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);

    }

    ClickColumn<PersonAbsence> reason = new ClickColumn<PersonAbsence>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.absenceReason());
        }

        @Override
        public void fillColumnValue(Element cell, PersonAbsence value) {
            cell.setInnerHTML(reasonLang.getName(value.getReason()));
        }
    };

    ClickColumn<PersonAbsence> absenceRange = new ClickColumn<PersonAbsence>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.absenceRange());
        }

        @Override
        public void fillColumnValue(Element cell, PersonAbsence value) {
            final StringBuilder valueBuilder = new StringBuilder(lang.absenceTimeRange(DateFormatter.formatDateTime(value.getFromTime()), DateFormatter.formatDateTime(value.getTillTime())));
            if (CollectionUtils.isNotEmpty(value.getScheduleItems())) {
                valueBuilder.append(" ")
                        .append(lang.absenceOnSchedule())
                        .append(": ")
                        .append(ScheduleFormatterClient.getSchedule(value.getScheduleItems()));
            }
            cell.setInnerHTML(valueBuilder.toString());
        }
    };

    ClickColumn<PersonAbsence> comment = new ClickColumn<PersonAbsence>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.absenceComment());
        }

        @Override
        public void fillColumnValue(Element cell, PersonAbsence value) {
            cell.setInnerHTML(value.getUserComment());
        }
    };

    @UiField
    TableWidget table;

    @Inject
    @UiField
    Lang lang;

    @Inject
    En_AbsenceReasonLang reasonLang;

    @Inject
    PolicyService policyService;

    AbstractAbsenceTableActivity activity;
    ActionIconClickColumn<PersonAbsence> completeClickColumn;
    EditClickColumn<PersonAbsence> editClickColumn;
    RemoveClickColumn<PersonAbsence> removeClickColumn;
    ClickColumnProvider<PersonAbsence> columnProvider = new ClickColumnProvider<>();
    List<ClickColumn> columns = new ArrayList<>();

    private static AbsenceTableViewUiBinder ourUiBinder = GWT.create(AbsenceTableViewUiBinder.class);
    interface AbsenceTableViewUiBinder extends UiBinder<HTMLPanel, AbsenceTableView> {}
}