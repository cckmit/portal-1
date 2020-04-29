package ru.protei.portal.ui.roomreservation.client.activity.calendar;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasEnabled;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.HasVisibility;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.ui.roomreservation.client.struct.RoomReservationCalendar;
import ru.protei.portal.ui.roomreservation.client.struct.YearMonthDay;

public interface AbstractRoomReservationCalendarView extends IsWidget {

    void setActivity(AbstractRoomReservationCalendarActivity activity);

    HasValue<RoomReservable> room();

    HasValue<Integer> year();

    HasValue<Integer> month();

    HasValue<YearMonthDay> dayOfMonth();

    TakesValue<String> dayAndName();

    HasValue<RoomReservationCalendar> calendarContainer();

    HasEnabled addNewReservationEnabled();

    HasVisibility loadingVisibility();

    HasVisibility calendarContainerVisibility();

    HasVisibility roomAccessibilityVisibility();
}
