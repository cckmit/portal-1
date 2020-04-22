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

    public RoomReservationCalendar() {}

    public RoomReservationCalendar(RoomReservable room, YearMonthDay yearMonthDay, List<RoomReservation> reservations, Integer hourStart) {
        this.room = room;
        this.yearMonthDay = yearMonthDay;
        this.reservations = reservations;
        this.hourStart = hourStart;
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

    @Override
    public String toString() {
        return "RoomReservationCalendar{" +
                "room=" + room +
                ", yearMonthDay=" + yearMonthDay +
                ", reservations=" + reservations +
                ", hourStart=" + hourStart +
                '}';
    }
}
