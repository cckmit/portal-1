package ru.protei.portal.core.model.struct.caseobjectreport;

import ru.protei.portal.core.model.dict.En_TimeElapsedType;

public class CaseObjectReportWork implements CaseObjectReportRow {
    private Long timeElapsed;
    private Long timeElapsedInSelectedDuration;
    private En_TimeElapsedType timeElapsedType;
    private String employeeDepartment;
    private String employeeName;

    public CaseObjectReportWork() {
    }

    public CaseObjectReportWork(Long timeElapsed, Long timeElapsedInSelectedDuration, En_TimeElapsedType timeElapsedType, String employeeDepartment, String employeeName) {
        this.timeElapsed = timeElapsed;
        this.timeElapsedInSelectedDuration = timeElapsedInSelectedDuration;
        this.timeElapsedType = timeElapsedType;
        this.employeeDepartment = employeeDepartment;
        this.employeeName = employeeName;
    }

    public CaseObjectReportWork(Long timeElapsed, En_TimeElapsedType timeElapsedType, String employeeName) {
        this.timeElapsed = timeElapsed;
        this.timeElapsedType = timeElapsedType;
        this.employeeName = employeeName;
    }

    public Long getTimeElapsed() {
        return timeElapsed;
    }

    public void setTimeElapsed(Long timeElapsed) {
        this.timeElapsed = timeElapsed;
    }

    public Long getTimeElapsedInSelectedDuration() {
        return timeElapsedInSelectedDuration;
    }

    public void setTimeElapsedInSelectedDuration(Long timeElapsedInSelectedDuration) {
        this.timeElapsedInSelectedDuration = timeElapsedInSelectedDuration;
    }

    public En_TimeElapsedType getTimeElapsedType() {
        return timeElapsedType;
    }

    public void setTimeElapsedType(En_TimeElapsedType timeElapsedType) {
        this.timeElapsedType = timeElapsedType;
    }

    public String getEmployeeDepartment() {
        return employeeDepartment;
    }

    public void setEmployeeDepartment(String employeeDepartment) {
        this.employeeDepartment = employeeDepartment;
    }

    public String getEmployeeName() {
        return employeeName;
    }

    public void setEmployeeName(String employeeName) {
        this.employeeName = employeeName;
    }

    @Override
    public String toString() {
        return "CaseObjectReportWork{" +
                "timeElapsed=" + timeElapsed +
                ", timeElapsedInSelectedDuration=" + timeElapsedInSelectedDuration +
                ", timeElapsedType=" + timeElapsedType +
                ", employeeDepartment='" + employeeDepartment + '\'' +
                ", employeeName='" + employeeName + '\'' +
                '}';
    }
}
