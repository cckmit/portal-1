package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.YtAttachment;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface YoutrackService {
    List<YtAttachment> getIssueAttachments(String issueId);

    ChangeResponse getIssueChanges(String issueId);

    String createIssue(String project, String summary, String description);

    Set<String> getIssueIdsByProjectAndUpdatedAfter(String projectId, Date updatedAfter);

    CoreResponse<YouTrackIssueInfo> getIssueInfo( String issueId );

    void setIssueCrmNumber( String issueId, Long caseNumber );
}
