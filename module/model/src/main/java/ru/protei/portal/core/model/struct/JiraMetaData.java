package ru.protei.portal.core.model.struct;

import java.io.Serializable;

public class JiraMetaData implements Serializable {

    private String issueType;
    private String severity;
    private Long slaMapId;

    public JiraMetaData() {}

    public JiraMetaData(String issueType, String severity, Long slaMapId) {
        this.issueType = issueType;
        this.severity = severity;
        this.slaMapId = slaMapId;
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
        return "JiraMetaData{" +
                "issueType='" + issueType + '\'' +
                ", severity='" + severity + '\'' +
                ", slaMapId=" + slaMapId +
                '}';
    }
}
