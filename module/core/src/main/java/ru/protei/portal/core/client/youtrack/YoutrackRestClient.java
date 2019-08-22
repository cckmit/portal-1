package ru.protei.portal.core.client.youtrack;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.YtAttachment;
import ru.protei.portal.core.model.yt.api.IssueApi;

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
    CoreResponse<Issue> getIssue( String issueId );

    /**
     * @deprecated Переход на {@link YoutrackApiClient}
     */
    @Deprecated
    CoreResponse<List<YtAttachment>> getIssueAttachments( String issueId );

    /**
     * @deprecated Переход на {@link YoutrackApiClient}
     */
    @Deprecated
    CoreResponse<ChangeResponse> getIssueChanges( String issueId );

    /**
     * @deprecated Переход на {@link YoutrackApiClient}
     */
    @Deprecated
    CoreResponse<String> createIssue( String project, String summary, String description );

    /**
     * @deprecated Переход на {@link YoutrackApiClient}
     */
    @Deprecated
    CoreResponse<List<Issue>> getIssuesByProjectAndUpdated( String projectId, Date updatedAfter );

    /**
     * @deprecated Переход на {@link YoutrackApiClient#removeCrmNumber(IssueApi)}
     *
     */
    @Deprecated
    CoreResponse<String> removeCrmNumber( String issueId );

    /**
     * @deprecated Переход на {@link YoutrackApiClient#setCrmNumber(IssueApi, Long)}
     */
    @Deprecated
    CoreResponse<String> setCrmNumber( String issueId, Long caseNumber );
}
