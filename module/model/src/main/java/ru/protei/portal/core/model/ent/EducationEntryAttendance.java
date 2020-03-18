package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;
import java.util.Date;

@JdbcEntity(table = "education_entry_attendance")
public class EducationEntryAttendance implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="education_entry_id")
    private Long educationEntryId;

    @JdbcColumn(name="worker_entry_id")
    private Long workerId;

    @JdbcColumn(name="charged")
    private boolean charged;

    @JdbcColumn(name="date_requested")
    private Date dateRequested;

    @JdbcJoinedColumn(mappedColumn = "displayShortName", joinPath = {
            @JdbcJoinPath(localColumn = "worker_entry_id", table = "worker_entry", remoteColumn = "id"),
            @JdbcJoinPath(localColumn = "personId", table = "Person", remoteColumn = "id")
    })
    private String workerName;

    @JdbcJoinedColumn(mappedColumn="coins", localColumn="education_entry_id", table="education_entry", remoteColumn="id")
    private Integer coins;

    public EducationEntryAttendance() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getEducationEntryId() {
        return educationEntryId;
    }

    public void setEducationEntryId(Long educationEntryId) {
        this.educationEntryId = educationEntryId;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public boolean isCharged() {
        return charged;
    }

    public void setCharged(boolean charged) {
        this.charged = charged;
    }

    public Date getDateRequested() {
        return dateRequested;
    }

    public void setDateRequested(Date dateRequested) {
        this.dateRequested = dateRequested;
    }

    public String getWorkerName() {
        return workerName;
    }

    public Integer getCoins() {
        return coins;
    }

    @Override
    public String toString() {
        return "EducationEntryAttendance{" +
                "id=" + id +
                ", educationEntryId=" + educationEntryId +
                ", workerId=" + workerId +
                ", charged=" + charged +
                ", dateRequested=" + dateRequested +
                ", workerName='" + workerName + '\'' +
                ", coins=" + coins +
                '}';
    }
}
