package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Date;

/**
 * Created by michael on 05.07.16.
 */
@JdbcEntity(table = "Person_Absence")
public class PersonAbsence {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "old_id")
    private Long old_id;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "updated")
    private Date updated;

    @JdbcColumn(name = "creator_id")
    private Long creatorId;


    @JdbcColumn(name = "person_id")
    private Long personId;

    @JdbcColumn(name = "reason_id")
    private int reasonId;

    @JdbcColumn(name = "from_time")
    private Date fromTime;

    @JdbcColumn(name = "till_time")
    private Date tillTime;

    @JdbcColumn(name = "user_comment")
    private String userComment;

    private String creator;

    public PersonAbsence() {
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

    public Long getPersonId() {
        return personId;
    }

    public void setPersonId(Long personId) {
        this.personId = personId;
    }

    public int getReasonId() {
        return reasonId;
    }

    public void setReasonId(int reasonId) {
        this.reasonId = reasonId;
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

    public Date getUpdated() {
        return updated;
    }

    public void setUpdated(Date updated) {
        this.updated = updated;
    }

    public String getCreator() {
        return creator;
    }

    public void setCreator(String creator) {
        this.creator = creator;
    }

    public Long getOldId() {
        return old_id;
    }

    public void setOldId(Long old_id) {
        this.old_id = old_id;
    }
}
