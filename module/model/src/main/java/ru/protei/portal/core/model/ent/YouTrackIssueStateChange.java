package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTrackIssueStateChange implements Serializable {

    private Long removedCaseStateId;
    private Long addedCaseStateId;
    private Long timestamp;
    private String authorLogin;
    private String authorFullName;

    public Long getRemovedCaseStateId() {
        return removedCaseStateId;
    }

    public void setRemovedCaseStateId(Long removedCaseStateId) {
        this.removedCaseStateId = removedCaseStateId;
    }

    public Long getAddedCaseStateId() {
        return addedCaseStateId;
    }

    public void setAddedCaseStateId(Long addedCaseStateId) {
        this.addedCaseStateId = addedCaseStateId;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public String getAuthorLogin() {
        return authorLogin;
    }

    public void setAuthorLogin(String authorLogin) {
        this.authorLogin = authorLogin;
    }

    public String getAuthorFullName() {
        return authorFullName;
    }

    public void setAuthorFullName(String authorFullName) {
        this.authorFullName = authorFullName;
    }

    @Override
    public String toString() {
        return "YouTrackIssueStateChange{" +
                "removedId=" + removedCaseStateId +
                ", addedId=" + addedCaseStateId +
                ", timestamp=" + timestamp +
                ", authorLogin='" + authorLogin + '\'' +
                ", authorFullName='" + authorFullName + '\'' +
                '}';
    }
}
