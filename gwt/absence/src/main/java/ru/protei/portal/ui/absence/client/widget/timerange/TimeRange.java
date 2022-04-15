package ru.protei.portal.ui.absence.client.widget.timerange;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.dto.Time;
import ru.protei.portal.core.model.dto.TimeInterval;
import ru.protei.portal.ui.common.client.common.UiConstants;

import java.util.Date;

public class TimeRange extends Composite implements HasValue<TimeInterval> {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public TimeInterval getValue() {
        TimeInterval value = new TimeInterval(from.getValue(), to.getValue());
        return value.isEmpty() ? null : value;
    }

    @Override
    public void setValue(TimeInterval value) {
        setValue(value, false);
    }

    @Override
    public void setValue(TimeInterval value, boolean fireEvents) {
        if (value == null || value.isEmpty()) {
            value = new TimeInterval();
        }
        from.setValue(convertTimeToDate(value.getFrom()));
        to.setValue(convertTimeToDate(value.getTo()));
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<TimeInterval> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("from")
    public void onFromTimeChanged(ValueChangeEvent<Date> event) {
        ValueChangeEvent.fire(this, getValue());
        validate();
    }

    @UiHandler("to")
    public void onToTimeChanged(ValueChangeEvent<Date> event) {
        ValueChangeEvent.fire(this, getValue());
        validate();
    }

    private void validate() {
        TimeInterval interval = new TimeInterval(from.getValue(), to.getValue());
        if (interval.isValid()) {
            getWidget().removeStyleName(UiConstants.Styles.HAS_ERROR);
        } else {
            getWidget().addStyleName(UiConstants.Styles.HAS_ERROR);
        }
    }

    private Date convertTimeToDate(Time value) {
        Date result = new Date();
        result.setHours(value.getHour());
        result.setMinutes(value.getMinute());
        return result;
    }

    @Inject
    @UiField(provided = true)
    SinglePicker from;
    @Inject
    @UiField(provided = true)
    SinglePicker to;

    interface AbsenceDatesItemBinder extends UiBinder<HTMLPanel, TimeRange> {}
    private static AbsenceDatesItemBinder ourUiBinder = GWT.create(AbsenceDatesItemBinder.class);
}
