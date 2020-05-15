package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_RoomReservationReason;

import java.util.Date;
import java.util.Set;

public class RoomReservationQuery extends BaseQuery {

    private Set<Long> roomIds;
    private Set<Long> personRequesterIds;
    private Set<Long> personResponsibleIds;
    private Set<En_RoomReservationReason> reasons;
    private Date dateStart;
    private Date dateEnd;

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

    public Date getDateStart() {
        return dateStart;
    }

    public void setDateStart(Date dateStart) {
        this.dateStart = dateStart;
    }

    public Date getDateEnd() {
        return dateEnd;
    }

    public void setDateEnd(Date dateEnd) {
        this.dateEnd = dateEnd;
    }

    @Override
    public String toString() {
        return "RoomReservationQuery{" +
                "roomIds=" + roomIds +
                ", personRequesterIds=" + personRequesterIds +
                ", personResponsibleIds=" + personResponsibleIds +
                ", reasons=" + reasons +
                ", dateStart=" + dateStart +
                ", dateEnd=" + dateEnd +
                '}';
    }
}
