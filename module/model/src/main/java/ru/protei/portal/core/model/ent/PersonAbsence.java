package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

/**
 * Created by michael on 05.07.16.
 */
@JdbcEntity(table = "person_absence")
public class PersonAbsence extends AuditableObject implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "creator_id")
    private Long creatorId;

    @JdbcJoinedObject(localColumn="creator_id", remoteColumn = "id")
    private Person creator;

    @JdbcColumn(name = "person_id")
    private Long personId;

    @JdbcJoinedObject(localColumn="person_id", remoteColumn = "id", sqlTableAlias = "pa")
    private Person person;

    @JdbcColumn(name = "reason_id")
    @JdbcEnumerated(EnumType.ID)
    private En_AbsenceReason reason;

    @JdbcColumn(name = "from_time")
    private Date fromTime;

    @JdbcColumn(name = "till_time")
    private Date tillTime;

    @JdbcColumn(name = "user_comment")
    private String userComment;

    public static final String AUDIT_TYPE = "PersonAbsence";

    public PersonAbsence() {}

    public PersonAbsence(PersonAbsence absence) {
        this.id = absence.getId();
        this.created = absence.getCreated();
        this.creatorId = absence.getCreatorId();
        this.creator = absence.getCreator();
        this.personId = absence.getPersonId();
        this.person = absence.getPerson();
        this.reason = absence.getReason();
        this.fromTime = absence.getFromTime();
        this.tillTime = absence.getTillTime();
        this.userComment = absence.getUserComment();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }

    public Long getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(Long creatorId) {
        this.creatorId = creatorId;
    }

    public Person getCreator() {
        return creator;
    }

    public void setCreator(Person creator) {
        this.creator = creator;
    }

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public Person getPerson() {
        return person;
    }

    public void setPerson(Person person) {
        this.person = person;
        this.personId = person == null ? null : person.getId();
    }

    public En_AbsenceReason getReason() {
        return reason;
    }

    public void setReason(En_AbsenceReason reason) {
        this.reason = reason;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public Date getTillTime() {
        return tillTime;
    }

    public void setTillTime(Date tillTime) {
        this.tillTime = tillTime;
    }

    public String getUserComment() {
        return userComment;
    }

    public void setUserComment(String userComment) {
        this.userComment = userComment;
    }

    @Override
    public String getAuditType() {
        return AUDIT_TYPE;
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        PersonAbsence person = (PersonAbsence) o;
        return Objects.equals(id, person.id);
    }
}
