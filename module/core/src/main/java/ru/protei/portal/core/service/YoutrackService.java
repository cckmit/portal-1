package ru.protei.portal.core.service;

import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.YtAttachment;

import java.util.Date;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;

public interface YoutrackService {
    Result<List<YtAttachment>> getIssueAttachments( String issueId);

    Result<ChangeResponse> getIssueChanges( String issueId);

    Result<String> createIssue( String project, String summary, String description);

    Result<Set<String>> getIssueIdsByProjectAndUpdatedAfter( String projectId, Date updatedAfter);

    Result<YouTrackIssueInfo> getIssueInfo( String issueId );

    /**
     * Установить caseNumber только если номер crm в youtrack не равен caseNumber
     * (Не затирать историю изменений youtrack)
     */
    Result<String> setIssueCrmNumberIfDifferent( String issueId, Long caseNumber );

    /**
     * Удалить caseNumber только если номер crm в youtrack равен caseNumber
     * (Не затирать историю изменений youtrack)
     */
    Result<String> removeIssueCrmNumberIfSame( String youtrackId, Long caseNumber );

    @Async(BACKGROUND_TASKS)
    Result<Void> mergeYouTrackLinks( Long caseNumber, List<String> added, List<String> removed );
}
