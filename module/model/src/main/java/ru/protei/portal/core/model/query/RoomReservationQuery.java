package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_RoomReservationReason;
import ru.protei.portal.core.model.struct.DateRange;
import ru.protei.portal.core.model.view.filterwidget.FilterQuery;

import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class RoomReservationQuery extends BaseQuery implements FilterQuery {

    private Set<Long> roomIds;
    private Set<Long> personRequesterIds;
    private Set<Long> personResponsibleIds;
    private Set<En_RoomReservationReason> reasons;
    private DateRange date;

    public RoomReservationQuery() {}

    public Set<Long> getRoomIds() {
        return roomIds;
    }

    public void setRoomIds(Set<Long> roomIds) {
        this.roomIds = roomIds;
    }

    public Set<Long> getPersonRequesterIds() {
        return personRequesterIds;
    }

    public void setPersonRequesterIds(Set<Long> personRequesterIds) {
        this.personRequesterIds = personRequesterIds;
    }

    public Set<Long> getPersonResponsibleIds() {
        return personResponsibleIds;
    }

    public void setPersonResponsibleIds(Set<Long> personResponsibleIds) {
        this.personResponsibleIds = personResponsibleIds;
    }

    public Set<En_RoomReservationReason> getReasons() {
        return reasons;
    }

    public void setReasons(Set<En_RoomReservationReason> reasons) {
        this.reasons = reasons;
    }

    public DateRange getDate() {
        return date;
    }

    public void setDate(DateRange date) {
        this.date = date;
    }

    @Override
    public String toString() {
        return "RoomReservationQuery{" +
                "roomIds=" + roomIds +
                ", personRequesterIds=" + personRequesterIds +
                ", personResponsibleIds=" + personResponsibleIds +
                ", reasons=" + reasons +
                ", date=" + date +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RoomReservationQuery)) return false;
        RoomReservationQuery that = (RoomReservationQuery) o;
        return Objects.equals(roomIds, that.roomIds) &&
                Objects.equals(personRequesterIds, that.personRequesterIds) &&
                Objects.equals(personResponsibleIds, that.personResponsibleIds) &&
                Objects.equals(reasons, that.reasons) &&
                Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(roomIds, personRequesterIds, personResponsibleIds, reasons, date);
    }
}
