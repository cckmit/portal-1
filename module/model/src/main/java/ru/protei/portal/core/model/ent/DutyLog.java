package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.DutyType;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Запись журнала дежурств
 */
@JdbcEntity(table = "duty_log")
public class DutyLog implements Serializable{

    @JdbcId
    private Long id;

    @JdbcColumn(name = "date_from")
    private Date from;

    @JdbcColumn
    private Date created;

    @JdbcColumn(name = "date_to")
    private Date to;

    @JdbcColumn(name = "creator_id")
    private Long creatorId;

    @JdbcColumn
    @JdbcEnumerated(EnumType.ID)
    private DutyType type;

    @JdbcColumn(name = "person_id")
    private Long personId;

    @JdbcJoinedColumn(localColumn = "person_id", remoteColumn = "id", table = "person", mappedColumn = "displayname")
    private String personDisplayName;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getFrom() {
        return from;
    }

    public void setFrom(Date from) {
        this.from = from;
    }

    public Date getTo() {
        return to;
    }

    public void setTo(Date to) {
        this.to = to;
    }

    public DutyType getType() {
        return type;
    }

    public void setType(DutyType type) {
        this.type = type;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public String getPersonDisplayName() {
        return personDisplayName;
    }

    public void setPersonDisplayName(String personDisplayName) {
        this.personDisplayName = personDisplayName;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public boolean isValid() {
        return from != null && to != null && to.after(from)
                && type != null && personId != null;
    }

    @Override
    public String toString() {
        return "DutyLog{" +
                "id=" + id +
                ", from=" + from +
                ", created=" + created +
                ", to=" + to +
                ", type=" + type +
                ", personId=" + personId +
                ", personDisplayName='" + personDisplayName + '\'' +
                '}';
    }
}
