package ru.protei.portal.ui.roomreservation.client.view.calendar;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.HeadingElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.uibinder.client.UiHandler;
import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.ui.common.client.widget.loading.IndeterminateCircleLoading;
import ru.protei.portal.ui.roomreservation.client.activity.calendar.AbstractRoomReservationCalendarActivity;
import ru.protei.portal.ui.roomreservation.client.activity.calendar.AbstractRoomReservationCalendarView;
import ru.protei.portal.ui.roomreservation.client.struct.RoomReservationCalendar;
import ru.protei.portal.ui.roomreservation.client.struct.YearMonthDay;
import ru.protei.portal.ui.roomreservation.client.widget.calendar.container.CalendarContainer;
import ru.protei.portal.ui.roomreservation.client.widget.calendar.options.CalendarDayOfMonth;
import ru.protei.portal.ui.roomreservation.client.widget.selector.month.MonthButtonSelector;
import ru.protei.portal.ui.roomreservation.client.widget.selector.room.RoomReservableButtonSelector;
import ru.protei.portal.ui.roomreservation.client.widget.selector.year.YearButtonSelector;

public class RoomReservationCalendarView extends Composite implements AbstractRoomReservationCalendarView {

    @Inject
    public void onInit() {
        initWidget(ourUiBinder.createAndBindUi(this));
        calendarContainer.setHandler(new CalendarContainer.Handler() {
            public void onReservationClicked(RoomReservation reservation) {
                if (activity != null) {
                    activity.onEditReservationClicked(reservation);
                }
            }
            public void onCellClicked(RoomReservable room, YearMonthDay day, Integer hour) {
                if (activity != null) {
                    activity.onAddNewReservationClicked(room, day, hour);
                }
            }
        });
    }

    @Override
    public void setActivity(AbstractRoomReservationCalendarActivity activity) {
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

    @Override
    public HasValue<RoomReservationCalendar> calendarContainer() {
        return calendarContainer;
    }

    @Override
    public HasEnabled addNewReservationEnabled() {
        return addNewReservationButton;
    }

    @Override
    public HasVisibility loadingVisibility() {
        return loading;
    }

    @Override
    public HasVisibility calendarContainerVisibility() {
        return calendarContainer;
    }

    @Override
    public HasVisibility roomAccessibilityVisibility() {
        return roomNotAccessiblePanel;
    }

    @UiHandler("addNewReservationButton")
    public void addNewReservationButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.onAddNewReservationClicked();
        }
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

    @UiHandler("showTodayButton")
    public void showTodayButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.showTodayButtonClicked();
        }
    }

    @UiHandler("toggleHourStartButton")
    public void toggleHourStartButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.toggleHourStartButtonClicked();
        }
    }

    @UiHandler("reloadButton")
    public void reloadButtonClick(ClickEvent event) {
        if (activity != null) {
            activity.reloadClicked();
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
    @UiField
    HTMLPanel roomNotAccessiblePanel;
    @UiField
    Button showTodayButton;
    @UiField
    Button toggleHourStartButton;
    @UiField
    Button reloadButton;
    @UiField
    Button addNewReservationButton;
    @Inject
    @UiField(provided = true)
    CalendarDayOfMonth dayOfMonthSelector;
    @UiField
    IndeterminateCircleLoading loading;
    @Inject
    @UiField(provided = true)
    CalendarContainer calendarContainer;

    private AbstractRoomReservationCalendarActivity activity;

    interface CalendarViewBinder extends UiBinder<HTMLPanel, RoomReservationCalendarView> {}
    private static CalendarViewBinder ourUiBinder = GWT.create(CalendarViewBinder.class);
}
