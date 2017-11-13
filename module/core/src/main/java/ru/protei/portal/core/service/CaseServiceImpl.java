package ru.protei.portal.core.service;


import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.CaseObjectEvent;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Реализация сервиса управления обращениями
 */
public class CaseServiceImpl implements CaseService {

    private static Logger log = LoggerFactory.getLogger(CaseServiceImpl.class);

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;

    @Autowired
    CaseObjectDAO caseObjectDAO;

    @Autowired
    CaseShortViewDAO caseShortViewDAO;

    @Autowired
    CaseStateMatrixDAO caseStateMatrixDAO;

    @Autowired
    CaseCommentDAO caseCommentDAO;

    @Autowired
    EventPublisherService publisherService;

    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    @Override
    public CoreResponse<List<CaseShortView>> caseObjectList( AuthToken token, CaseQuery query ) {

        List<CaseShortView> list = caseShortViewDAO.getCases( query );

        if ( list == null )
            return new CoreResponse<List<CaseShortView>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<CaseShortView>>().success(list);
    }

    @Override
    public CoreResponse<CaseObject> getCaseObject( AuthToken token, long id ) {

        CaseObject caseObject = caseObjectDAO.get( id );

        if(caseObject == null)
            return new CoreResponse().error(En_ResultStatus.NOT_FOUND);

        jdbcManyRelationsHelper.fillAll( caseObject.getInitiatorCompany() );
        jdbcManyRelationsHelper.fill( caseObject, "attachments");

        return new CoreResponse<CaseObject>().success(caseObject);
    }

    @Override
    @Transactional
    public CoreResponse< CaseObject > saveCaseObject( AuthToken token, CaseObject caseObject, Person initiator ) {

        if (caseObject == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        Date now = new Date();
        caseObject.setCreated(now);
        caseObject.setModified(now);

        Long caseId = caseObjectDAO.insertCase(caseObject);

        if (caseId == null)
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);

        caseObject.setId(caseId);

        if(CollectionUtils.isNotEmpty(caseObject.getAttachments())){
            caseAttachmentDAO.persistBatch(
                    caseObject.getAttachments()
                            .stream()
                            .map(a -> new CaseAttachment(caseId, a.getId()))
                            .collect(Collectors.toList())
            );
        }

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObject newState = caseObjectDAO.get(caseObject.getId());
        newState.setAttachments(caseObject.getAttachments());
        publisherService.publishEvent(new CaseObjectEvent(this, newState, initiator));

        return new CoreResponse<CaseObject>().success( caseObject );
    }

