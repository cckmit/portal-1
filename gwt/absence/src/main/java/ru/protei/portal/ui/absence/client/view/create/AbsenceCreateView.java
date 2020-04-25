package ru.protei.portal.ui.absence.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.absence.client.activity.create.AbstractAbsenceCreateActivity;
import ru.protei.portal.ui.absence.client.activity.create.AbstractAbsenceCreateView;
import ru.protei.portal.ui.absence.client.widget.selector.AbsenceReasonButtonSelector;
import ru.protei.portal.ui.common.client.activity.casetag.edit.AbstractCaseTagEditActivity;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;

public class AbsenceCreateView extends Composite implements AbstractAbsenceCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractAbsenceCreateActivity activity) {
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

    private void ensureDebugIds() {
        absenceEmployeeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.Absence.LABEL.EMPLOYEE);
        absenceDateRangeLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.Absence.LABEL.DATE_RANGE);
        absenceReasonLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.Absence.LABEL.REASON);
        absenceCommentLabel.setId(DebugIds.DEBUG_ID_PREFIX + DebugIds.Absence.LABEL.COMMENT);
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

    private AbstractAbsenceCreateActivity activity;

    private static AbsenceCreateViewUiBinder ourUiBinder = GWT.create(AbsenceCreateViewUiBinder.class);
    interface AbsenceCreateViewUiBinder extends UiBinder<HTMLPanel, AbsenceCreateView> {}
}