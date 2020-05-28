package ru.protei.portal.ui.roomreservation.client.struct;

import ru.protei.portal.core.model.ent.RoomReservable;
import ru.protei.portal.core.model.ent.RoomReservation;

import java.io.Serializable;
import java.util.List;

public class RoomReservationCalendar implements Serializable {

    private RoomReservable room;
    private YearMonthDay yearMonthDay;
    private List<RoomReservation> reservations;
    private Integer hourStart;
    private boolean hasCreateAccess;

    public RoomReservationCalendar() {}

    public RoomReservationCalendar(
        RoomReservable room,
        YearMonthDay yearMonthDay,
        List<RoomReservation> reservations,
        Integer hourStart,
        boolean hasCreateAccess
    ) {
        this.room = room;
        this.yearMonthDay = yearMonthDay;
        this.reservations = reservations;
        this.hourStart = hourStart;
        this.hasCreateAccess = hasCreateAccess;
    }

    public RoomReservable getRoom() {
        return room;
    }

    public void setRoom(RoomReservable room) {
        this.room = room;
    }

    public YearMonthDay getYearMonthDay() {
        return yearMonthDay;
    }

    public void setYearMonthDay(YearMonthDay yearMonthDay) {
        this.yearMonthDay = yearMonthDay;
    }

    public List<RoomReservation> getReservations() {
        return reservations;
    }

    public void setReservations(List<RoomReservation> reservations) {
        this.reservations = reservations;
    }

    public Integer getHourStart() {
        return hourStart;
    }

    public void setHourStart(Integer hourStart) {
        this.hourStart = hourStart;
    }

    public boolean isHasCreateAccess() {
        return hasCreateAccess;
    }

    public void setHasCreateAccess(boolean hasCreateAccess) {
        this.hasCreateAccess = hasCreateAccess;
    }

    @Override
    public String toString() {
        return "RoomReservationCalendar{" +
                "room=" + room +
                ", yearMonthDay=" + yearMonthDay +
                ", reservations=" + reservations +
                ", hourStart=" + hourStart +
                ", hasCreateAccess=" + hasCreateAccess +
                '}';
    }
}
