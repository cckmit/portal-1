package ru.protei.portal.ui.absence.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.absence.client.activity.edit.AbstractAbsenceEditActivity;
import ru.protei.portal.ui.absence.client.activity.edit.AbstractAbsenceEditView;
import ru.protei.portal.ui.common.client.widget.selector.absencereason.AbsenceReasonButtonSelector;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;

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

    private void ensureDebugIds() {
        absenceEmployeeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.EMPLOYEE_SELECTOR_LABEL);
        absenceDateRangeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.DATE_RANGE_LABEL);
        absenceReasonLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.REASON_SELECTOR_LABEL);
        absenceCommentLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.ABSENCE.COMMENT_LABEL);
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

    private AbstractAbsenceEditActivity activity;

    private static AbsenceEditViewUiBinder ourUiBinder = GWT.create(AbsenceEditViewUiBinder.class);
    interface AbsenceEditViewUiBinder extends UiBinder<HTMLPanel, AbsenceEditView> {}
}