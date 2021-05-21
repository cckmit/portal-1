package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseNameAndDescriptionEvent;
import ru.protei.portal.core.event.CaseObjectMetaEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.util.DiffResult;
import ru.protei.portal.redmine.utils.CachedPersonMapper;
import ru.protei.portal.redmine.utils.HttpInputSource;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public final class CommonServiceImpl implements CommonService {

    @Autowired
    private FileController fileController;

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private AttachmentDAO attachmentDAO;

    @Autowired
    private RedminePriorityMapEntryDAO priorityMapEntryDAO;

    @Autowired
    private RedmineToCrmStatusMapEntryDAO toCrmStatusMapEntryDAO;

    @Autowired
    private RedmineStatusMapEntryDAO statusMapEntryDAO;

    @Autowired
    private CaseObjectDAO caseObjectDAO;

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    private RedmineEndpointDAO endpointDAO;

    @Autowired
    private PersonDAO personDAO;

    @Autowired
    private ContactItemDAO contactItemDAO;

    @Autowired
    private JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    PlatformDAO platformDAO;

    @Autowired
    HistoryDAO historyDAO;

    @Autowired
    CaseStateDAO caseStateDAO;

    @Autowired
    ImportanceLevelDAO importanceLevelDAO;

    @Override
    public Result<Long> saveAttachment( Attachment attachment, Person author, HttpInputSource httpInputSource, Long fileSize, String contentType, CaseObject caseObject ) {
        Long id;
        logger.info( "Invoke file controller to store attachment {} (size={})", attachment.getFileName(), fileSize );
        try {
            id = fileController.saveAttachment( attachment, httpInputSource, fileSize, contentType, caseObject);
        } catch (Exception e) {
            logger.warn( "Unable to process attachment " + attachment.getFileName(), e );
            return error( En_ResultStatus.INTERNAL_ERROR, "Unable to process attachment " + attachment.getFileName() );
        }

        return ok( id ).publishEvent(
                new CaseAttachmentEvent( this, ServiceModule.REDMINE, author.getId(), caseObject.getId(),
                        Collections.singletonList( attachment ), null )
                );
    }

    @Override
    public Result<CaseObject> getByExternalAppCaseId( String externalAppCaseId ) {
        final CaseObject object = caseObjectDAO.getByExternalAppCaseId(externalAppCaseId);
        if (object == null) return error( En_ResultStatus.NOT_FOUND );
        return ok(object);
    }

    @Override
    public Result<Long> saveCase( CaseObject obj ) {
        Long id = caseObjectDAO.insertCase(obj);
        if (id == null) return error( En_ResultStatus.NOT_FOUND );
        return ok(id);
    }

    @Override
    public Result<Long> mergeExtAppData( ExternalCaseAppData appData ) {
        if(externalCaseAppDAO.merge(appData)) return error( En_ResultStatus.NOT_UPDATED );
        return ok(appData.getId());
    }

    @Override
    public Result<RedminePriorityMapEntry> getByRedminePriorityId( Integer priorityId, long priorityMapId ) {
        return ok( priorityMapEntryDAO.getByRedminePriorityId( priorityId, priorityMapId ) );
    }

    @Override
    public Result<RedminePriorityMapEntry> getByPortalPriorityId( Integer impLevel, long priorityMapId ) {
        return ok( priorityMapEntryDAO.getByPortalPriorityId( impLevel, priorityMapId ) );
    }

    @Override
    public Result<RedmineToCrmEntry> getLocalStatus( long statusMapId, Integer statusId ) {
        return ok( toCrmStatusMapEntryDAO.getLocalStatus( statusMapId, statusId ) );
    }

    @Override
    public Result<RedmineStatusMapEntry> getRedmineStatus( long initStateId, long lastStateId, long statusMapId ) {
        return ok( statusMapEntryDAO.getRedmineStatus(initStateId, lastStateId, statusMapId) );
    }

    public Result<Date> getLatestHistoryDate(Long caseObjectId) {
        return ok(historyDAO.getMaxValue("history.date", Date.class,
                "left join person on history.initiator_id = person.id",
                "case_object_id = ? and person.creator = 'redmine'", caseObjectId));
    }

    public Result<Date> getLatestCommentDate(Long caseObjectId) {
        return ok(caseCommentDAO.getMaxValue("case_comment.created", Date.class,
                "left join person on case_comment.author_id = person.id",
                "case_comment.case_id = ? and person.creator = 'redmine'", caseObjectId));
    }

    @Override
    public Result<Boolean> updateCreatedOn( RedmineEndpoint endpoint ) {
         if(!endpointDAO.updateCreatedOn( endpoint )) return error( En_ResultStatus.NOT_UPDATED );
         return ok(true);
    }

    @Override
    public Result<Boolean> updateUpdatedOn( RedmineEndpoint endpoint ) {
        if(!endpointDAO.updateUpdatedOn( endpoint )) return error( En_ResultStatus.NOT_UPDATED );
        return ok(true);
    }

    @Override
    public Result<Long> createAndStoreStateHistory(Date created, Long authorId, Long stateId, Long caseId) {
        Long id = addStateHistory(created, authorId, caseId, stateId, caseStateDAO.get(stateId).getState());
        if(id == null) return error( En_ResultStatus.NOT_CREATED, "State history do not created." );
        return ok(id);
    }

    @Override
    public Result<Long> updateAndStoreStateHistory(Date updated, Long authorId, Long oldStateId, Long caseObjectId, Long newStateId) {
        Long id = changeStateHistory(updated, authorId, caseObjectId, oldStateId, caseStateDAO.get(oldStateId).getState(),
                newStateId, caseStateDAO.get(newStateId).getState());
        if(id == null) return error( En_ResultStatus.NOT_UPDATED, "State history do not updated." );
        return ok(id);
    }

    @Override
    public Result<Long> createAndStoreImportanceHistory(Date created, Long authorId, Integer importance, Long caseId) {
        Long id = addImportanceHistory(created, authorId, caseId, importanceLevelDAO.get(importance));
        if(id == null) return error( En_ResultStatus.NOT_CREATED, "Importance comment do not created." );
        return ok(id);
    }

    @Override
    public Result<Long> updateAndStoreImportanceHistory(Date updated, Long authorId, Integer oldImportance, Long caseId, Integer newImportance) {
        Long id = changeImportanceHistory(updated, authorId, caseId, importanceLevelDAO.get(oldImportance), importanceLevelDAO.get(newImportance));
        if(id == null) return error( En_ResultStatus.NOT_UPDATED, "Importance comment do not updated." );
        return ok(id);
    }

    @Override
    @Transactional
    public Result<Long> updateCaseStatus( CaseObject object, Long statusMapId, Date creationOn, String value, Person author ) {
        Integer newStatus = parseToInteger( value );
        logger.debug( "Trying to get portal status id matching with redmine {}", newStatus );
        final RedmineToCrmEntry redmineStatusEntry = toCrmStatusMapEntryDAO.getLocalStatus( statusMapId, newStatus );

        if (redmineStatusEntry == null) {
            logger.warn( "Status was not found" );
            return error( En_ResultStatus.NOT_FOUND );
        }

        final CaseObjectMeta oldMeta = new CaseObjectMeta( object );

        object.setStateId( redmineStatusEntry.getLocalStatusId() );
        object.setStateName( redmineStatusEntry.getLocalStatusName() );
        caseObjectDAO.merge( object );
        logger.debug( "Updated case state for case with id {}, old={}, new={}", object.getId(), oldMeta.getStateId(), object.getStateId());

        Result<Long> stateCommentId = updateAndStoreStateHistory( creationOn, author.getId(),
                oldMeta.getStateId(), object.getId(), redmineStatusEntry.getLocalStatusId().longValue() );
        if (stateCommentId.isError()) {
            logger.error( "State comment for the issue {} not saved!", object.getId() );
            return stateCommentId;
        }

        return stateCommentId.publishEvent( new CaseObjectMetaEvent(
                this,
                ServiceModule.REDMINE,
                author.getId(),
                En_ExtAppType.forCode( object.getExtAppType() ),
                oldMeta,
                new CaseObjectMeta( object ) ) );

    }

    @Override
    @Transactional
    public Result<Long> updateCasePriority( CaseObject object, Long priorityMapId, Journal journal, String value, Person author ) {
        Integer newPriority = parseToInteger( value );
        logger.debug( "Trying to get portal priority level id matching with redmine {}", value );
        final RedminePriorityMapEntry priorityMapEntry = priorityMapEntryDAO.getByRedminePriorityId( newPriority, priorityMapId );

        if (priorityMapEntry == null) {
            logger.warn( "Priority was not found: newPriority={} priorityMapId={}", newPriority, priorityMapId );
            return error( En_ResultStatus.NOT_FOUND );
        }

        final CaseObjectMeta oldMeta = new CaseObjectMeta( object );

        object.setImpLevel( priorityMapEntry.getLocalPriorityId() );
        caseObjectDAO.merge( object );
        logger.debug( "Updated case priority for case with id {}, old={}, new={}", object.getId(), oldMeta.getImportanceCode(), object.getImportanceCode() );

        Result<Long> importanceCommentId = updateAndStoreImportanceHistory( journal.getCreatedOn(), author.getId(),
                oldMeta.getImpLevel(), object.getId(), priorityMapEntry.getLocalPriorityId());
        if (importanceCommentId.isError()) {
            logger.error( "Importance comment for the issue {} not saved!", object.getId() );
            return importanceCommentId;
        }

        return importanceCommentId.publishEvent( new CaseObjectMetaEvent(
                this,
                ServiceModule.REDMINE,
                author.getId(),
                En_ExtAppType.forCode( object.getExtAppType() ),
                oldMeta,
                new CaseObjectMeta( object ) ) );
    }

    @Override
    public Result<Long> updateCaseDescription( CaseObject object, String value, Person author ) {

        final DiffResult<String> infoDiff = new DiffResult<>(object.getInfo(), value);

        object.setInfo(value);
        if (!caseObjectDAO.merge( object )) {
            return error( En_ResultStatus.NOT_UPDATED, "Case description not updated." );
        }
        logger.info("Updated case info for case with id {}, old={}, new={}", object.getId(), infoDiff.getInitialState(), infoDiff.getNewState());

        return ok(object.getId()).publishEvent(new CaseNameAndDescriptionEvent(
                this,
                object.getId(),
                new DiffResult<>(null, object.getName()),
                infoDiff,
                author.getId(),
                ServiceModule.REDMINE,
                En_ExtAppType.forCode(object.getExtAppType())));
    }

    @Override
    public Result<Long> updateCaseSubject( CaseObject object, String value, Person author ) {
        final DiffResult<String> nameDiff = new DiffResult<>(object.getName(), value);

        object.setName(value);
        if (!caseObjectDAO.merge(object)) {
            return error( En_ResultStatus.NOT_UPDATED, "Case subject not updated." );
        }
        logger.info("Updated case name for case with id {}, old={}, new={}", object.getId(), nameDiff.getInitialState(), nameDiff.getNewState());

        return ok(object.getId()).publishEvent(new CaseNameAndDescriptionEvent(
                this,
                object.getId(),
                nameDiff,
                new DiffResult<>(null, object.getInfo()),
                author.getId(),
                ServiceModule.REDMINE,
                En_ExtAppType.forCode(object.getExtAppType())));
    }

    @Override
    public Result<Long> updateComment( Long objectId, Date creationDate, String text, Person author ){
        CaseComment caseComment = createAndStoreComment( creationDate,  text, author, objectId);
        logger.info("Added new case comment id={} to case with id {}",caseComment.getId(),  objectId);
        logger.trace("Added new case comment to case with id {}, comment has following text: {}", objectId, caseComment.getText());

        return ok(caseComment.getId()).publishEvent(new CaseCommentEvent(
                this,
                ServiceModule.REDMINE,
                author.getId(),
                objectId,
                false,
                null,
                caseComment,
                null));
    }

    @Override
    public Result<String> getExternalAppId(long caseId) {
        String extAppCaseId = externalCaseAppDAO.get(caseId).getExtAppCaseId();
        return (extAppCaseId != null) ? ok(extAppCaseId) : error(En_ResultStatus.NOT_FOUND);
    }

    @Override
    public Result<ExternalCaseAppData> getExternalCaseAppData( long caseId ) {
        ExternalCaseAppData appData = externalCaseAppDAO.get( caseId );
        return ok( appData );
    }

    @Override
    public Result<RedmineEndpoint> getEndpoint( long companyId, String projectId ) {
        RedmineEndpoint endpoint = endpointDAO.getByCompanyIdAndProjectId( companyId, projectId );
        if (endpoint == null)
            return error( En_ResultStatus.NOT_FOUND, "Not found RedmineEndpoint by companyId=" + companyId + " projectId=" + projectId );
        return ok(endpoint);
    }

    @Override
    public Result<List<RedmineEndpoint>> getEndpoints() {
       return ok( endpointDAO.getAll());
    }

    @Override
    public CachedPersonMapper getPersonMapper( RedmineEndpoint endpoint ) {
        return new CachedPersonMapper(personDAO, contactItemDAO, jdbcManyRelationsHelper, endpoint.getCompanyId(), endpoint.getDefaultUserLocalId(), null);
    }

    @Override
    public Result<Set<Integer>> getExistingAttachmentsHashCodes( long caseObjId ) {
        return ok( attachmentDAO.getAttachmentsByCaseId(caseObjId).stream()
                .map(Attachment::toHashCodeForRedmineCheck)
                .collect(Collectors.toSet()));
    }

    @Override
    public List<Platform> getPlatforms(Long companyId) {
        PlatformQuery query = new PlatformQuery();
        query.setCompanyId(companyId);
        return platformDAO.listByQuery(query);
    }

    private Long addStateHistory(Date date, Long personId, Long caseId, Long stateId, String stateName) {
        return createHistory(date, personId, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_STATE, null, null, stateId, stateName);
    }

    private Long changeStateHistory(Date date, Long personId, Long caseId, Long oldStateId, String oldStateName, Long newStateId, String newStateName) {
        return createHistory(date, personId, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_STATE, oldStateId, oldStateName, newStateId, newStateName);
    }

    private Long addImportanceHistory(Date date, Long personId, Long caseId, ImportanceLevel importanceLevel) {
        return createHistory(date, personId, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_IMPORTANCE, null, null,
                importanceLevel.getId().longValue(), importanceLevel.getCode());
    }

    private Long changeImportanceHistory(Date date, Long personId, Long caseId, ImportanceLevel oldImportanceLevel, ImportanceLevel newImportanceLevel) {
        return createHistory(date, personId, caseId, En_HistoryAction.CHANGE, En_HistoryType.CASE_IMPORTANCE,
                oldImportanceLevel.getId().longValue(), oldImportanceLevel.getCode(), newImportanceLevel.getId().longValue(), newImportanceLevel.getCode());
    }

    private Long createHistory(Date date, Long personId, Long caseObjectId, En_HistoryAction action,
                               En_HistoryType type, Long oldId, String oldValue, Long newId, String newValue) {
        History history = new History();
        history.setInitiatorId(personId);
        history.setDate(date);
        history.setCaseObjectId(caseObjectId);
        history.setAction(action);
        history.setType(type);
        history.setOldId(oldId);
        history.setOldValue(oldValue);
        history.setNewId(newId);
        history.setNewValue(newValue);

        return historyDAO.persist(history);
    }

    private CaseComment createAndStoreComment(Date creationDate, String text, Person author, Long caseId) {
        final CaseComment comment = new CaseComment();
        comment.setCreated(creationDate);
        comment.setAuthor(author);
        comment.setText(text);
        comment.setCaseId(caseId);
        comment.setPrivacyType(En_CaseCommentPrivacyType.PUBLIC);
        caseCommentDAO.persist(comment);
        return comment;
    }

    private Integer parseToInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Can't parse value {} to Integer", value);
            return null;
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);
}
