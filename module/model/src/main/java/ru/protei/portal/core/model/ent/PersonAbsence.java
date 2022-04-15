package ru.protei.portal.core.model.ent;

import ru.protei.portal.core.model.dict.En_AbsenceReason;
import ru.protei.portal.core.model.dto.ScheduleItem;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.model.view.PersonShortView;
import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
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

    @JdbcColumn(name = "person_id")
    private Long personId;

    @JdbcJoinedColumn(localColumn = "person_id", remoteColumn = "id", table = "person", mappedColumn = "displayname", sqlTableAlias = "pa")
    private String personDisplayName;

    @JdbcColumn(name = "reason_id")
    @JdbcEnumerated(EnumType.ID)
    private En_AbsenceReason reason;

    @JdbcColumn(name = "from_time")
    private Date fromTime;

    @JdbcColumn(name = "till_time")
    private Date tillTime;

    @JdbcColumn(name = "user_comment")
    private String userComment;

    @JdbcColumn(name = "created_from_1c")
    private boolean createdFrom1C;

    @JdbcColumn(name = "schedule", converterType = ConverterType.JSON)
    private List<ScheduleItem> scheduleItems;

    public static final String AUDIT_TYPE = "PersonAbsence";

    public PersonAbsence() {}

    public PersonAbsence(Long personId, String personDisplayName) {
        this.personId = personId;
        this.personDisplayName = personDisplayName;
    }

    public PersonAbsence(Long id, Long personId, En_AbsenceReason reason, Date fromTime, Date tillTime) {
        this.id = id;
        this.personId = personId;
        this.reason = reason;
        this.fromTime = fromTime;
        this.tillTime = tillTime;
    }

    public PersonAbsence(PersonAbsence absence) {
        this.id = absence.getId();
        this.created = absence.getCreated();
        this.creatorId = absence.getCreatorId();
        this.personId = absence.getPersonId();
        this.personDisplayName = absence.getPersonDisplayName();
        this.reason = absence.getReason();
        this.fromTime = absence.getFromTime();
        this.tillTime = absence.getTillTime();
        this.userComment = absence.getUserComment();
        this.createdFrom1C = absence.isCreatedFrom1C();
        this.scheduleItems = absence.getScheduleItems();
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

    public String getPersonDisplayName() {
        return personDisplayName;
    }

    public void setPersonDisplayName(String personDisplayName) {
        this.personDisplayName = personDisplayName;
    }

    public PersonShortView getPerson() {
        if (personId == null)
            return null;
        return new PersonShortView(personDisplayName, personId);
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

    public boolean isCreatedFrom1C() {
        return createdFrom1C;
    }

    public void setCreatedFrom1C(boolean createdFrom1C) {
        this.createdFrom1C = createdFrom1C;
    }

    public List<ScheduleItem> getScheduleItems() {
        return scheduleItems;
    }

    public void setScheduleItems(List<ScheduleItem> scheduleItems) {
        this.scheduleItems = scheduleItems;
    }

    public boolean isScheduledAbsence() {
        return CollectionUtils.isEmpty(scheduleItems);
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

    @Override
    public String toString() {
        return "PersonAbsence{" +
                "id=" + id +
                ", created=" + created +
                ", creatorId=" + creatorId +
                ", personId=" + personId +
                ", personDisplayName='" + personDisplayName + '\'' +
                ", reason=" + reason +
                ", fromTime=" + fromTime +
                ", tillTime=" + tillTime +
                ", userComment='" + userComment + '\'' +
                ", createdFrom1C=" + createdFrom1C +
                '}';
    }
}
