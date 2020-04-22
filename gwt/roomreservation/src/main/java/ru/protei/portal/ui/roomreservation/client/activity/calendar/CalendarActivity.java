package ru.protei.portal.ui.roomreservation.client.activity.calendar;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.events.RoomReservationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RoomReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.roomreservation.client.struct.YearMonthDay;

import java.util.Date;
import java.util.List;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.ui.roomreservation.client.util.DateUtils.*;

public abstract class CalendarActivity implements Activity, AbstractCalendarActivity {

    @PostConstruct
    public void onInit() {
        view.setActivity(this);
    }

    @Event
    public void onInitDetails(AppEvents.InitDetails initDetails) {
        this.initDetails = initDetails;
    }

    @Event(Type.FILL_CONTENT)
    public void onShow(RoomReservationEvents.Show event) {
        if (!policyService.hasPrivilegeFor(En_Privilege.ROOM_RESERVATION_VIEW)) {
            fireEvent(new ForbiddenEvents.Show(initDetails.parent));
            return;
        }
        date = new Date();
        showView();
        selectFirstRoom();
        selectDate(date);
        showCalendar(room, date);
    }

    @Override
    public void onRoomChanged(RoomReservable room) {
        this.room = room;
        showCalendar(room, date);
    }

    @Override
    public void onYearChanged(Integer year) {
        date.setYear(getYearDeNormalized(year));
        selectDate(date);
        showCalendar(room, date);
    }

    @Override
    public void onMonthChanged(Integer month) {
        date.setMonth(getMonthDeNormalized(month));
        selectDate(date);
        showCalendar(room, date);
    }

    @Override
    public void onDayOfMonthChanged(YearMonthDay day) {
        date.setYear(getYearDeNormalized(day.getYear()));
        date.setMonth(getMonthDeNormalized(day.getMonth()));
        date.setDate(day.getDayOfMonth());
        selectDate(date);
        showCalendar(room, date);
    }

    private void showView() {
        initDetails.parent.clear();
        initDetails.parent.add(view.asWidget());
    }

    private void selectFirstRoom() {
        roomReservationController.getRooms(new FluentCallback<List<RoomReservable>>()
                .withSuccess(rooms -> {
                    if (isNotEmpty(rooms)) {
                        view.room().setValue(rooms.get(0), true);
                    }
                }));
    }

    private void selectDate(Date date) {
        int year = getYearNormalized(date);
        int month = getMonthNormalized(date);
        int dayOfMonth = getDayOfMonth(date);
        int dayOfWeek = getDayOfWeekNormalized(date);
        view.year().setValue(year, false);
        view.month().setValue(month, false);
        view.dayOfMonth().setValue(new YearMonthDay(year, month, dayOfMonth), false);
        view.dayAndName().setValue(dayOfMonth + " " + getDayOfWeekName(dayOfWeek, lang));
    }

    private void showCalendar(RoomReservable room, Date date) {
        hideLoading();
        hideCalendar();
        if (room == null || date == null) {
            showLoading();
            return;
        }


    }

    private void showLoading() {

    }

    private void hideCalendar() {

    }

    private void hideLoading() {

    }

    @Inject
    Lang lang;
    @Inject
    AbstractCalendarView view;
    @Inject
    PolicyService policyService;
    @Inject
    RoomReservationControllerAsync roomReservationController;

    private RoomReservable room;
    private Date date;
    private AppEvents.InitDetails initDetails;
}
