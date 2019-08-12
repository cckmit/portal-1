package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.YtAttachment;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface YoutrackService {
    CoreResponse<List<YtAttachment>> getIssueAttachments(String issueId);

    CoreResponse<ChangeResponse> getIssueChanges(String issueId);

    CoreResponse<String> createIssue(String project, String summary, String description);

    CoreResponse<Set<String>> getIssueIdsByProjectAndUpdatedAfter(String projectId, Date updatedAfter);

    CoreResponse<YouTrackIssueInfo> getIssueInfo( String issueId );

    /**
     * Установить caseNumber только если номер не равен caseNumber
     * (Не затирать историю изменений youtrack)
     */
    CoreResponse<String> compareAndSetIssueCrmNumber( String issueId, Long caseNumber );

    /**
     * Обновить caseNumber только если номер не равен caseNumber
     * (Не затирать историю изменений youtrack)
     */
    CoreResponse<String> compareAndUpdateIssueCrmNumber( String issueId, Long caseNumber );

    /**
     * Удалить caseNumber только если номер равен caseNumber
     * (Не затирать историю изменений youtrack)
     */
    CoreResponse<String> compareAndRemoveIssueCrmNumber( String youtrackId, Long caseNumber );
}
