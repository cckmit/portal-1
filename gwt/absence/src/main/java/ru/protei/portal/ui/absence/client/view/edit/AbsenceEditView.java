package ru.protei.portal.ui.absence.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.absence.client.activity.edit.AbstractAbsenceEditActivity;
import ru.protei.portal.ui.absence.client.activity.edit.AbstractAbsenceEditView;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.ui.absence.client.widget.schedule.create.ScheduleCreateWidget;
import ru.protei.portal.ui.absence.client.widget.schedule.list.ScheduleListWidget;
import ru.protei.portal.ui.common.client.events.ApplyEvent;
import ru.protei.portal.ui.common.client.events.RejectEvent;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.common.client.widget.selector.absencereason.AbsenceReasonButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.switcher.Switcher;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.List;

public class AbsenceEditView extends Composite implements AbstractAbsenceEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractAbsenceEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<DateInterval> dateRange() {
        return dateRange;
    }

    @Override
    public HasValue<PersonShortView> employee() {
        return employee;
    }

    @Override
    public HasValue<En_AbsenceReason> reason() {
        return reason;
    }

    @Override
    public HasValue<String> comment() {
        return comment;
    }

    @Override
    public HasVisibility contentVisibility() {
        return content;
    }

    @Override
    public HasVisibility loadingVisibility() {
        return loading;
    }

    @Override
    public HasEnabled employeeEnabled() {
        return employee;
    }

    @Override
    public HasEnabled reasonEnabled() {
        return reason;
    }

    @Override
    public HasValidable employeeValidator() {
        return employee;
    }

    @Override
    public HasValidable reasonValidator() {
        return reason;
    }

    @Override
    public void setDateRangeValid(boolean isValid) {
        dateRange.markInputValid(isValid);
    }

    @Override
    public HasVisibility scheduleVisibility() {
        return scheduleContainer;
    }

    @Override
    public HasVisibility scheduleCreateVisibility() {
        return scheduleCreateWidget;
    }

    @Override
    public HasValue<Boolean> enableSchedule() {
        return enableSchedule;
    }

    @Override
    public HasEnabled enableScheduleEnabled() {
        return enableSchedule;
    }

    @Override
    public HasValue<List<ScheduleItem>> scheduleItems() {
        return scheduleListWidget;
    }

    @UiHandler("dateRange")
    public void onDateRangeChanged(ValueChangeEvent<DateInterval> event) {
        activity.onDateRangeChanged();
    }

    @UiHandler("enableSchedule")
    public void onEnableScheduleChanged(ValueChangeEvent<Boolean> event) {
        activity.onEnableScheduleChanged();
    }

    @UiHandler("reason")
    public void onReasonChanged(ValueChangeEvent<En_AbsenceReason> event) {
        activity.onReasonChanged();
    }

    @UiHandler("createSchedule")
    public void onCreateScheduleButtonClicked(ClickEvent event) {
        scheduleCreateWidget.resetView();
        scheduleCreateWidget.setVisible(true);
    }

    @UiHandler("scheduleCreateWidget")
    public void onApplyScheduleButtonClicked(ApplyEvent<ScheduleItem> event) {
        scheduleCreateWidget.setVisible(false);
        scheduleListWidget.addItem(event.getTarget());
    }

    @UiHandler("scheduleCreateWidget")
    public void onRejectScheduleButtonClicked(RejectEvent event) {
        scheduleCreateWidget.setVisible(false);
    }

    protected void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        absenceEmployeeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.EMPLOYEE_SELECTOR_LABEL);
        employee.setEnsureDebugId(DebugIds.ABSENCE.EMPLOYEE_SELECTOR);
        absenceDateRangeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.DATE_RANGE_LABEL);
        absenceReasonLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.REASON_SELECTOR_LABEL);
        reason.setEnsureDebugId(DebugIds.ABSENCE.REASON_SELECTOR);
        absenceCommentLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.COMMENT_LABEL);
        comment.ensureDebugId(DebugIds.ABSENCE.COMMENT_INPUT);
        dateRange.setEnsureDebugId(DebugIds.ABSENCE.DATE_RANGE_INPUT);
        dateRange.getRelative().ensureDebugId(DebugIds.ABSENCE.DATE_RANGE_BUTTON);
    }

    @Inject
    @UiField(provided = true)
    RangePicker dateRange;
    @UiField
    LabelElement absenceEmployeeLabel;
    @UiField
    LabelElement absenceDateRangeLabel;
    @UiField
    LabelElement absenceReasonLabel;
    @UiField
    LabelElement absenceCommentLabel;
    @Inject
    @UiField(provided = true)
    EmployeeButtonSelector employee;
    @Inject
    @UiField(provided = true)
    AbsenceReasonButtonSelector reason;
    @UiField
    AutoResizeTextArea comment;
    @UiField
    HTMLPanel content;
    @UiField
    IndeterminateCircleLoading loading;

    @UiField
    Lang lang;
    @UiField
    Switcher enableSchedule;
    @UiField
    HTMLPanel scheduleContainer;
    @UiField
    Button createSchedule;
    @UiField
    ScheduleCreateWidget scheduleCreateWidget;
    @UiField
    ScheduleListWidget scheduleListWidget;

    private AbstractAbsenceEditActivity activity;

    private static AbsenceEditViewUiBinder ourUiBinder = GWT.create(AbsenceEditViewUiBinder.class);
    interface AbsenceEditViewUiBinder extends UiBinder<HTMLPanel, AbsenceEditView> {}
}