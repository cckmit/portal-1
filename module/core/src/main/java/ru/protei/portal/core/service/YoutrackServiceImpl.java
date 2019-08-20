package ru.protei.portal.core.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.dao.YoutrackDAO;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.YouTrackIssueInfo;
import ru.protei.portal.core.model.yt.ChangeResponse;
import ru.protei.portal.core.model.yt.Issue;
import ru.protei.portal.core.model.yt.YtAttachment;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.CoreResponse.errorSt;
import static ru.protei.portal.api.struct.CoreResponse.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

/**
 * Created by admin on 15/11/2017.
 */
public class YoutrackServiceImpl implements YoutrackService {

    @Override
    public CoreResponse<ChangeResponse> getIssueChanges( String issueId ) {
        return youtrackDao.getIssueChanges( issueId );
    }

    @Override
    public CoreResponse<List<YtAttachment>> getIssueAttachments( String issueId ) {
        return youtrackDao.getIssueAttachments( issueId );
    }

    @Override
    public CoreResponse<String> createIssue( String project, String summary, String description ) {
        return youtrackDao.createIssue( project, summary, description );
    }

    @Override
    public CoreResponse<Set<String>> getIssueIdsByProjectAndUpdatedAfter( String projectId, Date updatedAfter ) {
        return youtrackDao.getIssuesByProjectAndUpdated( projectId, updatedAfter )
                .map( issues -> stream( issues ).map( Issue::getId )
                        .collect( Collectors.toSet() ) );
    }

    @Override
    public CoreResponse<YouTrackIssueInfo> getIssueInfo( String issueId ) {
        if (issueId == null) {
            log.warn( "getIssueInfo(): Can't get issue info. Argument issueId is mandatory" );
            return errorSt( En_ResultStatus.INCORRECT_PARAMS );
        }

        return youtrackDao.getIssue( issueId )
                .map( this::convertToInfo );
    }

    @Override
    public CoreResponse<String> setIssueCrmNumberIfDifferent( String issueId, Long caseNumber ) {
        if (issueId == null || caseNumber == null) {
            log.warn( "setIssueCrmNumber(): Can't set youtrack issue crm number. All arguments are mandatory issueId={} caseNumber={}", issueId, caseNumber );
            return errorSt( En_ResultStatus.INCORRECT_PARAMS );
        }

        return youtrackDao.getIssue( issueId )
                .flatMap( issue -> replaceCrmNumberIfDifferent( issueId, issue.getCrmNumber(), caseNumber ) );
    }

    @Override
    public CoreResponse<String> removeIssueCrmNumberIfSame( String issueId, Long caseNumber ) {
        if (issueId == null || caseNumber == null) {
            log.warn( "removeIssueCrmNumber(): Can't remove youtrack issue crm number. All arguments are mandatory issueId={} caseNumber={}", issueId, caseNumber  );
            return errorSt( En_ResultStatus.INCORRECT_PARAMS );
        }

        return youtrackDao.getIssue( issueId )
                .flatMap( issue -> removeCrmNumberIfSame( issueId, issue.getCrmNumber(), caseNumber ) );
    }

    private CoreResponse<String> removeCrmNumberIfSame( String issueId, Long crmNumber, Long caseNumber ) {
        if (Objects.equals( crmNumber, caseNumber )) {
            return youtrackDao.removeCrmNumber( issueId );
        }
        return ok();
    }

    private CoreResponse<String> replaceCrmNumberIfDifferent( String issueId, Long crmNumber, Long caseNumber ) {
        if (Objects.equals( crmNumber, caseNumber )) {
            return ok();
        }

        return youtrackDao.setCrmNumber( issueId, caseNumber );
    }

    private YouTrackIssueInfo convertToInfo( Issue issue ) {
        if (issue == null) return null;
        YouTrackIssueInfo issueInfo = new YouTrackIssueInfo();
        issueInfo.setId( issue.getId() );
        issueInfo.setSummary( issue.getSummary() );
        issueInfo.setDescription( issue.getDescription() );
        issueInfo.setState( EmployeeRegistrationYoutrackSynchronizer.toCaseState( issue.getStateId() ) );
        issueInfo.setImportance( toCaseImportance( issue.getPriority() ) );
        return issueInfo;
    }

    private En_ImportanceLevel toCaseImportance( String ytpriority ) {
        En_ImportanceLevel result = null;

        if (ytpriority != null) {
            switch (ytpriority) {
                case "Show-stopper":
                case "Critical":
                    result = En_ImportanceLevel.CRITICAL;
                    break;
                case "Important":
                    result = En_ImportanceLevel.IMPORTANT;
                    break;
                case "Basic":
                    result = En_ImportanceLevel.BASIC;
                    break;
                case "Low":
                    result = En_ImportanceLevel.COSMETIC;
                    break;
                default:
                    return result = null;
            }

            if (result == null) {
                log.warn( "toCaseImportance(): Detected unknown YouTrack priority level= {}", ytpriority );
            }
        }
        return result;
    }


    @Autowired
    YoutrackDAO youtrackDao;

    private final static Logger log = LoggerFactory.getLogger( YoutrackServiceImpl.class );

}

