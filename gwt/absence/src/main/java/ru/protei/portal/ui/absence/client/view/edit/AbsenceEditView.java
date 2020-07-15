package ru.protei.portal.ui.absence.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.LabelElement;
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
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.selector.absencereason.AbsenceReasonButtonSelector;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.Date;

public class AbsenceEditView extends Composite implements AbstractAbsenceEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        dateRange.setPlaceholder(lang.selectDate());
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractAbsenceEditActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<PersonShortView> employee() {
        return employee;
    }

    @Override
    public HasValue<DateInterval> dateRange() {
        return dateRange;
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
    public HasEnabled dateRangeEnabled() {
        return dateRange;
    }

    @Override
    public HasEnabled reasonEnabled() {
        return reason;
    }

    @Override
    public HasEnabled commentEnabled() {
        return comment;
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

    @UiHandler("dateRange")
    public void onDateRangeChanged(ValueChangeEvent<DateInterval> event) {
        activity.onDateRangeChanged();
    }

    private void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        absenceEmployeeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.EMPLOYEE_SELECTOR_LABEL);
        employee.setEnsureDebugId(DebugIds.ABSENCE.EMPLOYEE_SELECTOR);
        absenceDateRangeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.DATE_RANGE_LABEL);
        dateRange.setEnsureDebugId(DebugIds.ABSENCE.DATE_RANGE_INPUT);
        dateRange.getRelative().ensureDebugId(DebugIds.ABSENCE.DATE_RANGE_BUTTON);
        absenceReasonLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.REASON_SELECTOR_LABEL);
        reason.setEnsureDebugId(DebugIds.ABSENCE.REASON_SELECTOR);
        absenceCommentLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.COMMENT_LABEL);
        comment.ensureDebugId(DebugIds.ABSENCE.COMMENT_INPUT);
    }

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
    RangePicker dateRange;
    @Inject
    @UiField(provided = true)
    AbsenceReasonButtonSelector reason;
    @UiField
    AutoResizeTextArea comment;
    @UiField
    HTMLPanel content;
    @UiField
    IndeterminateCircleLoading loading;

    @Inject
    @UiField
    Lang lang;

    private AbstractAbsenceEditActivity activity;

    private static AbsenceEditViewUiBinder ourUiBinder = GWT.create(AbsenceEditViewUiBinder.class);
    interface AbsenceEditViewUiBinder extends UiBinder<HTMLPanel, AbsenceEditView> {}
}