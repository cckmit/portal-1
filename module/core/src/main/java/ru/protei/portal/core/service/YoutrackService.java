package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.youtrack.dto.issue.YtIssueComment;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface YoutrackService {

    Result<List<YouTrackIssueStateChange>> getIssueStateChanges(String issueId);

    Result<String> createIssue( String projectName, String summary, String description);

    Result<String> createFireWorkerIssue(String summary, String description);

    Result<String> createCompany(String companyName);

    Result<String> updateCompanyName(String companyId, String companyName);

    Result<String> updateCompanyArchived(String companyId, Boolean isArchived);

    Result<String> getCompanyByName(String companyName);

    Result<Set<String>> getIssueIdsByProjectAndUpdatedAfter( String projectName, Date updatedAfter);

    Result<YouTrackIssueInfo> getIssueInfo( String issueId );

    Result<YouTrackIssueInfo> setIssueCrmNumbers(String issueId, List<Long> caseNumbers);

    Result<YouTrackIssueInfo> setIssueProjectNumbers(String issueId, List<Long> caseNumbersFromDB);

    Result<YouTrackIssueInfo> addIssueSystemComment(String issueNumber, String text);

    Result<CaseComment> convertYtIssueComment(YtIssueComment issueComment);

    Result<List<YoutrackProject>> getProjects(AuthToken token, int offset, int limit);
}
