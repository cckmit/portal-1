package ru.protei.portal.core.client.youtrack.api;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.youtrack.dto.activity.YtActivityCategory;
import ru.protei.portal.core.model.youtrack.dto.activity.YtActivityItem;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueAttachment;
import ru.protei.portal.core.model.youtrack.dto.project.YtProject;

import java.util.Date;
import java.util.List;

public interface YoutrackApiClient {

    Result<YtIssue> createIssue(String projectName, String summary, String description);

    Result<YtIssue> getIssue(String issueId);

    Result<YtIssue> setCrmNumber(String issueId, Long caseNumber);

    Result<YtIssue> removeCrmNumber(String issueId);

    Result<List<YtIssueAttachment>> getIssueAttachments(String issueId);

    Result<List<YtProject>> getProjectsByName(String projectName);

    Result<YtProject> getProjectByName(String projectName);

    Result<List<YtIssue>> getIssuesByProjectAndUpdated(String projectName, Date updatedAfter);

    Result<List<YtActivityItem>> getIssueActivityChanges(String issueId, YtActivityCategory...activityCategories);
}
