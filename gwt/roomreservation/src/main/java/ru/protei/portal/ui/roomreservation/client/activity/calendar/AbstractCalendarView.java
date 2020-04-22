package ru.protei.portal.ui.roomreservation.client.activity.calendar;

import com.google.gwt.user.client.TakesValue;
import com.google.gwt.user.client.ui.HasValue;
import com.google.gwt.user.client.ui.IsWidget;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.ui.roomreservation.client.struct.YearMonthDay;

import java.util.List;
import java.util.Map;

public interface AbstractCalendarView extends IsWidget {

    void setActivity(AbstractCalendarActivity activity);

    HasValue<RoomReservable> room();

    HasValue<Integer> year();

    HasValue<Integer> month();

    HasValue<YearMonthDay> dayOfMonth();

    TakesValue<String> dayAndName();
}
