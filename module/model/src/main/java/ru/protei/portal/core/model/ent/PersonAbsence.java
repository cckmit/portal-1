package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

/**
 * Created by michael on 05.07.16.
 */
@JdbcEntity(table = "person_absence")
public class PersonAbsence implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "creator_id")
    private Long creatorId;

    @JdbcColumn(name = "person_id")
    private Long personId;

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

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
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
}
