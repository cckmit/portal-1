package ru.protei.portal.core.model.yt;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

/**
 * Created by admin on 15/11/2017.
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class IssueResponse {
    private List<Issue> issue;

    public List< Issue > getIssue() {
        return issue;
    }

    public void setIssue( List< Issue > issue ) {
        this.issue = issue;
    }
}
