package ru.protei.portal.ui.absence.client.view.summarytable;

import com.google.gwt.core.client.GWT;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.i18n.shared.DateTimeFormat;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.google.inject.Inject;
import ru.brainworm.factory.widget.table.client.InfiniteTableWidget;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.dto.TimeInterval;
import ru.protei.portal.core.model.ent.PersonAbsence;
import ru.protei.portal.core.model.helper.AbsenceUtils;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.DateUtils;
import ru.protei.portal.ui.absence.client.activity.summarytable.AbstractAbsenceSummaryTableActivity;
import ru.protei.portal.ui.absence.client.activity.summarytable.AbstractAbsenceSummaryTableView;
import ru.protei.portal.ui.absence.client.util.AccessUtil;
import ru.protei.portal.ui.absence.client.util.ScheduleFormatterClient;
import ru.protei.portal.ui.absence.client.widget.filter.AbsenceFilterWidget;
import ru.protei.portal.ui.absence.client.widget.filter.AbsenceFilterWidgetModel;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.columns.*;
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
        editClickColumn.setEnabledPredicate(value -> !value.isCreatedFrom1C());

        removeClickColumn.setHandler(activity);
        removeClickColumn.setRemoveHandler(activity);
        removeClickColumn.setColumnProvider(columnProvider);
        removeClickColumn.setEnabledPredicate(value -> !value.isCreatedFrom1C());

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
                value -> reasonLang.getName(value.getReason()));

        DynamicColumn<PersonAbsence> person = new DynamicColumn<>(lang.absenceEmployee(), "person",
                value -> value.getPerson().getName());

        DynamicColumn<PersonAbsence> range = new DynamicColumn<>(lang.absenceRange(), "range",
                value -> {
                    StringBuilder valueBuilder = new StringBuilder("<div class='m-b-5'><b>" +
                            prettyDateRangeFormat(value.getFromTime(), value.getTillTime()) +
                            "</b>");

                    if (value.isScheduledAbsence()) {
                        boolean isActive = AbsenceUtils.checkScheduledAbsenceActiveTodayWithoutTimeCheck(value);
                        valueBuilder.append(isActive ? wrapBadge(lang.absenceScheduleActive(), CrmConstants.Style.SUCCESS + " m-l-10") : wrapBadge(lang.absenceScheduleInactive(), CrmConstants.Style.DANGER + " m-l-10 "))
                                .append("</div>");
                        for (ScheduleItem scheduleItem : value.getScheduleItems()) {
                            valueBuilder.append("<div class='m-t-5'><span class='m-r-10'>")
                                    .append(ScheduleFormatterClient.getDays(scheduleItem))
                                    .append("</span>");
                            for (TimeInterval timeInterval : scheduleItem.getTimes()) {
                                valueBuilder.append(wrapBadge(ScheduleFormatterClient.formatTimePeriod(timeInterval)));
                            }
                            valueBuilder.append("</div>");
                        }
                    } else {
                        valueBuilder.append("</div>").append(wrapBadge(timeFormat.format(value.getFromTime()) + " – " + timeFormat.format(value.getTillTime())));
                    }

                    return valueBuilder.toString();
                });

        DynamicColumn<PersonAbsence> comment = new DynamicColumn<>(lang.absenceComment(), "comment",
                PersonAbsence::getUserComment);

        columns.add(reason);
        columns.add(person);
        columns.add(range);
        columns.add(comment);

        table.addColumn(reason.header, reason.values);
        table.addColumn(person.header, person.values);
        table.addColumn(range.header, range.values);

        table.addColumn(comment.header, comment.values);
        table.addColumn(completeClickColumn.header, completeClickColumn.values);
        table.addColumn(editClickColumn.header, editClickColumn.values);
        table.addColumn(removeClickColumn.header, removeClickColumn.values);
    }

    private String prettyDateRangeFormat(Date from, Date to) {
        if (DateUtils.isSameDay(from, to)) {
            return dateMonthYearFormat.format(from);
        }

        StringBuilder sb = new StringBuilder();
        if (DateUtils.isSameYear(from, to)) {
            sb.append(DateUtils.isSameMonth(from, to) ? from.getDate() : dateMonthFormat.format(from));
        }
        sb.append(" – ")
                .append(dateMonthYearFormat.format(to));
        return sb.toString();
    }

    private String wrapBadge(String text, String style) {
        return "<span class='badge badge-" + style + "'>"
                + text + "</span>";
    }

    private String wrapBadge(String text) {
        return wrapBadge(text, CrmConstants.Style.DEFAULT);
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

    private DateRange selectedDateRange = null;
    private DateTimeFormat dateMonthFormat = DateTimeFormat.getFormat(LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().formatMonthFullDay());
    private DateTimeFormat dateMonthYearFormat = DateTimeFormat.getFormat(LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().formatYearMonthFullDay());
    private DateTimeFormat timeFormat = DateTimeFormat.getFormat(LocaleInfo.getCurrentLocale().getDateTimeFormatInfo().timeFormatShort());

    private static AbsenceFullTableViewUiBinder ourUiBinder = GWT.create(AbsenceFullTableViewUiBinder.class);
    interface AbsenceFullTableViewUiBinder extends UiBinder<HTMLPanel, AbsenceSummaryTableView> {}
}