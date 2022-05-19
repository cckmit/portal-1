package ru.protei.portal.core.model.struct.caseobjectreport;

import ru.protei.portal.core.model.dict.En_TimeElapsedType;

public class CaseObjectReportTimeElapsedGroupRow implements CaseObjectReportRow {
    private Long timeElapsed;
    private En_TimeElapsedType timeElapsedType;
    private String employeeDepartment;
    private String employeeName;

    public CaseObjectReportTimeElapsedGroupRow() {
    }

    public CaseObjectReportTimeElapsedGroupRow(Long timeElapsed, En_TimeElapsedType timeElapsedType, String employeeDepartment, String employeeName) {
        this.timeElapsed = timeElapsed;
        this.timeElapsedType = timeElapsedType;
        this.employeeDepartment = employeeDepartment;
        this.employeeName = employeeName;
    }

    public CaseObjectReportTimeElapsedGroupRow(Long timeElapsed, En_TimeElapsedType timeElapsedType, String employeeName) {
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
                ", timeElapsedType=" + timeElapsedType +
                ", employeeDepartment='" + employeeDepartment + '\'' +
                ", employeeName='" + employeeName + '\'' +
                '}';
    }
}
