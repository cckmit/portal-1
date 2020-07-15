package ru.protei.portal.ui.roomreservation.client.widget.calendar.container;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.uibinder.client.UiBinder;
import com.google.gwt.uibinder.client.UiField;
import com.google.gwt.user.client.ui.*;
import com.google.inject.Inject;
import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.RoomReservation;
import ru.protei.portal.ui.common.client.lang.En_RoomReservationReasonLang;
import ru.protei.portal.ui.common.client.lang.Lang;
import ru.protei.portal.ui.roomreservation.client.struct.Day;
import ru.protei.portal.ui.roomreservation.client.struct.RoomReservationCalendar;
import ru.protei.portal.ui.common.client.common.YearMonthDay;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.google.gwt.user.datepicker.client.CalendarUtil.copyDate;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.ui.common.client.util.DateUtils.*;
import static ru.protei.portal.ui.roomreservation.client.util.WidgetUtils.*;

public class CalendarContainer extends Composite implements HasValue<RoomReservationCalendar>, HasVisibility {

    @Inject
    public void init() {
        initWidget(ourUiBinder.createAndBindUi(this));
    }

    public void setHandler(Handler handler) {
        this.handler = handler;
    }

    @Override
    public RoomReservationCalendar getValue() {
        return value;
    }

    @Override
    public void setValue(RoomReservationCalendar value) {
        setValue(value, false);
    }

    @Override
    public void setValue(RoomReservationCalendar value, boolean fireEvents) {
        this.value = value;
        // TODO split cross-day reservations + split by hourStart
        clearView();
        fillView(value);
        if (fireEvents) {
            ValueChangeEvent.fire(this, value);
        }
    }

    @Override
    public HandlerRegistration addValueChangeHandler(ValueChangeHandler<RoomReservationCalendar> handler) {
        return addHandler(handler, ValueChangeEvent.getType());
    }

    private void clearView() {
        container.clear();
    }

    private void fillView(RoomReservationCalendar value) {
        this.eventBackgroundState = 0;
        List<Day> week = makeWeek(value.getYearMonthDay());
        container.add(makeAllDayCell());
        container.add(makeHorizontalHeader(week));
        container.add(makeGrid(value, week));
    }

    private Widget makeAllDayCell() {
        return makeStyledDiv("allday-cell");
    }

    private Widget makeHorizontalHeader(List<Day> week) {
        HTMLPanel table = makeStyledDiv("tble");
        HTMLPanel thead = makeStyledDiv("thead");
        for (Day day : week) {
            HTMLPanel cell = makeStyledDiv("tcell");
            cell.add(makeStyledDiv("weekdate", day.getDayOfMonth() != null
                    ? String.valueOf(day.getDayOfMonth())
                    : ""));
            cell.add(makeStyledDiv("weekday", day.getDayOfMonth() != null
                    ? getDayOfWeekName(day.getDayOfWeek(), lang)
                    : ""));
            thead.add(cell);
        }
        table.add(thead);
        return table;
    }

    private Widget makeGrid(RoomReservationCalendar value, List<Day> week) {
        HTMLPanel grid = makeStyledDiv("grid slot-30");
        grid.add(makeVerticalHeader(value.getHourStart()));
        grid.add(makeTable(value, week));
        return grid;
    }

    private Widget makeVerticalHeader(int hourStart) {
        HTMLPanel slots = makeStyledDiv("time-slot-wrapper");
        for (int time = hourStart; time < 24; time++) {
            slots.add(makeStyledDiv("time-slot", "<span>" + time + ":00</span>"));
        }
        return slots;
    }

    private Widget makeTable(RoomReservationCalendar value, List<Day> week) {
        HTMLPanel table = makeStyledDiv("tble");
        for (int hour = value.getHourStart(); hour < 24; hour++) {
            HTMLPanel row = makeStyledDiv("trow");
            for (Day day : week) {
                row.add(makeTableDayCell(value, day, hour));
            }
            table.add(row);
        }
        return table;
    }

    private Widget makeTableDayCell(RoomReservationCalendar value, Day day, int hour) {
        YearMonthDay current = new YearMonthDay(
            value.getYearMonthDay().getYear(),
            value.getYearMonthDay().getMonth(),
            day.getDayOfMonth(),
            day.getDayOfWeek()
        );
        List<RoomReservation> reservations = fetchReservations(
            value.getReservations(),
            value.getRoom(),
            current,
            hour
        );
        boolean isSelected = isSame(
            value.getYearMonthDay(),
            current
        );
        boolean isIllegalDay = day.getDayOfMonth() == null;
        boolean isPastDay = !isIllegalDay && makeDateEndOfHour(current, hour).before(new Date());
        boolean isCellSelectable = !isPastDay && value.isHasCreateAccess();
        if (isIllegalDay) {
            HTMLPanel cell = makeStyledDiv("tcell" + (isSelected ? " active" : ""));
            return cell;
        } else if (!isCellSelectable) {
            HTMLPanel cell = makeStyledDiv("tcell" + (isSelected ? " active" : ""));
            cell.add(makeTableDayCell(reservations, hour));
            return cell;
        } else {
            FocusPanel cell = makeStyledFocusDiv("tcell" + (isSelected ? " active" : ""));
            cell.add(makeTableDayCell(reservations, hour));
            cell.addDoubleClickHandler(event -> {
                event.preventDefault();
                event.stopPropagation();
                if (handler != null) {
                    handler.onCellClicked(value.getRoom(), current, hour);
                }
            });
            return cell;
        }
    }

