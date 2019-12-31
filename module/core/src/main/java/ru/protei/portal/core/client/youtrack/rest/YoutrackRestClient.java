package ru.protei.portal.core.client.youtrack.rest;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.client.youtrack.api.YoutrackApiClient;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.YtAttachment;
import ru.protei.portal.core.model.yt.api.issue.YtIssue;

import java.util.Date;
import java.util.List;

/**
 * @deprecated Переход на {@link YoutrackApiClient}
 */
@Deprecated
public interface YoutrackRestClient {

    /**
     * Задача на youtrack
     * @deprecated Переход на {@link YoutrackApiClient#getIssue(String)}
     */
    @Deprecated
    Result<Issue> getIssue( String issueId );

    /**
     * @deprecated Переход на {@link YoutrackApiClient}
     */
    @Deprecated
    Result<List<YtAttachment>> getIssueAttachments( String issueId );

    /**
     * @deprecated Переход на {@link YoutrackApiClient}
     */
    @Deprecated
    Result<ChangeResponse> getIssueChanges( String issueId );

    /**
     * @deprecated Переход на {@link YoutrackApiClient}
     */
    @Deprecated
    Result<String> createIssue( String project, String summary, String description );

    /**
     * @deprecated Переход на {@link YoutrackApiClient}
     */
    @Deprecated
    Result<List<Issue>> getIssuesByProjectAndUpdated( String projectId, Date updatedAfter );
}
