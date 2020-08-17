package ru.protei.portal.ui.roomreservation.client.factory;

import com.google.gwt.inject.client.AbstractGinModule;
import com.google.inject.Singleton;
import ru.protei.portal.ui.roomreservation.client.activity.calendar.AbstractRoomReservationCalendarView;
import ru.protei.portal.ui.roomreservation.client.activity.calendar.RoomReservationCalendarActivity;
import ru.protei.portal.ui.roomreservation.client.activity.edit.AbstractRoomReservationEditView;
import ru.protei.portal.ui.roomreservation.client.activity.edit.RoomReservationEditActivity;
import ru.protei.portal.ui.roomreservation.client.activity.table.AbstractRoomReservationTableView;
import ru.protei.portal.ui.roomreservation.client.activity.table.RoomReservationTableActivity;
import ru.protei.portal.ui.roomreservation.client.page.RoomReservationPage;
import ru.protei.portal.ui.roomreservation.client.view.calendar.RoomReservationCalendarView;
import ru.protei.portal.ui.roomreservation.client.view.edit.RoomReservationEditView;
import ru.protei.portal.ui.roomreservation.client.view.table.RoomReservationTableView;

public class RoomReservationClientModule extends AbstractGinModule {
    @Override
    protected void configure() {

        bind(RoomReservationPage.class).asEagerSingleton();

        bind(RoomReservationCalendarActivity.class).asEagerSingleton();
        bind(AbstractRoomReservationCalendarView.class).to(RoomReservationCalendarView.class).in(Singleton.class);
        bind(RoomReservationTableActivity.class).asEagerSingleton();
        bind(AbstractRoomReservationTableView.class).to(RoomReservationTableView.class).in(Singleton.class);
        bind(RoomReservationEditActivity.class).asEagerSingleton();
        bind(AbstractRoomReservationEditView.class).to(RoomReservationEditView.class);
    }
}
