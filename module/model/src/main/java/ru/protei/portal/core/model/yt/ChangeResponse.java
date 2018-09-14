package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ChangeResponse {
    private Issue issue;

    private List< Change > change;

    public Issue getIssue() {
        return issue;
    }

    public void setIssue(Issue issue) {
        this.issue = issue;
    }

    public List<Change> getChange() {
        return change;
    }

    public void setChange(List<Change> change) {
        this.change = change;
    }

    @Override
    public String toString() {
        return "ChangeResponse{" +
                "issue=" + issue +
                ", change=" + change +
                '}';
    }
}
