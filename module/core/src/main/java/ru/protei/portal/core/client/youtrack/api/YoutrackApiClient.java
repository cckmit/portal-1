package ru.protei.portal.core.client.youtrack.api;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.yt.api.issue.YtIssue;

public interface YoutrackApiClient {

    Result<YtIssue> getIssue(String issueId);

    Result<YtIssue> setCrmNumber(String issueId, Long caseNumber);

    Result<YtIssue> removeCrmNumber(String issueId);
}
