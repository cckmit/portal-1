package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTrackIssueStateChange implements Serializable {

    private Long removedId;
    private Long addedId;
    private Long timestamp;
    private String authorLogin;
    private String authorFullName;

    public Long getRemovedId() {
        return removedId;
    }

    public void setRemovedId(Long removedId) {
        this.removedId = removedId;
    }

    public Long getAddedId() {
        return addedId;
    }

    public void setAddedId(Long addedId) {
        this.addedId = addedId;
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
                "removedId=" + removedId +
                ", addedId=" + addedId +
                ", timestamp=" + timestamp +
                ", authorLogin='" + authorLogin + '\'' +
                ", authorFullName='" + authorFullName + '\'' +
                '}';
    }
}
