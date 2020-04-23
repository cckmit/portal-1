package ru.protei.portal.ui.roomreservation.client.activity.calendar;

import com.google.inject.Inject;
import ru.brainworm.factory.generator.activity.client.activity.Activity;
import ru.brainworm.factory.generator.activity.client.annotations.Event;
import ru.brainworm.factory.generator.activity.client.enums.Type;
import ru.brainworm.factory.generator.injector.client.PostConstruct;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.core.model.query.RoomReservationQuery;
import ru.protei.portal.ui.common.client.activity.policy.PolicyService;
import ru.protei.portal.ui.common.client.common.LocalStorageService;
import ru.protei.portal.ui.common.client.events.AppEvents;
import ru.protei.portal.ui.common.client.events.ForbiddenEvents;
import ru.protei.portal.ui.common.client.events.RoomReservationEvents;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.common.client.service.RoomReservationControllerAsync;
import ru.protei.portal.ui.common.shared.model.DefaultErrorHandler;
import ru.protei.portal.ui.common.shared.model.FluentCallback;
import ru.protei.portal.ui.roomreservation.client.struct.RoomReservationCalendar;
import ru.protei.portal.ui.roomreservation.client.struct.YearMonthDay;

import java.util.Date;
import java.util.List;
import java.util.Objects;

import static ru.protei.portal.core.model.helper.CollectionUtils.*;
import static ru.protei.portal.ui.roomreservation.client.util.DateUtils.*;

public abstract class RoomReservationCalendarActivity implements Activity, AbstractRoomReservationCalendarActivity {

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
        clearCache();
        showView();
        selectFirstRoom();
        selectDate(date);
        show(room, date);
    }

    @Override
    public void onAddNewReservationClicked() {
        Date d = resetTime(date);
        d.setHours(date.getHours() + 1);
        fireEvent(new RoomReservationEvents.Create(room, d));
    }

    @Override
    public void onAddNewReservationClicked(RoomReservable room, YearMonthDay day, Integer hour) {
        Date d = resetTime(date);
        d.setYear(getYearDeNormalized(day.getYear()));
        d.setMonth(getMonthDeNormalized(day.getMonth()));
        d.setDate(day.getDayOfMonth());
        d.setHours(hour);
        fireEvent(new RoomReservationEvents.Create(room, d));
    }

    @Override
    public void onEditReservationClicked(RoomReservation reservation) {
        fireEvent(new RoomReservationEvents.Edit(reservation.getId()));
    }

    @Override
    public void toggleHourStartButtonClicked() {
        saveHoursStartHidden(!isHoursStartHidden());
        show(room, date);
    }

    @Override
    public void onRoomChanged(RoomReservable room) {
        this.room = room;
        show(room, date);
    }

    @Override
    public void onYearChanged(Integer year) {
        date.setYear(getYearDeNormalized(year));
        selectDate(date);
        show(room, date);
    }

    @Override
    public void onMonthChanged(Integer month) {
        date.setMonth(getMonthDeNormalized(month));
        selectDate(date);
        show(room, date);
    }

    @Override
    public void onDayOfMonthChanged(YearMonthDay day) {
        date.setYear(getYearDeNormalized(day.getYear()));
        date.setMonth(getMonthDeNormalized(day.getMonth()));
        date.setDate(day.getDayOfMonth());
        selectDate(date);
        show(room, date);
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
        YearMonthDay day = makeYearMonthDay(date);
        boolean isPastDate = resetTime(date).before(resetTime(new Date()));
        view.year().setValue(day.getYear(), false);
        view.month().setValue(day.getMonth(), false);
        view.dayOfMonth().setValue(day, false);
        view.dayAndName().setValue(day.getDayOfMonth() + " " + getDayOfWeekName(day.getDayOfWeek(), lang));
        view.addNewReservationEnabled().setEnabled(!isPastDate);
    }

    private void show(RoomReservable room, Date date) {
        hideLoading();
        hideCalendar();
        if (room == null || date == null) {
            showLoading();
            return;
        }
        RoomReservationQuery query = makeQuery(room, date);
        List<RoomReservation> reservations = getFromCache(query);
        if (reservations != null) {
            showCalendar(room, date, reservations);
            return;
        }
        showLoading();
        roomReservationController.getReservations(query, new FluentCallback<List<RoomReservation>>()
            .withError(throwable -> {
                hideLoading();
                hideCalendar();
                defaultErrorHandler.accept(throwable);
            })
            .withSuccess(roomReservations -> {
                hideLoading();
                storeToCache(query, roomReservations);
                showCalendar(room, date, roomReservations);
            }));
    }

    private void showCalendar(RoomReservable room, Date date, List<RoomReservation> reservations) {
        view.calendarContainer().setValue(new RoomReservationCalendar(
            room,
            makeYearMonthDay(date),
            reservations,
            isHoursStartHidden() ? 9 : 0
        ));
        view.calendarContainerVisibility().setVisible(true);
    }

    private void hideCalendar() {
        view.calendarContainerVisibility().setVisible(false);
    }

    private void showLoading() {
        view.loadingVisibility().setVisible(true);
    }

    private void hideLoading() {
        view.loadingVisibility().setVisible(false);
    }

    private RoomReservationQuery makeQuery(RoomReservable room, Date date) {
        RoomReservationQuery query = new RoomReservationQuery();
        query.setRoomIds(setOf(room.getId()));
        query.setDateStart(setBeginOfMonth(date));
        query.setDateEnd(setEndOfMonth(date));
        return query;
    }

    private List<RoomReservation> getFromCache(RoomReservationQuery query) {
        boolean cacheSet = reservationsCache != null && queryCache != null;
        if (!cacheSet) {
            return null;
        }
        boolean roomsMatched = !diffCollection(queryCache.getRoomIds(), query.getRoomIds()).hasDifferences();
        if (!roomsMatched) {
            return null;
        }
        boolean datesMatched = Objects.equals(queryCache.getDateStart(), query.getDateStart())
                            && Objects.equals(queryCache.getDateEnd(), query.getDateEnd());
        if (!datesMatched) {
            return null;
        }
        return reservationsCache;
    }

    private void storeToCache(RoomReservationQuery query, List<RoomReservation> reservations) {
        queryCache = query;
        reservationsCache = reservations;
    }

    private void clearCache() {
        storeToCache(null, null);
    }

    private boolean isHoursStartHidden() {
        return Boolean.parseBoolean(localStorageService.getOrDefault(CALENDAR_HIDE_HOURS_START, "true"));
    }

    private void saveHoursStartHidden(boolean isHidden) {
        localStorageService.set(CALENDAR_HIDE_HOURS_START, String.valueOf(isHidden));
    }

    @Inject
    Lang lang;
    @Inject
    AbstractRoomReservationCalendarView view;
    @Inject
    PolicyService policyService;
    @Inject
    LocalStorageService localStorageService;
    @Inject
    RoomReservationControllerAsync roomReservationController;
    @Inject
    DefaultErrorHandler defaultErrorHandler;

    private RoomReservable room;
    private Date date;
    private RoomReservationQuery queryCache;
    private List<RoomReservation> reservationsCache;
    private AppEvents.InitDetails initDetails;
    private final static String CALENDAR_HIDE_HOURS_START = "room_reservation_hide_hours_start";
}