    @Override
    @Transactional
    public CoreResponse<CaseObject> updateCaseObject( CaseObject caseObject, Person initiator ) {
        if (caseObject == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        caseObject.setModified(new Date());

        if (caseObject.getState() == En_CaseState.CREATED && caseObject.getManager() != null) {
            caseObject.setState(En_CaseState.OPENED);
        }

        CaseObject oldState = caseObjectDAO.get(caseObject.getId());
        jdbcManyRelationsHelper.fill( oldState, "attachments");

        boolean isUpdated = caseObjectDAO.merge(caseObject);

        if (!isUpdated)
            return new CoreResponse().error(En_ResultStatus.NOT_UPDATED);

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObject newState = caseObjectDAO.get(caseObject.getId());
        newState.setAttachments(caseObject.getAttachments());

        publisherService.publishEvent(new CaseObjectEvent(this, newState, oldState, initiator));

        return new CoreResponse<CaseObject>().success( caseObject );
    }

    @Override
    @Transactional
    public CoreResponse< CaseObject > updateCaseObject( AuthToken token, CaseObject caseObject ) {
        UserSessionDescriptor descriptor = authService.findSession( token );
        return updateCaseObject (caseObject, descriptor.getPerson());
    }

    @Override
    public CoreResponse<List<En_CaseState>> stateList( En_CaseType caseType ) {
        List<En_CaseState> states = caseStateMatrixDAO.getStatesByCaseType(caseType);

        if (states == null)
            return new CoreResponse<List<En_CaseState>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<En_CaseState>>().success(states);
    }

    @Override
    public CoreResponse<List<CaseComment>> getCaseCommentList( AuthToken token, long caseId ) {
        List<CaseComment> list = caseCommentDAO.getCaseComments( caseId );

        if ( list == null )
            return new CoreResponse<List<CaseComment>>().error(En_ResultStatus.GET_DATA_ERROR);

        jdbcManyRelationsHelper.fill(list, "caseAttachments");

        return new CoreResponse<List<CaseComment>>().success(list);
    }

    @Override
    @Transactional
    public CoreResponse<CaseComment> addCaseComment( AuthToken token, CaseComment comment, Person currentPerson ) {

        if ( comment == null )
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        Date now = new Date();
        comment.setCreated(now);

        Long commentId = caseCommentDAO.persist(comment);

        if (commentId == null)
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);

        if(CollectionUtils.isNotEmpty(comment.getCaseAttachments())){
            updateExistsAttachmentsFlag(comment.getCaseId(), true);
            comment.getCaseAttachments().forEach(ca -> ca.setCommentId(commentId));
            caseAttachmentDAO.persistBatch(comment.getCaseAttachments());
        }

        boolean isCaseChanged = updateCaseModified ( token, comment.getCaseId(), comment.getCreated() ).getData();

        if (!isCaseChanged)
            throw new RuntimeException( "failed to update case modifiedDate " );

        // re-read data from db to get full-filled object
        CaseComment result = caseCommentDAO.get( commentId );

        // attachments won't read now from DAO
        result.setCaseAttachments(comment.getCaseAttachments());


        //below building event

        CaseObject caseObject = caseObjectDAO.get(comment.getCaseId());
        jdbcManyRelationsHelper.fill( caseObject, "attachments");

        Collection<Long> addedAttachmentsIds = comment.getCaseAttachments()
                .stream()
                .map(CaseAttachment::getAttachmentId)
                .collect(Collectors.toList());

        Collection<Attachment> addedAttachments = caseObject.getAttachments()
                .stream()
                .filter(a -> addedAttachmentsIds.contains(a.getId()))
                .collect(Collectors.toList());

        publisherService.publishEvent(new CaseCommentEvent(this, caseObject, result, addedAttachments, currentPerson));

        return new CoreResponse<CaseComment>().success( result );
    }

