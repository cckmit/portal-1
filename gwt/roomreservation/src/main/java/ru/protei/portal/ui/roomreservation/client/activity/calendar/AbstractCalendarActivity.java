package ru.protei.portal.ui.roomreservation.client.activity.calendar;

import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.ui.roomreservation.client.struct.YearMonthDay;

public interface AbstractCalendarActivity {

    void onRoomChanged(RoomReservable room);

    void onYearChanged(Integer year);

    void onMonthChanged(Integer month);

    void onDayOfMonthChanged(YearMonthDay day);
}