    private Widget makeTableDayCell(List<RoomReservation> reservations, int hour) {
        HTMLPanel cellWrapper = makeDiv();
        HTMLPanel cellInner = makeStyledDiv("cell-inner");
        for (RoomReservation reservation : reservations) {
            cellInner.add(makeTableEvent(reservation, hour));
        }
        cellWrapper.add(cellInner);
        cellWrapper.add(makeStyledDiv("cell-inner"));
        return cellWrapper;
    }

    private Widget makeTableEvent(RoomReservation reservation, int hour) {
        FocusPanel container = makeStyledFocusDiv("event-container " + getNextEventBackground());
        HTMLPanel inner = makeStyledDiv("event-inner");
        inner.add(makeStyledDiv("event-title", makeReservationTitle(reservation)));
        inner.add(makeStyledDiv("time-wrap", makeReservationTime(reservation)));
        inner.add(makeStyledDiv("time-wrap", makeReservationReason(reservation)));
        container.setWidget(inner);
        container.getElement().getStyle().setHeight(calculateEventHeight(reservation), Style.Unit.PX);
        container.getElement().getStyle().setTop(calculateEventTop(reservation, hour), Style.Unit.PX);
        container.addDoubleClickHandler(event -> {
            event.preventDefault();
            event.stopPropagation();
            if (handler != null) {
                handler.onReservationClicked(reservation);
            }
        });
        return container;
    }

    private String makeReservationTitle(RoomReservation reservation) {
        return reservation.getPersonResponsible() != null
                ? reservation.getPersonResponsible().getDisplayShortName()
                : "?";
    }

    private String makeReservationTime(RoomReservation reservation) {
        String start = makeTimeHourMinutes(reservation.getDateFrom());
        String end = makeTimeHourMinutes(reservation.getDateUntil());
        return "<span class='event-start-time'>" + start + "</span> - <span class='event-end-time'>" + end + "</span>";
    }

    private String makeReservationReason(RoomReservation reservation) {
        String reason = reasonLang.getName(reservation.getReason());
        return "<span class='event-start-time'>" + reason + "</span>";
    }

    private double calculateEventHeight(RoomReservation reservation) {
        int minutes = getMinutesBetween(reservation.getDateFrom(), reservation.getDateUntil());
        return (PIXELS_PER_HOUR / 60.0) * (double) minutes;
    }

    private double calculateEventTop(RoomReservation reservation, int hour) {
        Date from = copyDate(reservation.getDateFrom());
        from.setHours(hour);
        from.setMinutes(0);
        int minutes = getMinutesBetween(from, reservation.getDateFrom());
        return (PIXELS_PER_HOUR / 60.0) * (double) minutes;
    }

    private String getNextEventBackground() {
        int index = eventBackgroundState++;
        String background = BACKGROUNDS[index];
        if (eventBackgroundState >= BACKGROUNDS.length) {
            eventBackgroundState = 0;
        }
        return background;
    }

    private List<RoomReservation> fetchReservations(
        List<RoomReservation> reservations,
        RoomReservable room,
        YearMonthDay day,
        int hour
    ) {
        return stream(reservations)
            .filter(r -> r.getRoom() != null && r.getDateFrom() != null)
            .filter(r -> Objects.equals(r.getRoom().getId(), room.getId()))
            .filter(r -> isSame(day, makeYearMonthDay(r.getDateFrom())))
            .filter(r -> r.getDateFrom().getHours() == hour)
            .collect(Collectors.toList());
    }

    private List<Day> makeWeek(YearMonthDay value) {
        int year = value.getYear();
        int month = value.getMonth();
        int dayOfMonth = value.getDayOfMonth();
        int dayOfWeek = value.getDayOfWeek();
        List<Day> week = new ArrayList<>();
        for (int w = 1; w <= 7; w++) {
            Integer m = dayOfMonth - (dayOfWeek - w);
            if (m < 1 || m > getDaysInMonth(month, year)) {
                m = null;
            }
            week.add(new Day(m, w));
        }
        return week;
    }

    private Date makeDateEndOfHour(YearMonthDay day, int hour) {
        Date date = makeDate(day);
        date.setHours(hour);
        date.setMinutes(59);
        date.setSeconds(59);
        return date;
    }

    private String makeTimeHourMinutes(Date date) {
        int hours = date.getHours();
        int minutes = date.getMinutes();
        return makeTwoDigitNumber(hours) + ":" + makeTwoDigitNumber(minutes);
    }

    private String makeTwoDigitNumber(int number) {
        return (String.valueOf(number).length() == 1 ? "0" : "") + number;
    }

    @Inject
    Lang lang;
    @Inject
    En_RoomReservationReasonLang reasonLang;

    @UiField
    HTMLPanel container;

    private RoomReservationCalendar value;
    private Handler handler;
    private int eventBackgroundState = 0;
    private static final double PIXELS_PER_HOUR = 80.0;
    private static final String[] BACKGROUNDS = new String[] {
        "bg-primary-lighter",
        "bg-complete-lighter",
        "bg-success-lighter",
        "bg-danger-lighter",
        "bg-master-lighter",
    };

    public interface Handler {
        void onReservationClicked(RoomReservation reservation);
        void onCellClicked(RoomReservable room, YearMonthDay day, Integer hour);
    }

    interface CalendarContainerBinder extends UiBinder<HTMLPanel, CalendarContainer> {}
    private static CalendarContainerBinder ourUiBinder = GWT.create(CalendarContainerBinder.class);
}
