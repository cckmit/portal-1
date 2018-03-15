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
import ru.protei.portal.core.model.dict.*;
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
    PersonDAO personDAO;

    @Autowired
    EventAssemblerService publisherService;

    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;

    @Autowired
    CaseNotifierDAO caseNotifierDAO;

    @Autowired
    AttachmentService attachmentService;

    @Autowired
    PolicyService policyService;

    @Autowired
    AuthService authService;

    @Override
    public CoreResponse<List<CaseShortView>> caseObjectList( AuthToken token, CaseQuery query ) {
        applyFilterByScope( token, query );
        List<CaseShortView> list = caseShortViewDAO.getCases( query );

        if ( list == null )
            return new CoreResponse<List<CaseShortView>>().error(En_ResultStatus.GET_DATA_ERROR);

        return new CoreResponse<List<CaseShortView>>().success(list);
    }

    @Override
    public CoreResponse<CaseObject> getCaseObject( AuthToken token, long number ) {

        CaseObject caseObject = caseObjectDAO.getCase( En_CaseType.CRM_SUPPORT, number );

        if(caseObject == null)
            return new CoreResponse().error(En_ResultStatus.NOT_FOUND);

        jdbcManyRelationsHelper.fillAll( caseObject.getInitiatorCompany() );
        jdbcManyRelationsHelper.fill( caseObject, "attachments");
        jdbcManyRelationsHelper.fill( caseObject, "notifiers");

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
        applyCaseByScope( token, caseObject );

        Long caseId = caseObjectDAO.insertCase(caseObject);

        if (caseId == null)
            return new CoreResponse().error(En_ResultStatus.NOT_CREATED);
        else
            caseObject.setId(caseId);

        Long messageId = createAndPersistStateMessage(initiator, caseId, En_CaseState.CREATED);
        if(messageId == null)
            log.error("State message for the issue %d not saved!", caseId);

        if(CollectionUtils.isNotEmpty(caseObject.getAttachments())){
            caseAttachmentDAO.persistBatch(
                    caseObject.getAttachments()
                            .stream()
                            .map(a -> new CaseAttachment(caseId, a.getId()))
                            .collect(Collectors.toList())
            );
        }

        if(CollectionUtils.isNotEmpty(caseObject.getNotifiers())){
            caseNotifierDAO.persistBatch(
                    caseObject.getNotifiers()
                            .stream()
                            .map(person -> new CaseNotifier(caseId, person.getId()))
                            .collect(Collectors.toList()));

            // update partially filled objects
            caseObject.setNotifiers(new HashSet<>(personDAO.partialGetListByKeys(
                            caseObject.getNotifiers()
                                    .stream()
                                    .map(person ->  person.getId())
                                    .collect(Collectors.toList()), "id", "contactInfo"))
            );
        }

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObject newState = caseObjectDAO.get(caseId);
        newState.setAttachments(caseObject.getAttachments());
        newState.setNotifiers(caseObject.getNotifiers());
        publisherService.publishEvent(new CaseObjectEvent(this, newState, initiator));

        return new CoreResponse<CaseObject>().success( newState );
    }


    @Override
    @Transactional
    public CoreResponse<CaseObject> updateCaseObject( CaseObject caseObject, Person initiator ) {
        if (caseObject == null)
            return new CoreResponse().error(En_ResultStatus.INCORRECT_PARAMS);

        jdbcManyRelationsHelper.persist(caseObject, "notifiers");
        CaseObject oldState = caseObjectDAO.get(caseObject.getId());

        if (caseObject.getState() == En_CaseState.CREATED && caseObject.getManager() != null) {
            caseObject.setState(En_CaseState.OPENED);
        }

        if(isCaseHasNoChanges(caseObject, oldState))
            return new CoreResponse<CaseObject>().success( caseObject ); //ignore

        caseObject.setModified(new Date());
        if(CollectionUtils.isNotEmpty(caseObject.getNotifiers())) {
            // update partially filled objects
            caseObject.setNotifiers(
                    new HashSet<>(personDAO.partialGetListByKeys(
                            caseObject.getNotifiers().stream().map(Person::getId).collect(Collectors.toList()
                            ), "id", "contactInfo"))
            );
        }

        boolean isUpdated = caseObjectDAO.merge(caseObject);
        if (!isUpdated)
            return new CoreResponse().error(En_ResultStatus.NOT_UPDATED);

        if(oldState.getState() != caseObject.getState()){
            Long messageId = createAndPersistStateMessage(initiator, caseObject.getId(), caseObject.getState());
            if(messageId == null)
                log.error("State message for the issue %d isn't saved!", caseObject.getId());
        }

        // From GWT-side we get partially filled object, that's why we need to refresh state from db
        CaseObject newState = caseObjectDAO.get(caseObject.getId());
        newState.setAttachments(caseObject.getAttachments());
        newState.setNotifiers(caseObject.getNotifiers());
        jdbcManyRelationsHelper.fill( oldState, "attachments");

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
                caseAttachmentDAO.calcDiffAndSynchronize(
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
        applyFilterByScope( token, query );
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
    public CoreResponse<Long> bindAttachmentToCaseNumber(AuthToken token, Attachment attachment, long caseNumber) {
        return attachToCaseId(attachment, caseObjectDAO.getCaseId(En_CaseType.CRM_SUPPORT, caseNumber));
    }

    @Override
    @Transactional
    public CoreResponse<Long> attachToCaseId(Attachment attachment, long caseId) {
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

    private Long createAndPersistStateMessage(Person author, Long caseId, En_CaseState state){
        CaseComment stateChangeMessage = new CaseComment();
        stateChangeMessage.setAuthor(author);
        stateChangeMessage.setCreated(new Date());
        stateChangeMessage.setCaseId(caseId);
        stateChangeMessage.setCaseStateId((long)state.getId());
        return caseCommentDAO.persist(stateChangeMessage);
    }

    private void applyFilterByScope( AuthToken token, CaseQuery query ) {
        UserSessionDescriptor descriptor = authService.findSession( token );
        Set< UserRole > roles = descriptor.getLogin().getRoles();
        if ( !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_VIEW ) ) {
            query.setCompanyId( descriptor.getCompany() == null ? null : descriptor.getCompany().getId() );
            query.setAllowViewPrivate( false );
        }
    }

    private void applyCaseByScope( AuthToken token, CaseObject caseObject ) {
        UserSessionDescriptor descriptor = authService.findSession( token );
        Set< UserRole > roles = descriptor.getLogin().getRoles();
        if ( !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_CREATE ) && policyService.hasScopeForPrivilege( roles, En_Privilege.ISSUE_CREATE, En_Scope.COMPANY ) ) {
            caseObject.setPrivateCase( false );
            caseObject.setInitiatorCompany( descriptor.getCompany() );
            caseObject.setManagerId( null );
        }
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

    private boolean isCaseHasNoChanges(CaseObject co1, CaseObject co2){
        //without notifiers
        return
                Objects.equals(co1.getName(), co2.getName())
                && Objects.equals(co1.getInfo(), co2.getInfo())
                && Objects.equals(co1.isPrivateCase(), co2.isPrivateCase())
                && Objects.equals(co1.getState(), co2.getState())
                && Objects.equals(co1.getImpLevel(), co2.getImpLevel())
                && Objects.equals(co1.getInitiatorCompanyId(), co2.getInitiatorCompanyId())
                && Objects.equals(co1.getInitiatorId(), co2.getInitiatorId())
                && Objects.equals(co1.getProductId(), co2.getProductId())
                && Objects.equals(co1.getManagerId(), co2.getManagerId());
    }

    static final long CHANGE_LIMIT_TIME = 300000;  // 5 минут  (в мсек)
}
