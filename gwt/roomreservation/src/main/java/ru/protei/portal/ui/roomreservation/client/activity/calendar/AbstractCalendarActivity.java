package ru.protei.portal.ui.roomreservation.client.activity.calendar;

import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.ui.roomreservation.client.struct.YearMonthDay;

public interface AbstractCalendarActivity {

    void onAddNewReservationClicked();

    void onAddNewReservationClicked(RoomReservable room, YearMonthDay day, Integer hour);

    void onEditReservationClicked(RoomReservation reservation);

    void onRoomChanged(RoomReservable room);

    void onYearChanged(Integer year);

    void onMonthChanged(Integer month);

    void onDayOfMonthChanged(YearMonthDay day);
}
