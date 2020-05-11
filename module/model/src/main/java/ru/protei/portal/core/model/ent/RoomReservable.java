package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.io.Serializable;

@JdbcEntity(table = "room_reservable")
public class RoomReservable implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="name")
    private String name;

    @JdbcColumn(name="active")
    private boolean active;

    @JdbcColumn(name="restricted")
    private boolean restricted;

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

    public boolean isRestricted() {
        return restricted;
    }

    @Override
    public String toString() {
        return "RoomReservable{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", active=" + active +
                ", restricted=" + restricted +
                '}';
    }
}
