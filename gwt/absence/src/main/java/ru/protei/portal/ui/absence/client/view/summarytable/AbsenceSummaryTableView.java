package ru.protei.portal.ui.absence.client.view.summarytable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.ui.absence.client.activity.summarytable.AbstractAbsenceSummaryTableActivity;
import ru.protei.portal.ui.absence.client.activity.summarytable.AbstractAbsenceSummaryTableView;
import ru.protei.portal.ui.absence.client.util.AccessUtil;
import ru.protei.portal.ui.absence.client.widget.filter.AbsenceFilterWidget;
import ru.protei.portal.ui.absence.client.widget.filter.AbsenceFilterWidgetModel;
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

        filterWidget.setOnFilterChangeCallback(activity::onFilterChange);

        completeClickColumn.setHandler(activity);
        completeClickColumn.setActionHandler(value -> activity.onCompleteAbsence(value));
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

        table.setLoadHandler( activity );
        table.setPagerListener( activity );
    }

    @Override
    public AbsenceFilterWidget getFilterWidget() {
        return filterWidget;
    }

    @Override
    public void clearRecords() {
        table.clearCache();
        table.clearRows();
    }

    @Override
    public HasWidgets getPagerContainer() {
        return pagerContainer;
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
    public void scrollTo( int page ) {
        table.scrollToPage( page );
    }

    @Override
    public void triggerTableLoad() {
        table.setTotalRecords(table.getPageSize());
    }

    private void initTable() {

        completeClickColumn = new ActionIconClickColumn<>("far fa-lg fa-check-circle", lang.absenceComplete(), "complete");
        completeClickColumn.setDisplayPredicate(value -> AccessUtil.isAllowedEdit(policyService, value));
        completeClickColumn.setEnabledPredicate(value -> new Date().after(value.getFromTime()) && new Date().before(value.getTillTime()));
        editClickColumn.setDisplayPredicate(value -> AccessUtil.isAllowedEdit(policyService, value));
        removeClickColumn.setDisplayPredicate(value -> AccessUtil.isAllowedRemove(policyService, value));

        DynamicColumn<PersonAbsence> reason = new DynamicColumn<>(lang.absenceReason(), "reason",
                value -> "<div class=\"absence-reason\"><i class=\"" + reasonLang.getIcon(value.getReason()) + "\"></i></div>" +
                        "<span class=\"absence-label\">" + reasonLang.getName(value.getReason()) +"</span>");

        DynamicColumn<PersonAbsence> person = new DynamicColumn<>(lang.absenceEmployee(), "person",
                value -> value.getPerson().getName());

        DynamicColumn<PersonAbsence> fromTime = new DynamicColumn<>(lang.absenceFromTime(), "from-time",
                value -> DateFormatter.formatDateTime(value.getFromTime()));

        DynamicColumn<PersonAbsence> tillTime = new DynamicColumn<>(lang.absenceTillTime(), "till-time",
                value -> DateFormatter.formatDateTime(value.getTillTime()));

        DynamicColumn<PersonAbsence> comment = new DynamicColumn<>(lang.absenceComment(), "comment",
                value -> value.getUserComment());

        columns.add(reason);
        columns.add(person);
        columns.add(fromTime);
        columns.add(tillTime);
        columns.add(comment);

        table.addColumn(reason.header, reason.values);
        table.addColumn(person.header, person.values);
        table.addColumn(fromTime.header, fromTime.values);
        table.addColumn(tillTime.header, tillTime.values);
        table.addColumn(comment.header, comment.values);
        table.addColumn(completeClickColumn.header, completeClickColumn.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    @UiField
    InfiniteTableWidget<PersonAbsence> table;
    @UiField
    HTMLPanel pagerContainer;

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