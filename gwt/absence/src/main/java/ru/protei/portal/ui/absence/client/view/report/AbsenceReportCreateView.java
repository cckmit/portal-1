package ru.protei.portal.ui.absence.client.view.report;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.absence.client.activity.report.AbstractAbsenceReportCreateActivity;
import ru.protei.portal.ui.absence.client.activity.report.AbstractAbsenceReportCreateView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.absencereason.AbsenceReasonMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;

import java.util.Set;

public class AbsenceReportCreateView extends Composite implements AbstractAbsenceReportCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        dateRange.setPlaceholder(lang.selectDate());
        dateRange.setHasPredefinedPeriods(true);
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractAbsenceReportCreateActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<String> name() {
        return name;
    }

    @Override
    public HasValue<DateInterval> dateRange() {
        return dateRange;
    }

    @Override
    public HasValue<Set<PersonShortView>> employees() {
        return employees;
    }

    @Override
    public HasValue<Set<En_AbsenceReason>> reasons() {
        return reasons;
    }

    @Override
    public HasValue<En_SortField> sortField() {
        return sortField;
    }

    @Override
    public HasValue<Boolean> sortDir() {
        return sortDir;
    }

    private void ensureDebugIds() {
        absenceReportTitleLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE_REPORT.NAME_LABEL);
        name.ensureDebugId(DebugIds.ABSENCE_REPORT.NAME_INPUT);
        absenceReportDateRangeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE_REPORT.DATE_RANGE_LABEL);
        dateRange.setEnsureDebugId(DebugIds.ABSENCE_REPORT.DATE_RANGE_INPUT);
        dateRange.getRelative().ensureDebugId(DebugIds.ABSENCE_REPORT.DATE_RANGE_BUTTON);
        employees.setAddEnsureDebugId(DebugIds.ABSENCE_REPORT.EMPLOYEE_SELECTOR_ADD_BUTTON);
        employees.setClearEnsureDebugId(DebugIds.ABSENCE_REPORT.EMPLOYEE_SELECTOR_CLEAR_BUTTON);
        employees.setItemContainerEnsureDebugId(DebugIds.ABSENCE_REPORT.EMPLOYEE_SELECTOR_ITEM_CONTAINER);
        employees.setLabelEnsureDebugId(DebugIds.ABSENCE_REPORT.EMPLOYEE_SELECTOR_LABEL);
        reasons.setAddEnsureDebugId(DebugIds.ABSENCE_REPORT.REASON_SELECTOR_ADD_BUTTON);
        reasons.setClearEnsureDebugId(DebugIds.ABSENCE_REPORT.REASON_SELECTOR_CLEAR_BUTTON);
        reasons.setItemContainerEnsureDebugId(DebugIds.ABSENCE_REPORT.REASON_SELECTOR_ITEM_CONTAINER);
        reasons.setLabelEnsureDebugId(DebugIds.ABSENCE_REPORT.REASON_SELECTOR_LABEL);
        absenceReportSortByLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE_REPORT.SORT_FIELD_LABEL);
        sortField.setEnsureDebugId(DebugIds.ABSENCE_REPORT.SORT_FIELD_SELECTOR);
        sortDir.ensureDebugId(DebugIds.ABSENCE_REPORT.SORT_DIR_BUTTON);
    }

    @UiField
    LabelElement absenceReportTitleLabel;
    @UiField
    TextBox name;
    @UiField
    LabelElement absenceReportDateRangeLabel;
    @Inject
    @UiField(provided = true)
    RangePicker dateRange;
    @Inject
    @UiField(provided = true)
    EmployeeMultiSelector employees;
    @Inject
    @UiField(provided = true)
    AbsenceReasonMultiSelector reasons;
    @UiField
    LabelElement absenceReportSortByLabel;
    @Inject
    @UiField(provided = true)
    SortFieldSelector sortField;
    @UiField
    ToggleButton sortDir;
    @Inject
    @UiField
    Lang lang;

    private AbstractAbsenceReportCreateActivity activity;

    private static AbsenceReportCreateViewUiBinder ourUiBinder = GWT.create(AbsenceReportCreateViewUiBinder.class);
    interface AbsenceReportCreateViewUiBinder extends UiBinder<HTMLPanel, AbsenceReportCreateView> {}
}