package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.*;

import java.io.Serializable;

@JdbcEntity(table = "education_entry_attendance")
public class EducationEntryAttendance implements Serializable {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name="education_entry_id")
    private String educationEntryId;

    @JdbcColumn(name="worker_entry_id")
    private String workerId;

    @JdbcColumn(name="approved")
    private boolean approved;

    @JdbcJoinedColumn(mappedColumn = "displayShortName", joinPath = {
            @JdbcJoinPath(localColumn = "worker_entry_id", table = "worker_entry", remoteColumn = "id"),
            @JdbcJoinPath(localColumn = "personId", table = "Person", remoteColumn = "id")
    })
    private String workerName;

    public EducationEntryAttendance() {}

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getEducationEntryId() {
        return educationEntryId;
    }

    public void setEducationEntryId(String educationEntryId) {
        this.educationEntryId = educationEntryId;
    }

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public boolean isApproved() {
        return approved;
    }

    public void setApproved(boolean approved) {
        this.approved = approved;
    }

    public String getWorkerName() {
        return workerName;
    }

    @Override
    public String toString() {
        return "EducationEntryAttendance{" +
                "id=" + id +
                ", educationEntryId='" + educationEntryId + '\'' +
                ", workerId='" + workerId + '\'' +
                ", approved=" + approved +
                ", workerName='" + workerName + '\'' +
                '}';
    }
}
