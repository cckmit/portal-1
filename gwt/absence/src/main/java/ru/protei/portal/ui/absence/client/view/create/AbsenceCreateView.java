package ru.protei.portal.ui.absence.client.view.create;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.ui.absence.client.activity.common.AbstractAbsenceCommonActivity;
import ru.protei.portal.ui.absence.client.activity.create.AbstractAbsenceCreateView;
import ru.protei.portal.ui.absence.client.widget.datetime.AbsenceDates;

import java.util.List;

public class AbsenceCreateView extends Composite implements AbstractAbsenceCreateView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractAbsenceCommonActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<List<DateInterval>> dateRange() {
        return absenceDates;
    }

    @Override
    public void setDateRangeValid(boolean isValid) {
        absenceDates.setValid(isValid);
    }

    @UiHandler("absenceDates")
    public void onDateRangeChanged(ValueChangeEvent<List<DateInterval>> event) {
        activity.onDateRangeChanged();
    }

    @Inject
    @UiField(provided = true)
    AbsenceDates absenceDates;

    protected AbstractAbsenceCommonActivity activity;

    private static AbsenceCreateViewUiBinder ourUiBinder = GWT.create(AbsenceCreateViewUiBinder.class);
    interface AbsenceCreateViewUiBinder extends UiBinder<HTMLPanel, AbsenceCreateView> {}
}