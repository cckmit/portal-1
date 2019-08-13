package ru.protei.portal.core.model.dto;

import java.util.Date;

public class CaseResolutionTimeReportDto {

    private Date created;
    private Long caseId;
    private Long caseStateId;
    private long caseNumber;

    public void setCaseId( Long case_id ) {
        caseId = case_id;
    }

    public Long getCaseId() {
        return caseId;
    }

    public void setCaseStateId( Long caseStateId ) {
        this.caseStateId = caseStateId;
    }

    public Long getCaseStateId() {
        return caseStateId;
    }

    public void setCreated( Date created ) {
        this.created = created;
    }

    public Date getCreated() {
        return created;
    }

    public void setCaseNumber( Long caseNumber ) {
        this.caseNumber = caseNumber;
    }

    public Long getCaseNumber() {
        return caseNumber;
    }
}
