package ru.protei.portal.ui.absencereport.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.i18n.client.LocaleInfo;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.absencereport.client.activity.create.AbstractAbsenceReportCreateActivity;
import ru.protei.portal.ui.absencereport.client.activity.create.AbstractAbsenceReportCreateView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.absencereason.AbsenceReasonMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeMultiSelector;
import ru.protei.portal.ui.common.client.widget.selector.sortfield.SortFieldSelector;

import java.util.Set;
import java.util.stream.Collectors;

public class AbsenceReportCreateView extends Composite implements AbstractAbsenceReportCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        dateRange.setPlaceholder(lang.selectDate());
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractAbsenceReportCreateActivity activity) {
        this.activity = activity;
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

    @Override
    public void markDateRangeError() {
        dateRange.markInputValid(false);
    }

    @UiHandler("reportButton")
    public void reportButtonClick(ClickEvent event) {
        if (!dateRange.getValue().isValid()) return;
        Window.open(DOWNLOAD_PATH +
                "from_time=" + dateRange.getValue().from.getTime() +
                "&till_time=" + dateRange.getValue().to.getTime() +
                "&employees="+ employees.getValue().stream().map(PersonShortView::getId).map(Object::toString).collect(Collectors.joining(",")) +
                "&reasons="+ reasons.getValue().stream().map(En_AbsenceReason::getId).map(Object::toString).collect(Collectors.joining(",")) +
                "&sort_field=" + sortField.getValue() + "&sort_dir=" + (sortDir.getValue() ? En_SortDir.ASC : En_SortDir.DESC),
                "_blank", "");
    }

    @UiHandler("resetButton")
    public void resetButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onResetClicked();
        }
    }

    private void ensureDebugIds() {
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
        reportButton.ensureDebugId(DebugIds.ABSENCE_REPORT.REPORT_BUTTON);
        resetButton.ensureDebugId(DebugIds.ABSENCE_REPORT.RESET_BUTTON);
    }

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
    @UiField
    Button reportButton;
    @UiField
    Button resetButton;
    @Inject
    @UiField
    Lang lang;

    private AbstractAbsenceReportCreateActivity activity;

    private static final String DOWNLOAD_PATH = GWT.getModuleBaseURL() + "springApi/download/report?";

    private static AbsenceReportCreateViewUiBinder ourUiBinder = GWT.create(AbsenceReportCreateViewUiBinder.class);
    interface AbsenceReportCreateViewUiBinder extends UiBinder<HTMLPanel, AbsenceReportCreateView> {}
}