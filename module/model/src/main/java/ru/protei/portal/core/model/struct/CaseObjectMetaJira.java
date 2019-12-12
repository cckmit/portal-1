package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.ent.CaseObject;

import java.io.Serializable;

public class CaseObjectMetaJira implements Serializable {

    private Long id;

    // --------------------

    private String issueType;
    private String severity;
    private Long slaMapId;

    // --------------------

    public CaseObjectMetaJira() {}

    public CaseObjectMetaJira(String issueType, String severity, Long slaMapId) {
        setIssueType(issueType);
        setSeverity(severity);
        setSlaMapId(slaMapId);
    }

    public CaseObjectMetaJira(CaseObject co) {
        setId(co.getId());
        CaseObjectMetaJira jira = co.getCaseObjectMetaJira();
        if (jira == null) {
            return;
        }
        setIssueType(jira.getIssueType());
        setSeverity(jira.getSeverity());
        setSlaMapId(jira.getSlaMapId());
    }

//    public CaseObject collectToCaseObject(CaseObject co) {
//        co.setId(getId());
//        co.setCaseObjectMetaJira(this);
//        return co;
//    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getIssueType() {
        return issueType;
    }

    public void setIssueType(String issueType) {
        this.issueType = issueType;
    }

    public String getSeverity() {
        return severity;
    }

    public void setSeverity(String severity) {
        this.severity = severity;
    }

    public Long getSlaMapId() {
        return slaMapId;
    }

    public void setSlaMapId(Long slaMapId) {
        this.slaMapId = slaMapId;
    }

    @Override
    public String toString() {
        return "CaseObjectMetaJira{" +
                "id=" + id +
                ", issueType='" + issueType + '\'' +
                ", severity='" + severity + '\'' +
                ", slaMapId=" + slaMapId +
                '}';
    }
}
