package ru.protei.portal.core.dao;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.YtAttachment;

import java.util.Date;
import java.util.List;

public interface YoutrackRestDAO {

    @Deprecated
    CoreResponse<Issue> getIssue( String issueId );
    @Deprecated
    CoreResponse<List<YtAttachment>> getIssueAttachments(String issueId);
    @Deprecated
    CoreResponse<ChangeResponse> getIssueChanges(String issueId);
    @Deprecated
    CoreResponse<String> createIssue(String project, String summary, String description);
    @Deprecated
    CoreResponse<List<Issue>> getIssuesByProjectAndUpdated( String projectId, Date updatedAfter);
    @Deprecated
    CoreResponse<String> removeCrmNumber( String issueId );
    @Deprecated
    CoreResponse<String> setCrmNumber( String issueId, Long caseNumber );
}
