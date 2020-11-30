package ru.protei.portal.ui.roomreservation.client.widget.datetime.item;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Node;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.brainworm.factory.core.datetimepicker.client.view.input.range.RangePicker;
import ru.brainworm.factory.core.datetimepicker.client.view.input.single.SinglePicker;
import ru.brainworm.factory.core.datetimepicker.shared.dto.DateInterval;
import ru.protei.portal.ui.roomreservation.client.widget.datetime.HasVaryAbility;

import java.util.Date;

import static ru.protei.portal.ui.common.client.util.DateUtils.resetTime;

public class RoomReservationDatesItem extends Composite implements HasValue<DateInterval>, HasEnabled, HasVaryAbility {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
        fixRangePickerTimeIcon(timePicker);
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public DateInterval getValue() {
        return new DateInterval(
            makeValue(
                datePicker.getValue(),
                timePicker.getValue().from
            ),
            makeValue(
                datePicker.getValue(),
                timePicker.getValue().to
            )
        );
    }

    private Date makeValue(Date date, Date time) {
        Date d = resetTime(date);
        d.setHours(time.getHours());
        d.setMinutes(time.getMinutes());
        d.setSeconds(0);
        return d;
    }

    @Override
    public void setValue(DateInterval value) {
        setValue(value, false);
    }

    @Override
    public void setValue(DateInterval value, boolean fireEvents) {
        datePicker.setValue(resetTime(value.from), fireEvents);
        timePicker.setValue(value, fireEvents);
    }

    @Override
    public boolean isEnabled() {
        return datePicker.isEnabled();
    }

    @Override
    public void setEnabled(boolean enabled) {
        datePicker.setEnabled(enabled);
        timePicker.setEnabled(enabled);
    }

    @Override
    public boolean isVaryAble() {
        return remove.isEnabled();
    }

    @Override
    public void setVaryAble(boolean isVaryAble) {
        remove.setEnabled(isVaryAble);
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<DateInterval> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    @UiHandler("remove")
    public void removeClick(ClickEvent event) {
        if (handler != null) {
            handler.onRemove();
        }
    }

    private void fixRangePickerTimeIcon(RangePicker rangePicker) {
        Element button = rangePicker.getRelative().getElement();
        int childrenCount = button.getChildCount();
        for (int i = 0; i < childrenCount; i++) {
            Node child = button.getChild(i);
            if (child.getNodeType() != Node.ELEMENT_NODE) {
                continue;
            }
            Element element = (Element) child;
            if (!"i".equalsIgnoreCase(element.getTagName())) {
                continue;
            }
            element.setClassName("fas fa-clock");
            break;
        }
    }

    @Inject
    @UiField(provided = true)
    SinglePicker datePicker;
    @Inject
    @UiField(provided = true)
    RangePicker timePicker;
    @UiField
    Button remove;

    public interface Handler {
        void onRemove();
    }

    private Handler handler;

    interface RoomReservationDatesItemBinder extends UiBinder<HTMLPanel, RoomReservationDatesItem> {}
    private static RoomReservationDatesItemBinder ourUiBinder = GWT.create(RoomReservationDatesItemBinder.class);
}
