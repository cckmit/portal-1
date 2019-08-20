package ru.protei.portal.core.dao;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.YtAttachment;

import java.util.Date;
import java.util.List;

public interface YoutrackRestDAO {

    CoreResponse<Issue> getIssue( String issueId );

    CoreResponse<List<YtAttachment>> getIssueAttachments(String issueId);

    CoreResponse<ChangeResponse> getIssueChanges(String issueId);

    CoreResponse<String> createIssue(String project, String summary, String description);

    CoreResponse<List<Issue>> getIssuesByProjectAndUpdated( String projectId, Date updatedAfter);

    CoreResponse<String> removeCrmNumber( String issueId );

    CoreResponse<String> setCrmNumber( String issueId, Long caseNumber );
}
