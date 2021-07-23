package ru.protei.portal.ui.absence.client.widget.timerange;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.InputElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.ui.common.client.common.UiConstants;

import java.util.Date;

public class TimeRange extends Composite implements HasValue<Interval> {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public Interval getValue() {
        Interval value = new Interval(from.getValue(), to.getValue());
//        String fromTime = this.fromTime.getValue();
//        String toTime = this.toTime.getValue();

        return value.isEmpty() ? null : value;
    }

    @Override
    public void setValue(Interval value) {
        setValue(value, false);
    }

    @Override
    public void setValue(Interval value, boolean fireEvents) {
        if (value == null || value.isEmpty()) {
            value = new Interval();
        }
        from.setValue(value.from);
        to.setValue(value.to);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<Interval> handler) {
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
        Interval interval = new Interval(from.getValue(), to.getValue());
        if (interval.isValid()) {
            getWidget().removeStyleName(UiConstants.Styles.HAS_ERROR);
        } else {
            getWidget().addStyleName(UiConstants.Styles.HAS_ERROR);
        }
    }

    @Inject
    @UiField(provided = true)
    SinglePicker from;
    @Inject
    @UiField(provided = true)
    SinglePicker to;

//    @UiField
//    InputElement fromTime;

//    @UiField
//    InputElement toTime;

    interface AbsenceDatesItemBinder extends UiBinder<HTMLPanel, TimeRange> {}
    private static AbsenceDatesItemBinder ourUiBinder = GWT.create(AbsenceDatesItemBinder.class);
}
