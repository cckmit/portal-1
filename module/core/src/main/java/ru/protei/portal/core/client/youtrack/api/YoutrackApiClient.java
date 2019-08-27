package ru.protei.portal.core.client.youtrack.api;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.yt.api.IssueApi;

public interface YoutrackApiClient {

    CoreResponse<IssueApi> getIssue( String issueId );

    CoreResponse<String> removeCrmNumber( IssueApi issueId );

    CoreResponse<String> setCrmNumber( IssueApi issue, Long caseNumber );
}
