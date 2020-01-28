package ru.protei.portal.core.model.ent;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import ru.protei.portal.core.model.dict.En_CaseState;

import java.io.Serializable;

@JsonIgnoreProperties(ignoreUnknown = true)
public class YouTrackIssueStateChange implements Serializable {

    private En_CaseState removed;
    private En_CaseState added;
    private Long timestamp;
    private String authorLogin;
    private String authorFullName;

    public En_CaseState getRemoved() {
        return removed;
    }

    public void setRemoved(En_CaseState removed) {
        this.removed = removed;
    }

    public En_CaseState getAdded() {
        return added;
    }

    public void setAdded(En_CaseState added) {
        this.added = added;
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
                "removed=" + removed +
                ", added=" + added +
                ", timestamp=" + timestamp +
                ", authorLogin='" + authorLogin + '\'' +
                ", authorFullName='" + authorFullName + '\'' +
                '}';
    }
}
