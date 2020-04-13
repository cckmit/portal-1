package ru.protei.portal.core.client.youtrack.api;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.youtrack.YtFieldDescriptor;
import ru.protei.portal.core.model.youtrack.dto.activity.YtActivityItem;
import ru.protei.portal.core.model.youtrack.dto.bundleelemenet.YtStateBundleElement;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssue;
import ru.protei.portal.core.model.youtrack.dto.project.YtProject;

import java.util.List;

public interface YoutrackApi {

    Result<YtIssue> createIssueAndReturnId(YtIssue issue);

    Result<YtIssue> getIssueWithFieldsCommentsAttachments(String issueId);

    Result<YtIssue> updateIssueAndReturnWithFieldsCommentsAttachments(String issueId, YtIssue issue);

    Result<YtIssue> removeIssueFieldAndReturnWithFieldsCommentsAttachments(String issueId, YtIssue issue, YtFieldDescriptor...fieldNamesToRemove);

    Result<List<YtProject>> getProjectIdByName(String projectName);

    Result<List<YtIssue>> getIssueIdsByQuery(String query);

    Result<List<YtActivityItem>> getIssueCustomFieldActivityItems(String issueId);

    Result<YtStateBundleElement> createCompany (YtStateBundleElement company);

    Result<YtStateBundleElement> updateCompanyName (String companyId, YtStateBundleElement company);

    Result<List<YtStateBundleElement>> getCompanyByName (String companyName);
}
