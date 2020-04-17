package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_RoomReservationReason;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

@JdbcEntity(table = "room_reservation")
public class RoomReservation implements Serializable {

    @JdbcId(name="id", idInsertMode=IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="date_requested")
    private Date dateRequested;

    @JdbcJoinedObject(localColumn="person_requester_id")
    private Person personRequester;

    @JdbcJoinedObject(localColumn="person_responsible_id")
    private Person personResponsible;

    @JdbcJoinedObject(localColumn="room_id")
    private RoomReservable room;

    @JdbcColumn(name="date_from")
    private Date dateFrom;

    @JdbcColumn(name="date_until")
    private Date dateUntil;

    @JdbcColumn(name="reason_id")
    @JdbcEnumerated(EnumType.ID)
    private En_RoomReservationReason reason;

    @JdbcColumn(name="coffee_break_count")
    private Integer coffeeBreakCount;

    @JdbcColumn(name="comment")
    private String comment;

    @JdbcManyToMany(linkTable = "room_reservation_notifiers", localLinkColumn = "room_reservation_id", remoteLinkColumn = "person_id")
    private List<Person> personsToBeNotified;

    public RoomReservation() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }

    public Person getPersonRequester() {
        return personRequester;
    }

    public void setPersonRequester(Person personRequester) {
        this.personRequester = personRequester;
    }

    public Person getPersonResponsible() {
        return personResponsible;
    }

    public void setPersonResponsible(Person personResponsible) {
        this.personResponsible = personResponsible;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public void setDateFrom(Date dateFrom) {
        this.dateFrom = dateFrom;
    }

    public Date getDateUntil() {
        return dateUntil;
    }

    public void setDateUntil(Date dateUntil) {
        this.dateUntil = dateUntil;
    }

    public RoomReservable getRoom() {
        return room;
    }

    public void setRoom(RoomReservable room) {
        this.room = room;
    }

    public En_RoomReservationReason getReason() {
        return reason;
    }

    public void setReason(En_RoomReservationReason reason) {
        this.reason = reason;
    }

    public Integer getCoffeeBreakCount() {
        return coffeeBreakCount;
    }

    public void setCoffeeBreakCount(Integer coffeeBreakCount) {
        this.coffeeBreakCount = coffeeBreakCount;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public List<Person> getPersonsToBeNotified() {
        return personsToBeNotified;
    }

    public void setPersonsToBeNotified(List<Person> personsToBeNotified) {
        this.personsToBeNotified = personsToBeNotified;
    }

    @Override
    public String toString() {
        return "RoomReservation{" +
                "id=" + id +
                ", dateRequested=" + dateRequested +
                ", personRequester=" + personRequester +
                ", personResponsible=" + personResponsible +
                ", dateFrom=" + dateFrom +
                ", dateUntil=" + dateUntil +
                ", room=" + room +
                ", reason=" + reason +
                ", coffeeBreakCount=" + coffeeBreakCount +
                ", comment='" + comment + '\'' +
                ", personsToBeNotified=" + personsToBeNotified +
                '}';
    }
}
