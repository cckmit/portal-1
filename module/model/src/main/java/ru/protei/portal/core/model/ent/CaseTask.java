package ru.protei.portal.core.model.ent;

import ru.protei.winter.jdbc.annotations.IdInsertMode;
import ru.protei.winter.jdbc.annotations.JdbcColumn;
import ru.protei.winter.jdbc.annotations.JdbcEntity;
import ru.protei.winter.jdbc.annotations.JdbcId;

import java.util.Date;

/**
 * Created by michael on 20.05.16.
 */
@JdbcEntity(table = "case_task")
public class CaseTask {

    @JdbcId(name = "id", idInsertMode = IdInsertMode.AUTO)
    private Long id;

    @JdbcColumn(name = "created")
    private Date created;

    @JdbcColumn(name = "case_id")
    private Long caseId;

    @JdbcColumn(name = "task_info")
    private String taskInfo;

    @JdbcColumn(name = "time_unit")
    private char timeUnit;

    @JdbcColumn(name = "EST_PERSON_ID")
    private Long estPersonId;

    @JdbcColumn(name = "EST_TIME")
    private Long estTime;

    @JdbcColumn(name = "STARTED")
    private Date started;

    @JdbcColumn(name = "COMPLETED")
    private Date completed;

    @JdbcColumn(name = "WORKER_ID")
    private Long workerId;

    @JdbcColumn(name= "WORKTIME")
    private Long workTime;

    @JdbcColumn(name = "remain_time")
    private Long remainTime;


    public CaseTask() {
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

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseId(Long caseId) {
        this.caseId = caseId;
    }

    public String getTaskInfo() {
        return taskInfo;
    }

    public void setTaskInfo(String taskInfo) {
        this.taskInfo = taskInfo;
    }

    public char getTimeUnit() {
        return timeUnit;
    }

    public void setTimeUnit(char timeUnit) {
        this.timeUnit = timeUnit;
    }

    public Long getEstPersonId() {
        return estPersonId;
    }

    public void setEstPersonId(Long estPersonId) {
        this.estPersonId = estPersonId;
    }

    public Long getEstTime() {
        return estTime;
    }

    public void setEstTime(Long estTime) {
        this.estTime = estTime;
    }

    public Date getStarted() {
        return started;
    }

    public void setStarted(Date started) {
        this.started = started;
    }

    public Date getCompleted() {
        return completed;
    }

    public void setCompleted(Date completed) {
        this.completed = completed;
    }

    public Long getWorkerId() {
        return workerId;
    }

    public void setWorkerId(Long workerId) {
        this.workerId = workerId;
    }

    public Long getWorkTime() {
        return workTime;
    }

    public void setWorkTime(Long workTime) {
        this.workTime = workTime;
    }

    public Long getRemainTime() {
        return remainTime;
    }

    public void setRemainTime(Long remainTime) {
        this.remainTime = remainTime;
    }
}
