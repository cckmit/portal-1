package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.client.youtrack.api.YoutrackApiClient;
import ru.protei.portal.core.client.youtrack.YoutrackConstansMapping;
import ru.protei.portal.core.client.youtrack.rest.YoutrackRestClient;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.helper.NumberUtils;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.YtAttachment;
import ru.protei.portal.core.model.yt.api.customfield.issue.YtIssueCustomField;
import ru.protei.portal.core.model.yt.api.issue.YtIssue;
import ru.protei.portal.core.model.yt.fields.YtFields;

import java.util.*;
import java.util.stream.Collectors;

import static org.apache.commons.collections4.CollectionUtils.emptyIfNull;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.config.MainConfiguration.BACKGROUND_TASKS;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

/**
 * Created by admin on 15/11/2017.
 */
public class YoutrackServiceImpl implements YoutrackService {

    @Override
    public Result<ChangeResponse> getIssueChanges( String issueId ) {
        return restDao.getIssueChanges( issueId );
    }

    @Override
    public Result<List<YtAttachment>> getIssueAttachments( String issueId ) {
        return restDao.getIssueAttachments( issueId );
    }

    @Override
    public Result<String> createIssue( String project, String summary, String description ) {
        return restDao.createIssue( project, summary, description );
    }

    @Override
    public Result<Set<String>> getIssueIdsByProjectAndUpdatedAfter( String projectId, Date updatedAfter ) {
        return restDao.getIssuesByProjectAndUpdated( projectId, updatedAfter )
                .map( issues -> stream( issues ).map( Issue::getId )
                        .collect( Collectors.toSet() ) );
    }

    @Override
    public Result<YouTrackIssueInfo> getIssueInfo( String issueId ) {
        if (issueId == null) {
            log.warn( "getYoutrackIssueInfo(): Can't get issue info. Argument issueId is mandatory" );
            return error( En_ResultStatus.INCORRECT_PARAMS );
        }

        return restDao.getIssue( issueId ).map(
                this::convertToInfo );
    }

    @Override
    public Result<YtIssue> setIssueCrmNumberIfDifferent(String ytIssueId, Long caseNumber) {
        if (ytIssueId == null || caseNumber == null) {
            log.warn("setIssueCrmNumber(): Can't set youtrack issue crm number. All arguments are mandatory issueId={} caseNumber={}", ytIssueId, caseNumber);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return apiDao.getIssue(ytIssueId)
                .flatMap(issue -> {
                    YtIssueCustomField field = issue.getField(YtFields.crmNumber);
                    Long crmNumber = field == null ? null : NumberUtils.parseLong(field.getValue());
                    return replaceCrmNumberIfDifferent(issue.idReadable, crmNumber, caseNumber);
                });
    }

    @Override
    public Result<YtIssue> removeIssueCrmNumberIfSame(String ytIssueId, Long caseNumber) {
        if (ytIssueId == null || caseNumber == null) {
            log.warn("removeIssueCrmNumber(): Can't remove youtrack issue crm number. All arguments are mandatory issueId={} caseNumber={}", ytIssueId, caseNumber);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        return apiDao.getIssue(ytIssueId)
                .flatMap(issue -> {
                    YtIssueCustomField field = issue.getField(YtFields.crmNumber);
                    Long crmNumber = field == null ? null : NumberUtils.parseLong(field.getValue());
                    return removeCrmNumberIfSame(issue.idReadable, crmNumber, caseNumber);
                });
    }

    @Async(BACKGROUND_TASKS)
    @Override
    public void mergeYouTrackLinks( Long caseNumber, List<String> added, List<String> removed ) {

        for (String youtrackId : emptyIfNull( removed )) {
            removeIssueCrmNumberIfSame( youtrackId, caseNumber);
        }

        for (String youtrackId : emptyIfNull( added)) {
            setIssueCrmNumberIfDifferent( youtrackId, caseNumber );
        }
    }

    private Result<YtIssue> removeCrmNumberIfSame(String ytIssueId, Long crmNumber, Long caseNumber) {
        if (Objects.equals(crmNumber, caseNumber)) {
            return apiDao.removeCrmNumber(ytIssueId);
        }
        return ok();
    }

    private Result<YtIssue> replaceCrmNumberIfDifferent(String ytIssueId, Long crmNumber, Long caseNumber) {
        if (Objects.equals(crmNumber, caseNumber)) {
            return ok();
        }
        return apiDao.setCrmNumber(ytIssueId, caseNumber);
    }

    private YouTrackIssueInfo convertToInfo( Issue issue ) {
        if (issue == null) return null;
        YouTrackIssueInfo issueInfo = new YouTrackIssueInfo();
        issueInfo.setId( issue.getId() );
        issueInfo.setSummary( issue.getSummary() );
        issueInfo.setDescription( issue.getDescription() );
        issueInfo.setState( YoutrackConstansMapping.toCaseState( issue.getStateId() ) );
        issueInfo.setImportance( YoutrackConstansMapping.toCaseImportance( issue.getPriority() ) );
        return issueInfo;
    }

    @Autowired
    YoutrackRestClient restDao;

    @Autowired
    YoutrackApiClient apiDao;

    private final static Logger log = LoggerFactory.getLogger( YoutrackServiceImpl.class );

}

