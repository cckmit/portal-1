package ru.protei.portal.ui.common.client.view.transportationrequestfilter;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.DivElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.ui.common.client.activity.transportationrequestfilter.AbstractTransportationRequestFilterActivity;
import ru.protei.portal.ui.common.client.activity.transportationrequestfilter.AbstractTransportationRequestFilterView;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.DateIntervalWithType;
import ru.protei.portal.ui.common.client.widget.typedrangepicker.TypedSelectorRangePicker;

public class TransportationRequestFilterView extends Composite
        implements AbstractTransportationRequestFilterView {

    @Inject
    public void onInit() {
        initWidget(outUiBinder.createAndBindUi(this));
        pickupDate.fillSelector(En_DateIntervalType.defaultTypes());
    }

    @Override
    public void setActivity(AbstractTransportationRequestFilterActivity activity) {
        this.activity = activity;
    }

    @Override
    public void resetFilter() {
        pickupDate.setValue(null);
    }

    @Override
    public void clearFooterStyle() {
        footer.removeClassName("card-footer");
    }

    @Override
    public HasValue<DateIntervalWithType> pickupDate() {
        return pickupDate;
    }

    @Override
    public void setDateValid(boolean isTypeValid, boolean isRangeValid) {
        pickupDate.setValid(isTypeValid, isRangeValid);
    }

    @UiHandler("resetBtn")
    public void onResetClicked(ClickEvent event) {
        if (activity != null) {
            resetFilter();
            activity.onFilterChanged();
        }
    }

    @UiHandler("pickupDate")
    public void onPickupDateRangeChanged(ValueChangeEvent<DateIntervalWithType> event) {
        restartChangeTimer();
    }

    private void restartChangeTimer() {
        changeTimer.cancel();
        changeTimer.schedule(300);
    }

    private final Timer changeTimer = new Timer() {
        @Override
        public void run() {
            if (activity != null)
                activity.onFilterChanged();
        }
    };

    @Inject
    @UiField
    Lang lang;

    @Inject
    @UiField(provided = true)
    TypedSelectorRangePicker pickupDate;
    @UiField
    DivElement footer;

    private AbstractTransportationRequestFilterActivity activity;

    private static FilterViewUiBinder outUiBinder = GWT.create(FilterViewUiBinder.class);

    interface FilterViewUiBinder extends UiBinder<HTMLPanel, TransportationRequestFilterView> {
    }
}
