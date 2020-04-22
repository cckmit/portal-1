package ru.protei.portal.ui.roomreservation.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.roomreservation.client.activity.calendar.AbstractCalendarView;
import ru.protei.portal.ui.roomreservation.client.activity.calendar.CalendarActivity;
import ru.protei.portal.ui.roomreservation.client.page.RoomReservationPage;
import ru.protei.portal.ui.roomreservation.client.view.calendar.CalendarView;

public class RoomReservationClientModule extends AbstractGinModule {
    @Override
    protected void configure() {

        bind(RoomReservationPage.class).asEagerSingleton();

        bind(CalendarActivity.class).asEagerSingleton();
        bind(AbstractCalendarView.class).to(CalendarView.class).in(Singleton.class);

    }
}
