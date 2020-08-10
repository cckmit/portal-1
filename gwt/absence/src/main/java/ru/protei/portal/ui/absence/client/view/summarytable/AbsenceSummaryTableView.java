package ru.protei.portal.ui.absence.client.view.summarytable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.ui.absence.client.activity.summarytable.AbstractAbsenceSummaryTableActivity;
import ru.protei.portal.ui.absence.client.activity.summarytable.AbstractAbsenceSummaryTableView;
import ru.protei.portal.ui.absence.client.util.AccessUtil;
import ru.protei.portal.ui.absence.client.widget.AbsenceFilterWidget;
import ru.protei.portal.ui.absence.client.widget.AbsenceFilterWidgetModel;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.columns.*;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class AbsenceSummaryTableView extends Composite implements AbstractAbsenceSummaryTableView {

    @Inject
    public void onInit(EditClickColumn<PersonAbsence> editClickColumn, RemoveClickColumn<PersonAbsence> removeClickColumn,
                       AbsenceFilterWidgetModel model) {
        filterWidget.onInit(model);
        initWidget(ourUiBinder.createAndBindUi(this));
        this.editClickColumn = editClickColumn;
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractAbsenceSummaryTableActivity activity) {
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
    public AbsenceFilterWidget getFilterWidget() {
        return filterWidget;
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
        removeClickColumn.setDisplayPredicate(value -> AccessUtil.isAllowedRemove(policyService, value));

        columns.add(reason);
        columns.add(fromTime);
        columns.add(tillTime);
        columns.add(comment);

        table.addColumn(fromTime.header, fromTime.values);
        table.addColumn(tillTime.header, tillTime.values);
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

    ClickColumn<PersonAbsence> fromTime = new ClickColumn<PersonAbsence>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.absenceFromTime());
        }

        @Override
        public void fillColumnValue(Element cell, PersonAbsence value) {
            cell.setInnerHTML(DateFormatter.formatDateTime(value.getFromTime()));
        }
    };

    ClickColumn<PersonAbsence> tillTime = new ClickColumn<PersonAbsence>() {
        @Override
        protected void fillColumnHeader(Element columnHeader) {
            columnHeader.setInnerText(lang.absenceTillTime());
        }

        @Override
        public void fillColumnValue(Element cell, PersonAbsence value) {
            cell.setInnerHTML(DateFormatter.formatDateTime(value.getTillTime()));
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
    TableWidget<PersonAbsence> table;

    @Inject
    @UiField(provided = true)
    AbsenceFilterWidget filterWidget;

    @Inject
    @UiField
    Lang lang;

    @Inject
    En_AbsenceReasonLang reasonLang;

    @Inject
    PolicyService policyService;

    AbstractAbsenceSummaryTableActivity activity;
    ActionIconClickColumn<PersonAbsence> completeClickColumn;
    EditClickColumn<PersonAbsence> editClickColumn;
    RemoveClickColumn<PersonAbsence> removeClickColumn;
    ClickColumnProvider<PersonAbsence> columnProvider = new ClickColumnProvider<>();
    List<ClickColumn<PersonAbsence>> columns = new ArrayList<>();

    private static AbsenceFullTableViewUiBinder ourUiBinder = GWT.create(AbsenceFullTableViewUiBinder.class);
    interface AbsenceFullTableViewUiBinder extends UiBinder<HTMLPanel, AbsenceSummaryTableView> {}
}