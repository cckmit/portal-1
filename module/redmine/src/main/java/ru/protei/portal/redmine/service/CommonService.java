package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Journal;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.redmine.utils.CachedPersonMapper;
import ru.protei.portal.redmine.utils.HttpInputSource;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface CommonService {

    CachedPersonMapper getPersonMapper( RedmineEndpoint endpoint );

    Result<Long> createAndStoreStateHistory(Date created, Long authorId, Long stateId, Long caseObjectId);

    Result<Long> updateAndStoreStateHistory(Date updated, Long authorId, Long oldStateId, Long caseObjectId, Long newStateId);

    Result<Long> createAndStoreImportanceHistory(Date created, Long authorId, Integer importance, Long caseId);

    Result<Long> updateAndStoreImportanceHistory(Date updated, Long authorId, Integer oldImportance, Long caseId, Integer newImportance);

    Result<Long> updateCaseStatus( CaseObject object, Long statusMapId, Date creationOn, String value, Person author );

    Result<Long> updateCasePriority( CaseObject object, Long priorityMapId, Journal journal, String value, Person author );

    Result<Long> updateCaseDescription( CaseObject object, String value, Person author );

    Result<Long> updateCaseSubject( CaseObject object, String value, Person author );

    Result<Long> updateComment( Long objectId, Date creationDate, String text, Person author );

    Result<String> getExternalAppId( long caseId );
    Result<ExternalCaseAppData> getExternalCaseAppData( long caseId );

    Result<RedmineEndpoint> getEndpoint( long companyId, String projectId );
    Result<List<RedmineEndpoint>> getEndpoints();

    Result<Set<Integer>> getExistingAttachmentsHashCodes( long caseObjId );

    Result<Long> saveAttachment( Attachment a, Person author, HttpInputSource httpInputSource, Long fileSize, String contentType, CaseObject caseObject );

    Result<CaseObject> getByExternalAppCaseId( String externalAppCaseId );

    Result<Long> saveCase( CaseObject obj );

    Result<Long>  mergeExtAppData( ExternalCaseAppData appData );

    Result<RedminePriorityMapEntry> getByRedminePriorityId( Integer priorityId, long priorityMapId );
    Result<RedminePriorityMapEntry> getByPortalPriorityId( Integer impLevel, long priorityMapId );

    Result<RedmineToCrmEntry> getLocalStatus( long statusMapId, Integer statusId );
    Result<RedmineStatusMapEntry> getRedmineStatus(long initStateId, long lastStateId, long statusMapId );


    Result<Boolean> updateCreatedOn( RedmineEndpoint endpoint );
    Result<Boolean> updateUpdatedOn( RedmineEndpoint endpoint );

    List<Platform> getPlatforms(Long companyId);

    Result<Date> getLatestHistoryDate(Long caseObjectId);
    Result<Date> getLatestCommentDate(Long caseObjectId);
}
