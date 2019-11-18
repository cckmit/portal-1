package ru.protei.portal.core.model.struct;

import java.io.Serializable;

public class JiraMetaData implements Serializable {

    private String issueType;
    private String severity;
    private Long slaMapId;
    private String url;

    public JiraMetaData() {}

    public JiraMetaData(String issueType, String severity, Long slaMapId, String url) {
        this.issueType = issueType;
        this.severity = severity;
        this.slaMapId = slaMapId;
        this.url = url;
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

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public String toString() {
        return "JiraMetaData{" +
                "issueType='" + issueType + '\'' +
                ", severity='" + severity + '\'' +
                ", url='" + url + '\'' +
                ", slaMapId=" + slaMapId +
                '}';
    }
}
