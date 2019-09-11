package ru.protei.portal.core.client.youtrack.api;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.yt.api.IssueApi;

public interface YoutrackApiClient {

    Result<IssueApi> getIssue( String issueId );

    Result<String> removeCrmNumber( IssueApi issueId );

    Result<String> setCrmNumber( IssueApi issue, Long caseNumber );
}
