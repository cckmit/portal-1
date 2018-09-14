package ru.protei.portal.core.service;

import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.WorkItem;
import ru.protei.portal.core.model.yt.YtAttachment;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface YoutrackService {
    Issue getIssueDetails(String issueId);

    List<YtAttachment> getIssueAttachments(String issueId);

    ChangeResponse getIssueChanges(String issueId);

    WorkItem[] getIssueWorkItems(String issueId);

    String createIssue(String project, String summary, String description);

    Set<String> getIssueIdsByProjectAndUpdatedAt(String projectId, Date updated);

    Set<String> getIssueIdsByProject(String projectId);

    Set<String> getIssueIdsByProjectAndUpdatedAfter(String projectId, Date updatedAfter);
}
