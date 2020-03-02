package ru.protei.portal.redmine.service;

import com.taskadapter.redmineapi.bean.Journal;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.controller.cloud.FileController;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseNameAndDescriptionEvent;
import ru.protei.portal.core.event.CaseObjectMetaEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_ExtAppType;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.util.DiffResult;
import ru.protei.portal.redmine.utils.CachedPersonMapper;
import ru.protei.portal.redmine.utils.HttpInputSource;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public final class CommonServiceImpl implements CommonService {

    @Override
    public Result<Long> saveAttachment( Attachment attachment, Person author, HttpInputSource httpInputSource, Long fileSize, String contentType, Long caseObjId ) {
        Long id;
        logger.debug( "Invoke file controller to store attachment {} (size={})", attachment.getFileName(), fileSize );
        try {
            id = fileController.saveAttachment( attachment, httpInputSource, fileSize, contentType, caseObjId );
        } catch (Exception e) {
            logger.debug( "Unable to process attachment {}", attachment.getFileName() );
            logger.debug( "Trace", e );
            return error( En_ResultStatus.INTERNAL_ERROR, "Unable to process attachment " + attachment.getFileName() );
        }

        return ok( id )
                .publishEvent( new CaseAttachmentEvent( this, ServiceModule.REDMINE, author.getId(), caseObjId,
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
        return null;
    }

    @Override
    public Result<Long> mergeExtAppData( ExternalCaseAppData appData ) {
        if(externalCaseAppDAO.merge(appData)) return error( En_ResultStatus.NOT_UPDATED );
        return ok(appData.getId());
    }

    @Override
    public Result<List<CaseComment>> getCaseComments( CaseCommentQuery caseCommentQuery ) {
        return ok(caseCommentDAO.getCaseComments( caseCommentQuery ));
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
    public Result<RedmineStatusMapEntry> getRedmineStatus( En_CaseState initState, En_CaseState lastState, long statusMapId ) {
        return ok( statusMapEntryDAO.getRedmineStatus( initState, lastState, statusMapId) );
    }

    private CaseComment createAndStoreComment(Date creationDate, String text, Person author, Long caseId) {
        final CaseComment comment = new CaseComment();
        comment.setCreated(creationDate);
        comment.setAuthor(author);
        comment.setText(text);
        comment.setCaseId(caseId);
        caseCommentDAO.persist(comment);
        return comment;
    }

    @Override
    public Result<Long> createAndStoreStateComment( Date created, Long authorId, Long stateId, Long caseId) {
        final CaseComment statusComment = new CaseComment();
        statusComment.setCreated(created);
        statusComment.setAuthorId(authorId);
        statusComment.setCaseStateId(stateId);
        statusComment.setCaseId(caseId);
        Long id = caseCommentDAO.persist( statusComment );
        if(id == null) return error( En_ResultStatus.NOT_CREATED, "State comment do not created." );
        return ok(id);
    }

    @Override
    public Result<Long> createAndStoreImportanceComment( Date created, Long authorId, Integer importance, Long caseId) {
        final CaseComment stateChangeMessage = new CaseComment();
        stateChangeMessage.setCreated(created);
        stateChangeMessage.setAuthorId(authorId);
        stateChangeMessage.setCaseImpLevel(importance);
        stateChangeMessage.setCaseId(caseId);
        Long id = caseCommentDAO.persist( stateChangeMessage );
        if(id == null) return error( En_ResultStatus.NOT_CREATED, "Importance comment do not created." );
        return ok(id);
    }

    @Override
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
        caseObjectDAO.merge( object );
        logger.debug( "Updated case state for case with id {}, old={}, new={}", object.getId(), En_CaseState.getById( oldMeta.getStateId() ), En_CaseState.getById( object.getStateId() ) );

        Result<Long> stateCommentId = createAndStoreStateComment( creationOn, author.getId(), redmineStatusEntry.getLocalStatusId().longValue(), object.getId() );
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
    public Result<Long> updateCasePriority( CaseObject object, Long priorityMapId, Journal journal, String value, Person author ) {
        Integer newPriority = parseToInteger( value );
        logger.debug( "Trying to get portal priority level id matching with redmine {}", value );
        final RedminePriorityMapEntry priorityMapEntry = priorityMapEntryDAO.getByRedminePriorityId( newPriority, priorityMapId );

        if (priorityMapEntry == null) {
            logger.warn( "Priority was not found" );
            return error( En_ResultStatus.NOT_FOUND );
        }

        final CaseObjectMeta oldMeta = new CaseObjectMeta( object );

        object.setImpLevel( priorityMapEntry.getLocalPriorityId() );
        caseObjectDAO.merge( object );
        logger.debug( "Updated case priority for case with id {}, old={}, new={}", object.getId(), En_ImportanceLevel.find( oldMeta.getImpLevel() ), En_ImportanceLevel.find( object.getImpLevel() ) );

        Result<Long> importanceCommentId = createAndStoreImportanceComment( journal.getCreatedOn(), author.getId(), priorityMapEntry.getLocalPriorityId(), object.getId() );
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
        logger.debug("Added new case comment to case with id {}, comment has following text: {}", objectId, caseComment.getText());

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
    public Result<ExternalCaseAppData> getExternalCaseAppData( long caseId ) {
        ExternalCaseAppData appData = externalCaseAppDAO.get( caseId );
        if (appData == null) return error( En_ResultStatus.NOT_FOUND, "Not found ExternalCaseAppData by id=" + caseId );
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
    public CachedPersonMapper getPersonMapper( RedmineEndpoint endpoint ) {
        return new CachedPersonMapper(personDAO, endpoint.getCompanyId(), endpoint.getDefaultUserLocalId(), null);
    }

    private Integer parseToInteger(String value) {
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.warn("Can't parse value {} to Integer", value);
            return null;
        }
    }

    @Override
    public Result<Set<Integer>> getExistingAttachmentsHashCodes( long caseObjId ) {
        return ok( attachmentDAO.getListByCaseId(caseObjId).stream()
                .map(Attachment::toHashCodeForRedmineCheck)
                .collect(Collectors.toSet()));
    }

    @Autowired
    private CaseCommentDAO caseCommentDAO;

    @Autowired
    private FileController fileController;

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
    public CommonService commonService;

    @Autowired
    private ExternalCaseAppDAO externalCaseAppDAO;

    @Autowired
    private RedmineEndpointDAO endpointDAO;

    @Autowired
    private PersonDAO personDAO;

    private final static Logger logger = LoggerFactory.getLogger(CommonServiceImpl.class);
}
