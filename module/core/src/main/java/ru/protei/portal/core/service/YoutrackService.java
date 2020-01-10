package ru.protei.portal.core.service;

import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.ent.YouTrackIssueStateChange;
import ru.protei.portal.core.model.yt.dto.activity.YtActivityItem;
import ru.protei.portal.core.model.yt.dto.issue.YtIssue;
import ru.protei.portal.core.model.yt.dto.issue.YtIssueAttachment;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

public interface YoutrackService {

    Result<List<YtIssueAttachment>> getIssueAttachments(String issueId);

    Result<List<YtActivityItem>> getIssueCustomFieldsChanges(String issueId);

    Result<List<YouTrackIssueStateChange>> getIssueStateChanges(String issueId);

    Result<String> createIssue( String projectName, String summary, String description);

    Result<Set<String>> getIssueIdsByProjectAndUpdatedAfter( String projectName, Date updatedAfter);

    Result<YouTrackIssueInfo> getIssueInfo( String issueId );

    /**
     * Установить caseNumber только если номер crm в youtrack не равен caseNumber
     * (Не затирать историю изменений youtrack)
     */
    Result<YouTrackIssueInfo> setIssueCrmNumberIfDifferent(String issueId, Long caseNumber);

    /**
     * Удалить caseNumber только если номер crm в youtrack равен caseNumber
     * (Не затирать историю изменений youtrack)
     */
    Result<YouTrackIssueInfo> removeIssueCrmNumberIfSame(String youtrackId, Long caseNumber);

    @Async(BACKGROUND_TASKS)
    void mergeYouTrackLinks( Long caseNumber, List<String> added, List<String> removed );
}
