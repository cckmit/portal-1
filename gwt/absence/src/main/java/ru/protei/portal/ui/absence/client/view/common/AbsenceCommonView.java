package ru.protei.portal.ui.absence.client.view.common;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.dom.client.LabelElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.absence.client.activity.common.AbstractAbsenceCommonView;
import ru.protei.portal.ui.absence.client.activity.create.AbstractAbsenceCreateView;
import ru.protei.portal.ui.absence.client.activity.edit.AbstractAbsenceEditView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.autoresizetextarea.AutoResizeTextArea;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.common.client.widget.selector.absencereason.AbsenceReasonButtonSelector;
import ru.protei.portal.ui.common.client.widget.selector.person.EmployeeButtonSelector;
import ru.protei.portal.ui.common.client.widget.validatefield.HasValidable;

import java.util.*;

public class AbsenceCommonView extends Composite implements AbstractAbsenceCommonView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
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
    public HasWidgets getDateContainer() {
        return dateContainer;
    }

    @UiHandler("reason")
    public void onReasonChanged(ValueChangeEvent<En_AbsenceReason> event) {
        List<DateInterval> newDateRange = new LinkedList<>();
        createView.dateRange().getValue().forEach(interval -> {
            Date to = interval.to;
            if (event.getValue().equals(En_AbsenceReason.NIGHT_WORK)) {
                to.setHours(13);
                to.setMinutes(00);
                to.setSeconds(00);
            } else {
                to.setHours(23);
                to.setMinutes(59);
                to.setSeconds(59);
            }

            newDateRange.add(new DateInterval(interval.from, to));
        });

        createView.dateRange().setValue(newDateRange);
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

    @UiField
    protected HTMLPanel dateContainer;

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

    @Inject
    AbstractAbsenceCreateView createView;
    @Inject
    AbstractAbsenceEditView editView;

    private static AbsenceCommonViewUiBinder ourUiBinder = GWT.create(AbsenceCommonViewUiBinder.class);
    interface AbsenceCommonViewUiBinder extends UiBinder<HTMLPanel, AbsenceCommonView> {}
}