    @Override
    @Transactional
    public CoreResponse<CaseComment> updateCaseComment( AuthToken token, CaseComment comment, Person person ) {

        if (comment == null || comment.getId() == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        if (person == null) {
            return new CoreResponse().error( En_ResultStatus.NOT_UPDATED );
        }

        if (!person.getId().equals(comment.getAuthorId()) || !isChangeAvailable ( comment.getCreated() ))
            return new CoreResponse().error( En_ResultStatus.NOT_UPDATED );

        CaseComment prevComment = caseCommentDAO.get( comment.getId() );
        jdbcManyRelationsHelper.fill(prevComment, "caseAttachments");

        boolean isCommentUpdated = caseCommentDAO.merge(comment);

        if (!isCommentUpdated)
            return new CoreResponse().error( En_ResultStatus.NOT_UPDATED );


        Collection<CaseAttachment> removedCaseAttachments =
                caseAttachmentDAO.subtractDiffAndSynchronize(
                        prevComment.getCaseAttachments(),
                        comment.getCaseAttachments()
                );

        if (!removedCaseAttachments.isEmpty()) {
            removeAttachments( token, removedCaseAttachments);
        }

        boolean isCaseChanged =
                updateExistsAttachmentsFlag(comment.getCaseId()).getData()
                && updateCaseModified ( token, comment.getCaseId(), new Date() ).getData();

        if (!isCaseChanged)
            throw new RuntimeException( "failed to update case modifiedDate " );


        // below building event

        CaseObject caseObject = caseObjectDAO.get(comment.getCaseId());
        jdbcManyRelationsHelper.fill( caseObject, "attachments");

        Collection<Attachment> removedAttachments = attachmentService.getAttachments(token, removedCaseAttachments).getData();
        Collection<Attachment> addedAttachments = attachmentService.getAttachments(token,
                HelperFunc.subtract(comment.getCaseAttachments(), prevComment.getCaseAttachments())
        ).getData();

        publisherService.publishEvent(
                new CaseCommentEvent(this, caseObject, prevComment, removedAttachments, comment, addedAttachments, person)
        );

        return new CoreResponse<CaseComment>().success( comment );
    }

    @Override
    @Transactional
    public CoreResponse removeCaseComment( AuthToken token, CaseComment caseComment, Long personId ) {

        if (caseComment == null || caseComment.getId() == null || personId == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        if (!personId.equals(caseComment.getAuthorId()) || !isChangeAvailable ( caseComment.getCreated() ))
            return new CoreResponse().error(En_ResultStatus.NOT_REMOVED);

        long caseId = caseComment.getCaseId();

        boolean isRemoved = caseCommentDAO.remove(caseComment);

        if (!isRemoved)
            return new CoreResponse().error( En_ResultStatus.NOT_REMOVED );

        boolean isCaseChanged = true;
        if(CollectionUtils.isNotEmpty(caseComment.getCaseAttachments())){
            caseAttachmentDAO.removeByCommentId(caseId);
            caseComment.getCaseAttachments().forEach(ca -> attachmentService.removeAttachment(token, ca.getAttachmentId()));

            if(!isExistsAttachments(caseComment.getCaseId()))
                isCaseChanged = updateExistsAttachmentsFlag(caseComment.getCaseId(), false).getData();
        }

        isCaseChanged &= updateCaseModified(token, caseId, new Date()).getData();

        if (!isCaseChanged)
            throw new RuntimeException( "failed to update case modifiedDate " );

        return new CoreResponse<Boolean>().success(isRemoved);
    }

    @Override
    public CoreResponse<Long> count( AuthToken token, CaseQuery query ) {

        Long count = caseObjectDAO.count(query);

        if (count == null)
            return new CoreResponse<Long>().error(En_ResultStatus.GET_DATA_ERROR, 0L);

        return new CoreResponse<Long>().success(count);
    }

    @Override
    public CoreResponse<Boolean> updateCaseModified( AuthToken token, Long caseId, Date modified) {
        if(caseId == null || !caseObjectDAO.checkExistsByKey(caseId))
            return new CoreResponse<Boolean>().error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setModified(modified == null? new Date(): modified);

        boolean isUpdated = caseObjectDAO.partialMerge(caseObject, "MODIFIED");

        return new CoreResponse<Boolean>().success(isUpdated);
    }

    @Override
    public CoreResponse<Boolean> updateExistsAttachmentsFlag(Long caseId, boolean flag){
        if(caseId == null)
            return new CoreResponse<Boolean>().error(En_ResultStatus.INCORRECT_PARAMS);

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setAttachmentExists(flag);
        boolean result = caseObjectDAO.partialMerge(caseObject, "ATTACHMENT_EXISTS");

        if(!result)
            return new CoreResponse<Boolean>().error(En_ResultStatus.NOT_UPDATED);
        return new CoreResponse<Boolean>().success(true);
    }

    @Override
    public CoreResponse<Boolean> updateExistsAttachmentsFlag(Long caseId){
        return updateExistsAttachmentsFlag(caseId, isExistsAttachments(caseId));
    }


    @Override
    @Transactional
    public CoreResponse<Long> bindAttachmentToCase( AuthToken token, Attachment attachment, long caseId) {
        return attachToCase(attachment, caseId);
    }

    @Override
    @Transactional
    public CoreResponse<Long> attachToCase(Attachment attachment, long caseId) {
        CaseAttachment caseAttachment = new CaseAttachment(caseId, attachment.getId());
        Long caseAttachId = caseAttachmentDAO.persist(caseAttachment);

        if(caseAttachId == null)
            return new CoreResponse<Long>().error(En_ResultStatus.NOT_CREATED);

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setAttachmentExists(true);
        caseObject.setModified(new Date());
        boolean isCaseUpdated = caseObjectDAO.partialMerge(caseObject, "ATTACHMENT_EXISTS", "MODIFIED");

        if(!isCaseUpdated)
            throw new RuntimeException("failed to update case object");

        return new CoreResponse<Long>().success(caseAttachId);
    }

    @Override
    public boolean isExistsAttachments(Long caseId) {
        return caseAttachmentDAO.checkExistsByCondition("case_id = ?", caseId);
    }

    private boolean isChangeAvailable (Date date ) {
        Calendar c = Calendar.getInstance();
        long current = c.getTimeInMillis();
        c.setTime( date );
        long checked = c.getTimeInMillis();

        return current - checked < CHANGE_LIMIT_TIME;
    }

    private void removeAttachments( AuthToken token, Collection<CaseAttachment> list){
        list.forEach(ca -> attachmentService.removeAttachment( token, ca.getAttachmentId()));
    }

    static final long CHANGE_LIMIT_TIME = 300000;  // 5 минут  (в мсек)
}
