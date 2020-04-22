package ru.protei.portal.ui.roomreservation.client.view.calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.HTMLPanel;
import com.google.gwt.user.client.ui.HasValue;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.ui.roomreservation.client.activity.calendar.AbstractCalendarActivity;
import ru.protei.portal.ui.roomreservation.client.activity.calendar.AbstractCalendarView;
import ru.protei.portal.ui.roomreservation.client.struct.YearMonthDay;
import ru.protei.portal.ui.roomreservation.client.widget.calendar.dayofmonth.CalendarDayOfMonth;
import ru.protei.portal.ui.roomreservation.client.widget.selector.month.MonthButtonSelector;
import ru.protei.portal.ui.roomreservation.client.widget.selector.room.RoomReservableButtonSelector;
import ru.protei.portal.ui.roomreservation.client.widget.selector.year.YearButtonSelector;

public class CalendarView extends Composite implements AbstractCalendarView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    @Override
    public void setActivity(AbstractCalendarActivity activity) {
        this.activity = activity;
    }

    @Override
    public HasValue<RoomReservable> room() {
        return roomSelector;
    }

    @Override
    public HasValue<Integer> year() {
        return yearSelector;
    }

    @Override
    public HasValue<Integer> month() {
        return monthSelector;
    }

    @Override
    public HasValue<YearMonthDay> dayOfMonth() {
        return dayOfMonthSelector;
    }

    @Override
    public TakesValue<String> dayAndName() {
        return new TakesValue<String>() {
            public void setValue(String value) { dayAndName.setInnerText(value); }
            public String getValue() { return dayAndName.getInnerText(); }
        };
    }

    @UiHandler("roomSelector")
    public void roomSelectorChanged(ValueChangeEvent<RoomReservable> event) {
        if (activity != null) {
            activity.onRoomChanged(event.getValue());
        }
    }

    @UiHandler("yearSelector")
    public void yearSelectorChanged(ValueChangeEvent<Integer> event) {
        if (activity != null) {
            activity.onYearChanged(event.getValue());
        }
    }

    @UiHandler("monthSelector")
    public void monthSelectorChanged(ValueChangeEvent<Integer> event) {
        if (activity != null) {
            activity.onMonthChanged(event.getValue());
        }
    }

    @UiHandler("dayOfMonthSelector")
    public void dayOfMonthSelectorChanged(ValueChangeEvent<YearMonthDay> event) {
        if (activity != null) {
            activity.onDayOfMonthChanged(event.getValue());
        }
    }

    @Inject
    @UiField(provided = true)
    RoomReservableButtonSelector roomSelector;
    @Inject
    @UiField(provided = true)
    YearButtonSelector yearSelector;
    @Inject
    @UiField(provided = true)
    MonthButtonSelector monthSelector;
    @UiField
    HeadingElement dayAndName;
    @Inject
    @UiField(provided = true)
    CalendarDayOfMonth dayOfMonthSelector;

    private AbstractCalendarActivity activity;

    interface CalendarViewBinder extends UiBinder<HTMLPanel, CalendarView> {}
    private static CalendarViewBinder ourUiBinder = GWT.create(CalendarViewBinder.class);
}
