package ru.protei.portal.ui.absence.client.view.table;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.AbstractColumn;
import ru.brainworm.factory.widget.table.client.TableWidget;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.ui.absence.client.activity.table.AbstractAbsenceTableActivity;
import ru.protei.portal.ui.absence.client.activity.table.AbstractAbsenceTableView;
import ru.protei.portal.ui.common.client.columns.ClickColumn;
import ru.protei.portal.ui.common.client.columns.ClickColumnProvider;
import ru.protei.portal.ui.common.client.columns.RemoveClickColumn;
import ru.protei.portal.ui.common.client.common.DateFormatter;
import ru.protei.portal.ui.common.client.lang.En_AbsenceReasonLang;
import ru.protei.portal.ui.common.client.lang.Lang;

import java.util.ArrayList;
import java.util.List;

public class AbsenceTableView extends Composite implements AbstractAbsenceTableView {

    @Inject
    public void onInit(RemoveClickColumn<PersonAbsence> removeClickColumn) {
        initWidget(ourUiBinder.createAndBindUi(this));
        this.removeClickColumn = removeClickColumn;
        initTable();
    }

    @Override
    public void setActivity(AbstractAbsenceTableActivity activity) {
        this.activity = activity;

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

    @Override
    public void showRemoveColumn(boolean isVisible) {
        removeColumn.setVisibility(isVisible);
    }

    private void initTable() {

        columns.add(reason);
        columns.add(fromTime);
        columns.add(tillTime);
        columns.add(comment);

        table.addColumn(fromTime.header, fromTime.values);
        table.addColumn(tillTime.header, tillTime.values);
        table.addColumn(reason.header, reason.values);
        table.addColumn(comment.header, comment.values);
        removeColumn = table.addColumn(removeClickColumn.header, removeClickColumn.values);

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
    TableWidget table;

    @Inject
    @UiField
    Lang lang;

    @Inject
    En_AbsenceReasonLang reasonLang;

    AbstractAbsenceTableActivity activity;
    RemoveClickColumn<PersonAbsence> removeClickColumn;
    ClickColumnProvider<PersonAbsence> columnProvider = new ClickColumnProvider<>();
    List<ClickColumn> columns = new ArrayList<>();
    AbstractColumn removeColumn;

    private static AbsenceTableViewUiBinder ourUiBinder = GWT.create(AbsenceTableViewUiBinder.class);
    interface AbsenceTableViewUiBinder extends UiBinder<HTMLPanel, AbsenceTableView> {}
}