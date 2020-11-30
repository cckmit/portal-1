package ru.protei.portal.tools.migrate.struct;

import protei.sql.Column;
import protei.sql.PrimaryKey;
import protei.sql.Table;
import ru.protei.portal.core.model.ent.LegacyEntity;

import java.util.Date;

@Table(name="AbsentLog.Tm_AbsentViewer")
public class ExternalPersonAbsence implements LegacyEntity {

    @PrimaryKey
    @Column(name = "nID")
    private Long id;

    @Column(name = "dtCreation")
    private Date created;

    @Column(name = "nSubmitterID")
    private Long submitterID;

    @Column(name = "dFromDate")
    private Date fromDate;

    @Column(name = "dToDate")
    private Date toDate;

    @Column(name = "strComment")
    private String comment;

    @Column(name = "nPersonID")
    private Long personID;

    @Column(name = "nReasonID")
    private Long reasonID;

    @Column(name = "tFromTime")
    private Date fromTime;

    @Column(name = "tToTime")
    private Date toTime;

    public ExternalPersonAbsence() {}

    @Override
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

    public Long getSubmitterID() {
        return submitterID;
    }

    public void setSubmitterID(Long submitterID) {
        this.submitterID = submitterID;
    }

    public Date getFromDate() {
        return fromDate;
    }

    public void setFromDate(Date fromDate) {
        this.fromDate = fromDate;
    }

    public Date getToDate() {
        return toDate;
    }

    public void setToDate(Date toDate) {
        this.toDate = toDate;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public Long getPersonID() {
        return personID;
    }

    public void setPersonID(Long personID) {
        this.personID = personID;
    }

    public Long getReasonID() {
        return reasonID;
    }

    public void setReasonID(Long reasonID) {
        this.reasonID = reasonID;
    }

    public Date getFromTime() {
        return fromTime;
    }

    public void setFromTime(Date fromTime) {
        this.fromTime = fromTime;
    }

    public Date getToTime() {
        return toTime;
    }

    public void setToTime(Date toTime) {
        this.toTime = toTime;
    }
}
