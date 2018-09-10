package ru.protei.portal.core.service;

import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.WorkItem;
import ru.protei.portal.core.model.yt.YtAttachment;

import java.util.List;

public interface YoutrackService {
    Issue getIssueDetails(String issueId);

    List<YtAttachment> getIssueAttachments(String issueId);

    ChangeResponse getIssueChanges(String issueId);

    WorkItem[] getIssueWorkItems(String issueId);

    String createIssue(String project, String summary, String description);
}
