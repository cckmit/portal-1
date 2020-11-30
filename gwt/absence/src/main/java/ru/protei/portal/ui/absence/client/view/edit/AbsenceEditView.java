package ru.protei.portal.ui.absence.client.view.edit;

import com.google.gwt.core.client.GWT;
import com.google.gwt.debug.client.DebugInfo;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.test.client.DebugIds;
import ru.protei.portal.ui.absence.client.activity.common.AbstractAbsenceCommonActivity;
import ru.protei.portal.ui.absence.client.activity.edit.AbstractAbsenceEditView;

public class AbsenceEditView extends Composite implements AbstractAbsenceEditView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        ensureDebugIds();
    }

    @Override
    public void setActivity(AbstractAbsenceCommonActivity activity) {
        this.activity = activity;
    }


    @Override
    public HasValue<DateInterval> dateRange() {
        return dateRange;
    }

    @Override
    public void setDateRangeValid(boolean isValid) {
        dateRange.markInputValid(isValid);
    }

    @UiHandler("dateRange")
    public void onDateRangeChanged(ValueChangeEvent<DateInterval> event) {
        activity.onDateRangeChanged();
    }

    protected void ensureDebugIds() {
        if (!DebugInfo.isDebugIdEnabled()) {
            return;
        }
        dateRange.setEnsureDebugId(DebugIds.ABSENCE.DATE_RANGE_INPUT);
        dateRange.getRelative().ensureDebugId(DebugIds.ABSENCE.DATE_RANGE_BUTTON);
    }

    @Inject
    @UiField(provided = true)
    RangePicker dateRange;

    protected AbstractAbsenceCommonActivity activity;

    private static AbsenceEditViewUiBinder ourUiBinder = GWT.create(AbsenceEditViewUiBinder.class);
    interface AbsenceEditViewUiBinder extends UiBinder<HTMLPanel, AbsenceEditView> {}
}