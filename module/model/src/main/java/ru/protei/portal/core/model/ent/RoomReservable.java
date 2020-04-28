package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.List;

@JdbcEntity(table = "room_reservable")
public class RoomReservable implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="name")
    private String name;

    @JdbcColumn(name="active")
    private boolean active;

    @JdbcColumn(name="restriction_message")
    private String restrictionMessage;

    @JdbcManyToMany(linkTable = "room_reservable_allowed_persons", localLinkColumn = "room_reservable_id", remoteLinkColumn = "person_id")
    private List<Person> personsAllowedToReserve;

    public RoomReservable() {
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public boolean isActive() {
        return active;
    }

    public String getRestrictionMessage() {
        return restrictionMessage;
    }

    public List<Person> getPersonsAllowedToReserve() {
        return personsAllowedToReserve;
    }

    @Override
    public String toString() {
        return "RoomReservable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", restrictionMessage='" + restrictionMessage + '\'' +
                ", personsAllowedToReserve=" + personsAllowedToReserve +
                '}';
    }
}
