package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.*;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.CaseTagQuery;
import ru.protei.portal.core.model.query.PersonQuery;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.portal.core.model.struct.CaseCommentSaveOrUpdateResult;
import ru.protei.portal.core.model.struct.ReplaceLoginWithUsernameInfo;
import ru.protei.portal.core.model.struct.receivedmail.ReceivedMail;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseCommentShortView;
import ru.protei.portal.core.model.view.PersonProjectMemberView;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.access.ProjectAccessUtil.canAccessProject;
import static ru.protei.portal.core.access.ProjectAccessUtil.canAccessProjectPrivateElements;
import static ru.protei.portal.core.model.dict.En_CaseType.*;
import static ru.protei.portal.core.model.dict.En_Privilege.ISSUE_EDIT;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class CaseCommentServiceImpl implements CaseCommentService {
    private static final long CHANGE_LIMIT_TIME = 300000;  // 5 минут (в мсек)
    private static Logger log = LoggerFactory.getLogger(CaseCommentServiceImpl.class);

    @Autowired
    CaseService caseService;
    @Autowired
    AttachmentService attachmentService;
    @Autowired
    ProjectService projectService;
    @Autowired
    EventPublisherService publisherService;

    @Autowired
    PolicyService policyService;
    @Autowired
    AuthService authService;
    @Autowired
    HistoryService historyService;
    @Autowired
    HistoryDAO historyDAO;

    @Autowired
    JdbcManyRelationsHelper jdbcManyRelationsHelper;
    @Autowired
    CaseObjectDAO caseObjectDAO;
    @Autowired
    CaseCommentDAO caseCommentDAO;
    @Autowired
    CaseCommentShortViewDAO caseCommentShortViewDAO;
    @Autowired
    CaseAttachmentDAO caseAttachmentDAO;
    @Autowired
    UserLoginShortViewDAO userLoginShortViewDAO;
    @Autowired
    EmployeeShortViewDAO employeeShortViewDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    UserLoginDAO userLoginDAO;
    @Autowired
    CompanyDAO companyDAO;
    @Autowired
    AttachmentDAO attachmentDAO;
    @Autowired
    CaseTagDAO caseTagDAO;

/*
    @Autowired
    private ClientEventService clientEventService;
*/

    @Override
    public Result<List<CaseComment>> getCaseCommentList(AuthToken token, En_CaseType caseType, long caseObjectId) {
        En_ResultStatus checkAccessStatus = checkAccessForCaseObjectById(token, caseType, caseObjectId);
        if (checkAccessStatus != null) {
            return error(checkAccessStatus);
        }
        CaseCommentQuery query = new CaseCommentQuery(caseObjectId);
        applyFilterByScope(token, caseType, query);

        return getList(query);
    }

    @Override
    public Result<SearchResult<CaseCommentShortView>> getCaseCommentShortViewList(AuthToken token, En_CaseType caseType, CaseCommentQuery query) {
        En_ResultStatus checkAccessStatus = checkAccessForCaseObjectByNumber(token, caseType, query.getCaseNumber());
        if (checkAccessStatus != null) {
            return error(checkAccessStatus);
        }
        applyFilterByScope(token, caseType, query);

        return ok(caseCommentShortViewDAO.getSearchResult(query));
    }

    @Override
    @Transactional
    public Result<CaseComment> addCaseComment( AuthToken token, En_CaseType caseType, CaseComment comment) {

        if (comment == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Result<CaseCommentSaveOrUpdateResult> result = addCaseCommentWithoutEvent(token, caseType, comment);
        if (result.isError()) {
            throw new RollbackTransactionException(result.getStatus());
        }
        CaseCommentSaveOrUpdateResult resultData = result.getData();

        Result<CaseComment> okResult = ok( resultData.getCaseComment() );
        if (CRM_SUPPORT.equals(caseType)) {
            okResult.publishEvent( new CaseAttachmentEvent(this, ServiceModule.GENERAL, token.getPersonId(), comment.getCaseId(),
                    resultData.getAddedAttachments(), null
            ));
            boolean isEagerEvent = En_ExtAppType.REDMINE.getCode().equals( caseObjectDAO.getExternalAppName( comment.getCaseId() ) );

            okResult.publishEvent( new CaseCommentEvent( this, ServiceModule.GENERAL, token.getPersonId(), comment.getCaseId(), isEagerEvent,
                    null, resultData.getCaseComment(), null) );

        }

        if (PROJECT.equals(caseType)) {
            okResult.publishEvent(new ProjectCommentEvent(
                    this, null, resultData.getCaseComment(), null,
                    token.getPersonId(), comment.getCaseId())
            );

            okResult.publishEvent(new ProjectAttachmentEvent(this, resultData.getAddedAttachments(), resultData.getRemovedAttachments(), comment.getId(),
                    token.getPersonId(), comment.getCaseId())
            );
        }

        if (DELIVERY.equals(caseType)) {
            okResult.publishEvent(new DeliveryCommentEvent(
                    this, null, resultData.getCaseComment(), null,
                    token.getPersonId(), comment.getCaseId())
            );

            okResult.publishEvent(new DeliveryAttachmentEvent(this, resultData.getAddedAttachments(), resultData.getRemovedAttachments(), comment.getId(),
                    token.getPersonId(), comment.getCaseId())
            );
        }

        if (EMPLOYEE_REGISTRATION.equals(caseType)) {
            okResult.publishEvent(new EmployeeRegistrationCommentEvent(
                            this, null, resultData.getCaseComment(), null,
                            token.getPersonId(), comment.getCaseId())
            );

            okResult.publishEvent(new EmployeeRegistrationAttachmentEvent(this, resultData.getAddedAttachments(), resultData.getRemovedAttachments(), comment.getId(),
                    token.getPersonId(), comment.getCaseId())
            );
        }

/*
        if (resultData.getCaseComment() != null) {
            clientEventService.fireEvent( new CaseCommentSavedClientEvent( token.getPersonId(), comment.getCaseId(), resultData.getCaseComment().getId() ) );
        }
*/

        return okResult;
    }

    @Override
    @Transactional
    public Result<CaseCommentSaveOrUpdateResult> addCaseCommentWithoutEvent(AuthToken token, En_CaseType caseType, CaseComment comment) {

        if (comment == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_ResultStatus checkAccessStatus = checkAccessForCaseObjectById(token, caseType, comment.getCaseId());
        if (checkAccessStatus != null) {
            return error(checkAccessStatus);
        }

        if (caseType == CRM_SUPPORT && !allowedPrivateComment(token, caseType, comment)) {
            return error(En_ResultStatus.PROHIBITED_PRIVATE_COMMENT);
        }

        comment.setCreated(new Date());
        Long commentId = caseCommentDAO.persist(comment);
        if (commentId == null) {
            log.info("Failed to create comment at db: {}", comment);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        if (CollectionUtils.isNotEmpty(comment.getCaseAttachments())) {
            caseService.updateExistsAttachmentsFlag(comment.getCaseId(), true);
            comment.getCaseAttachments().forEach(ca -> {
                ca.setCommentId(commentId);
                if (caseType.equals(CRM_SUPPORT)){
                    String fileName = StringUtils.emptyIfNull(attachmentDAO.get(ca.getAttachmentId()).getFileName());
                    addCaseAttachmentHistory(token, comment.getCaseId(), ca.getAttachmentId(), fileName);
                }
            });
            caseAttachmentDAO.persistBatch(comment.getCaseAttachments());

            if (comment.isPrivateComment()) {
                Attachment attachment = new Attachment();
                attachment.setPrivate(true);
                comment.getCaseAttachments().forEach(a -> {
                    attachment.setId(a.getAttachmentId());
                    attachmentDAO.partialMerge(attachment, "private_flag");
                });
            }
        }

        boolean isCaseChanged = caseService.updateCaseModified(token, comment.getCaseId(), comment.getCreated()).getData();
        if (!isCaseChanged) {
            log.info("Failed to update case modifiedDate: {}", comment);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        if (!updateTimeElapsed(token, comment.getCaseId()).getData()) {
            log.info("Failed to update time elapsed on addCaseComment: {}", comment);
            throw new RollbackTransactionException(En_ResultStatus.NOT_CREATED);
        }

        // re-read data from db to get full-filled object
        CaseComment result = caseCommentDAO.get(commentId);
        // attachments won't read now from DAO
        result.setCaseAttachments(comment.getCaseAttachments());

        List<Long> addedAttachmentsIds;

        if (result.getCaseAttachments() == null) {
            addedAttachmentsIds = new ArrayList<>();
        } else {
            addedAttachmentsIds = result.getCaseAttachments()
                    .stream()
                    .map(CaseAttachment::getAttachmentId)
                    .collect(Collectors.toList());
        }

        Collection<Attachment> addedAttachments = attachmentService.getAttachments(
                token,
                caseType,
                addedAttachmentsIds
        ).getData();

        return ok( new CaseCommentSaveOrUpdateResult(result, addedAttachments));
    }

    @Override
    @Transactional
    public Result<CaseComment> updateCaseComment( AuthToken token, En_CaseType caseType, CaseComment comment) {

        Result<CaseCommentSaveOrUpdateResult> result = updateCaseCommentWithoutEvent(token, caseType, comment);
        if (result.isError()) {
            throw new RollbackTransactionException(result.getStatus());
        }
        CaseCommentSaveOrUpdateResult resultData = result.getData();

        Result<CaseComment> okResult = ok( resultData.getCaseComment() );
        if (CRM_SUPPORT.equals(caseType)) {
            boolean isEagerEvent = En_ExtAppType.REDMINE.getCode().equals( caseObjectDAO.getExternalAppName( comment.getCaseId() ) );
            okResult.publishEvent( new CaseAttachmentEvent(this, ServiceModule.GENERAL, token.getPersonId(), comment.getCaseId(),
                    resultData.getAddedAttachments(), resultData.getRemovedAttachments())
            );
            okResult.publishEvent( new CaseCommentEvent(this, ServiceModule.GENERAL, token.getPersonId(), comment.getCaseId(),
                            isEagerEvent, resultData.getOldCaseComment(), resultData.getCaseComment(), null ));
        }

        if (PROJECT.equals(caseType)) {
            okResult.publishEvent(new ProjectCommentEvent(this,
                    resultData.getOldCaseComment(), resultData.getCaseComment(), null,
                    token.getPersonId(), comment.getCaseId())
            );

            okResult.publishEvent(new ProjectAttachmentEvent(this, resultData.getAddedAttachments(), resultData.getRemovedAttachments(), comment.getId(),
                    token.getPersonId(), comment.getCaseId())
            );
        }

        if (DELIVERY.equals(caseType)) {
            okResult.publishEvent(new DeliveryCommentEvent(this,
                    resultData.getOldCaseComment(), resultData.getCaseComment(), null,
                    token.getPersonId(), comment.getCaseId())
            );

            okResult.publishEvent(new DeliveryAttachmentEvent(this, resultData.getAddedAttachments(), resultData.getRemovedAttachments(), comment.getId(),
                    token.getPersonId(), comment.getCaseId())
            );
        }

        if (EMPLOYEE_REGISTRATION.equals(caseType)) {
            okResult.publishEvent(new EmployeeRegistrationCommentEvent(this,
                    resultData.getOldCaseComment(), resultData.getCaseComment(), null,
                    token.getPersonId(), comment.getCaseId())
            );

            okResult.publishEvent(new EmployeeRegistrationAttachmentEvent(this, resultData.getAddedAttachments(), resultData.getRemovedAttachments(), comment.getId(),
                    token.getPersonId(), comment.getCaseId())
            );
        }
/*
        if (resultData.getCaseComment() != null) {
            clientEventService.fireEvent( new CaseCommentSavedClientEvent( token.getPersonId(), comment.getCaseId(), resultData.getCaseComment().getId() ) );
        }
*/

        return okResult;
    }

    @Override
    @Transactional
    public Result<CaseCommentSaveOrUpdateResult> updateCaseCommentWithoutEvent( AuthToken token, En_CaseType caseType, CaseComment comment) {

        if (comment == null || comment.getId() == null || token.getPersonId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_ResultStatus checkAccessStatus = checkAccessForCaseObjectById(token, caseType, comment.getCaseId());
        if (checkAccessStatus != null) {
            return error(checkAccessStatus);
        }

        if (caseType == CRM_SUPPORT && !allowedPrivateComment(token, caseType, comment)) {
            return error(En_ResultStatus.PROHIBITED_PRIVATE_COMMENT);
        }

        if (!Objects.equals(token.getPersonId(), comment.getAuthorId())) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        if (isCaseCommentReadOnlyByTime(comment.getCreated())) {
            return error(En_ResultStatus.NOT_ALLOWED_EDIT_COMMENT_BY_TIME);
        }

        CaseComment prevComment = caseCommentDAO.get(comment.getId());

        jdbcManyRelationsHelper.fill(prevComment, "caseAttachments");

        boolean isCommentUpdated = caseCommentDAO.merge(comment);
        if (!isCommentUpdated) {
            log.info("Failed to update comment {} at db", comment.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        Collection<CaseAttachment> removedCaseAttachments = caseAttachmentDAO.calcDiffAndSynchronize(
                prevComment.getCaseAttachments(),
                comment.getCaseAttachments()
        );
        if (!removedCaseAttachments.isEmpty()) {
            removeAttachments(token, caseType, removedCaseAttachments);
        }

        boolean isCaseChanged =
                caseService.updateExistsAttachmentsFlag(comment.getCaseId()).getData()
                        && caseService.updateCaseModified(token, comment.getCaseId(), new Date()).getData();
        if (!isCaseChanged) {
            log.info("Failed to update case modifiedDate for comment {}", comment.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        if (!updateTimeElapsed(token, comment.getCaseId()).getData()) {
            log.info("Failed to update time elapsed on updateCaseComment for comment {}", comment.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED);
        }

        Collection<Attachment> removedAttachments = attachmentService.getAttachments(
                token,
                caseType,
                removedCaseAttachments
        ).getData();

        Collection<Attachment> addedAttachments = attachmentService.getAttachments(
                token,
                caseType,
                HelperFunc.subtract(comment.getCaseAttachments(), prevComment.getCaseAttachments())
        ).getData();

        if (caseType.equals(CRM_SUPPORT) && isNotEmpty(addedAttachments)){
            addedAttachments.forEach(a -> {
                addCaseAttachmentHistory(token, comment.getCaseId(), a.getId(), a.getFileName());
            });
        }

        return ok( new CaseCommentSaveOrUpdateResult(comment, prevComment, addedAttachments, removedAttachments));
    }

    @Override
    @Transactional
    public Result<Long> removeCaseComment(AuthToken token, En_CaseType caseType, CaseComment removedComment) {
        return removeCaseComment(token, caseType, removedComment, true);
    }

    @Override
    public Result<Long> removeCaseCommentWithOutTimeCheck(AuthToken token, En_CaseType caseType, CaseComment comment) {
        return removeCaseComment(token, caseType, comment, false);
    }

    public Result<Long> removeCaseComment(AuthToken token, En_CaseType caseType, CaseComment commentToBeRemoved, boolean isTimeCheck) {
        En_ResultStatus checkAccessStatus = null;
        if (commentToBeRemoved == null || commentToBeRemoved.getId() == null || token.getPersonId() == null) {
            checkAccessStatus = En_ResultStatus.INCORRECT_PARAMS;
        }
        if (checkAccessStatus == null) {
            checkAccessStatus = checkAccessForCaseObjectById(token, caseType, commentToBeRemoved.getCaseId());
        }
        if (checkAccessStatus == null) {
            if (!Objects.equals(token.getPersonId(), commentToBeRemoved.getAuthorId())) {
                checkAccessStatus = En_ResultStatus.NOT_REMOVED;
            }

            if (isTimeCheck && isCaseCommentReadOnlyByTime(commentToBeRemoved.getCreated())) {
                return error(En_ResultStatus.NOT_ALLOWED_REMOVE_COMMENT_BY_TIME);
            }
        }
        if (checkAccessStatus != null) {
            return error(checkAccessStatus);
        }

        Collection<Attachment> attachmentsToBeRemoved = attachmentService.getAttachments(
                token,
                caseType,
                commentToBeRemoved.getCaseAttachments()
        ).getData();

        long caseId = commentToBeRemoved.getCaseId();

        boolean isRemoved = caseCommentDAO.remove(commentToBeRemoved);
        if (!isRemoved) {
            log.info("Failed to remove comment {} at db", commentToBeRemoved.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_REMOVED);
        }

        boolean isCaseChanged = true;
        if (CollectionUtils.isNotEmpty(commentToBeRemoved.getCaseAttachments())) {
            caseAttachmentDAO.removeByCommentId(caseId);
            commentToBeRemoved.getCaseAttachments().forEach(ca -> attachmentService.removeAttachment(token, caseType, caseId, ca.getAttachmentId()));

            isCaseChanged = caseService.isExistAnyAttachments( toList(commentToBeRemoved.getCaseAttachments(), CaseAttachment::getAttachmentId) ).flatMap(isExists -> {
                if (isExists) {
                    return ok( false );
                }
                return caseService.updateExistsAttachmentsFlag( commentToBeRemoved.getCaseId(), false );
            } ).orElseGet( result -> ok( false ) ).getData();
        }
        isCaseChanged &= caseService.updateCaseModified(token, caseId, new Date()).getData();
        if (!isCaseChanged) {
            log.info("Failed to update case modifiedDate for comment {}", commentToBeRemoved.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_REMOVED);
        }

        if (!updateTimeElapsed(token, commentToBeRemoved.getCaseId()).getData()) {
            log.info("Failed to update time elapsed on removeCaseComment for comment {}", commentToBeRemoved.getId());
            throw new RollbackTransactionException(En_ResultStatus.NOT_REMOVED);
        }

        Result<Long> okResult = ok(commentToBeRemoved.getId());

        if (PROJECT.equals(caseType)) {
            okResult.publishEvent(new ProjectCommentEvent(this,
                    null, null, commentToBeRemoved, token.getPersonId(), caseId)
            );

            okResult.publishEvent(new ProjectAttachmentEvent(this, Collections.emptyList(), attachmentsToBeRemoved, commentToBeRemoved.getId(),
                    token.getPersonId(), caseId)
            );
        }

        if (DELIVERY.equals(caseType)) {
            okResult.publishEvent(new DeliveryCommentEvent(this,
                    null, null, commentToBeRemoved, token.getPersonId(), caseId)
            );

            okResult.publishEvent(new DeliveryAttachmentEvent(this, Collections.emptyList(), attachmentsToBeRemoved, commentToBeRemoved.getId(),
                    token.getPersonId(), caseId)
            );
        }

        if (CRM_SUPPORT.equals(caseType)) {
            boolean isEagerEvent = En_ExtAppType.REDMINE.getCode().equals(caseObjectDAO.getExternalAppName(caseId));

            okResult
                    .publishEvent(new CaseAttachmentEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, null, attachmentsToBeRemoved))
                    .publishEvent(new CaseCommentEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, isEagerEvent, null, null, commentToBeRemoved));
        }

        if (EMPLOYEE_REGISTRATION.equals(caseType)) {
            okResult.publishEvent(new EmployeeRegistrationCommentEvent(this,
                    null, null, commentToBeRemoved, token.getPersonId(), caseId)
            );

            okResult.publishEvent(new EmployeeRegistrationAttachmentEvent(this, Collections.emptyList(), attachmentsToBeRemoved, commentToBeRemoved.getId(),
                    token.getPersonId(), caseId)
            );
        }

/*
        if(isRemoved) {
            clientEventService.fireEvent( new CaseCommentRemovedClientEvent( token.getPersonId(), caseId, removedComment.getId() ));
        }
*/

        return okResult;
    }

    @Override
    public Result<Long> getTimeElapsed( Long caseId) {
        List<CaseComment> allCaseComments = caseCommentDAO.partialGetListByCondition("CASE_ID=?", Collections.singletonList(caseId), "id", "time_elapsed");
        long sum = stream(allCaseComments)
                .filter(cmnt -> cmnt.getTimeElapsed() != null)
                .mapToLong(CaseComment::getTimeElapsed).sum();
        return ok( sum);
    }

    @Override
    @Transactional
    public Result<Boolean> updateTimeElapsed( AuthToken token, Long caseId) {
        long timeElapsed = getTimeElapsed(caseId).getData();
        return updateCaseTimeElapsed(token, caseId, timeElapsed);
    }

    @Override
    @Transactional
    public Result<Boolean> updateCaseTimeElapsed( AuthToken token, Long caseId, long timeElapsed) {
        if (caseId == null || !caseObjectDAO.checkExistsByKey(caseId)) {
            return error( En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setTimeElapsed(timeElapsed);

        boolean isUpdated = caseObjectDAO.partialMerge(caseObject, "time_elapsed");

        return ok( isUpdated);
    }

    @Override
    @Transactional
    public Result<Boolean> updateCaseTimeElapsedType(AuthToken token, Long caseCommentId, En_TimeElapsedType type) {
        CaseComment caseComment;

        if (caseCommentId == null || type == En_TimeElapsedType.NONE || (caseComment = caseCommentDAO.get(caseCommentId)) == null) {
            return error( En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!Objects.equals(caseComment.getAuthorId(), token.getPersonId())) {
            return error(En_ResultStatus.NOT_AVAILABLE);
        }

        caseComment.setTimeElapsedType(type);

        boolean isUpdated = caseCommentDAO.partialMerge(caseComment, "time_elapsed_type");

        return ok(isUpdated);
    }

    @Override
    @Transactional
    public Result<Long> addCommentOnSentReminder( CaseComment comment ) {
        comment.setCreated( new Date() );
        if (comment.getAuthorId() == null) {
            comment.setAuthorId( CrmConstants.Person.SYSTEM_USER_ID );
        }
        Long commentId = caseCommentDAO.persist(comment);

        if (commentId == null) {
            return error( En_ResultStatus.NOT_CREATED);
        }

        return ok( commentId);
    }

    @Override
    public Result<CaseComment> getCaseComment( AuthToken token, Long commentId ) {
        if (commentId == null) {
            return error( En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseComment caseComment = caseCommentDAO.get(commentId);
        jdbcManyRelationsHelper.fill(caseComment, "caseAttachments");

        return ok(caseComment);
    }

    @Transactional
    @Override
    public Result<Boolean> updateProjectCommentsFromYoutrack(AuthToken token, CaseComment comment) {
        log.info("updateProjectCommentsFromYoutrack(): Comment to update={}", comment);
        CaseCommentQuery caseCommentQuery = new CaseCommentQuery();
        caseCommentQuery.setRemoteId(comment.getRemoteId());

        List<CaseComment> existedCaseComments = caseCommentDAO.getCaseComments(caseCommentQuery);
        log.info("updateProjectCommentFromYoutrack(): Comments to update={}", existedCaseComments);

        if (existedCaseComments == null){
            log.warn("updateProjectCommentFromYoutrack(): Failed to get project comments. Comment={}", comment);
            return error(En_ResultStatus.INTERNAL_ERROR);
        }

        List<CaseComment> updatedCaseComments = new ArrayList<>(existedCaseComments);
        updatedCaseComments.forEach(updatedCaseComment -> updatedCaseComment.setText(comment.getText()));

        int updatedCount = caseCommentDAO.mergeBatch(updatedCaseComments);

        if (updatedCaseComments.size() != updatedCount){
            throw new RollbackTransactionException(En_ResultStatus.NOT_UPDATED,
                    "updatedCaseComments size = " + updatedCaseComments.size() + " but updatedCount = " + updatedCount);
        }

        List<ApplicationEvent> events = new ArrayList<>();
        for (int i = 0; i < existedCaseComments.size(); i++) {
            events.add(new ProjectCommentEvent(this,
                    existedCaseComments.get(i), updatedCaseComments.get(i), null, token.getPersonId(), updatedCaseComments.get(i).getCaseId()));
        }

        return ok(true).publishEvents(events);
    }

    @Transactional
    @Override
    public Result<Boolean> deleteProjectCommentsFromYoutrack(AuthToken token, String commentRemoteId) {
        log.info("deleteProjectCommentFromYoutrack(): commentRemoteId={}", commentRemoteId);
        CaseCommentQuery caseCommentQuery = new CaseCommentQuery();
        caseCommentQuery.setRemoteId(commentRemoteId);

        List<CaseComment> caseComments = caseCommentDAO.getCaseComments(caseCommentQuery);
        log.info("deleteProjectCommentFromYoutrack(): Comments to delete={}", caseComments);

        if (CollectionUtils.isEmpty(caseComments)){
            log.warn("deleteProjectCommentFromYoutrack(): Failed to find project comments. commentRemoteId={}", commentRemoteId);
            return error(En_ResultStatus.NOT_FOUND);
        }

        int removedCount = caseCommentDAO.removeByKeys(caseComments.stream().map(CaseComment::getId).collect(Collectors.toList()));

        if (caseComments.size() != removedCount){
            throw new RollbackTransactionException(En_ResultStatus.NOT_REMOVED,
                    "caseComments size = " + caseComments.size() + " but removedCount = " + removedCount);
        }

        List<ApplicationEvent> events = new ArrayList<>();
        for (CaseComment caseComment : caseComments) {
            events.add(new ProjectCommentEvent(this,
                    null, null, caseComment, token.getPersonId(), caseComment.getCaseId()));
        }

        return ok(true).publishEvents(events);
    }

    @Override
    public Result<List<String>> replaceLoginWithUsername(AuthToken token, List<String> texts) {
        return replaceLoginWithUsername(texts, Function.identity(), String::replace).map(this::objectListFromReplacementInfoList);
    }

    @Override
    public Result<List<ReplaceLoginWithUsernameInfo<CaseComment>>> replaceLoginWithUsername(List<CaseComment> comments) {
        return replaceLoginWithUsername(comments, CaseComment::getText, this::replaceTextAndGetComment);
    }

    @Override
    @Transactional
    public Result<Boolean> addCommentReceivedByMail(ReceivedMail receivedMail) {
        if (receivedMail.getCaseNo() == null || receivedMail.getSenderEmail() == null) {
            log.warn("addCommentsReceivedByMail(): no case no or sender mail receivedMail ={}", receivedMail);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        PersonQuery personQuery = new PersonQuery();
        personQuery.setEmail(receivedMail.getSenderEmail());
        List<Person> persons = personDAO.getPersons(personQuery);
        if (persons.isEmpty()) {
            log.warn("addCommentsReceivedByMail(): no found person person by mail ={}", receivedMail.getSenderEmail());
            return error(En_ResultStatus.USER_NOT_FOUND);
        }

        if (persons.size() > 1) {
            log.warn("addCommentsReceivedByMail(): more than one found person by mail ={}", receivedMail.getSenderEmail());
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        Person person = persons.get(0);
        List<UserLogin> userLogins = userLoginDAO.findByPersonId( person.getId() );
        if (userLogins.isEmpty()) {
            log.warn("addCommentsReceivedByMail(): no found user login by email ={}", receivedMail.getSenderEmail());
            return error(En_ResultStatus.USER_NOT_FOUND);
        }

        jdbcManyRelationsHelper.fill( userLogins, "roles" );
        if (stream(userLogins)
                .noneMatch(userLogin -> policyService.hasPrivilegeFor(ISSUE_EDIT, userLogin.getRoles()))) {
            log.warn("addCommentsReceivedByMail(): no privilege for create comment ={}", receivedMail.getSenderEmail());
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        CaseObject caseObject = caseObjectDAO.getCaseByNumber(CRM_SUPPORT, receivedMail.getCaseNo());
        if (caseObject == null) {
            log.warn("addCommentsReceivedByMail(): no found case for case no ={}", receivedMail.getCaseNo());
            return error(En_ResultStatus.NOT_FOUND);
        }

        boolean isCustomer = !companyDAO.isEmployeeInHomeCompanies(person.getCompanyId());
        if (isCustomer && caseObject.isPrivateCase()) {
            log.warn("addCommentsReceivedByMail(): private case, forbidden for customer company ={}", person.getCompanyId());
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (isCustomer && !getCompanyAndChildIds(person.getCompanyId()).contains(caseObject.getInitiatorCompanyId())) {
            log.warn("addCommentsReceivedByMail(): case is not owned customer company, forbidden for customer, company = {}", person.getCompanyId());
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        log.info("addCommentsReceivedByMail(): process receivedMail={}", receivedMail);
        CaseComment comment = createComment(caseObject, person, receivedMail.getContent());
        caseCommentDAO.persist(comment);

        boolean isEagerEvent = En_ExtAppType.REDMINE.getCode().equals( caseObjectDAO.getExternalAppName( comment.getCaseId() ) );

        return ok(true).publishEvent( new CaseCommentEvent( this, ServiceModule.GENERAL, person.getId(), comment.getCaseId(), isEagerEvent,
                null, comment, null) );
    }

    @Override
    public Result<CommentsAndHistories> getCommentsAndHistories(AuthToken token, En_CaseType caseType, long caseObjectId) {
        Result<List<CaseComment>> caseCommentListResult = getCaseCommentList(token, caseType, caseObjectId);

        if (caseCommentListResult.isError()) {
            return error(caseCommentListResult.getStatus());
        }

        Result<List<History>> historyListResult = En_CaseType.EMPLOYEE_REGISTRATION.equals(caseType) ?
                historyService.getHistoryListWithEmployeeRegistrationHistory(token, caseObjectId) :
                historyService.getHistoryListByCaseId(token, caseObjectId);

        if (historyListResult.isError()) {
            return error(historyListResult.getStatus());
        }

        CommentsAndHistories commentsAndHistories = new CommentsAndHistories();

        commentsAndHistories.setComments(caseCommentListResult.getData());
        commentsAndHistories.setHistories(
                filterHistories(historyListResult.getData(), token, caseType)
        );

        return ok(commentsAndHistories);
    }

    private List<History> filterHistories(List<History> histories, AuthToken token, En_CaseType caseType) {
        Set<UserRole> roles = token.getRoles();
        if (caseType != CRM_SUPPORT || policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
            return histories;
        }

        CaseTagQuery tagQuery = new CaseTagQuery();
        tagQuery.setCompanyId(token.getCompanyId());
        List<Long> customerTagsIds = toList(caseTagDAO.getListByQuery(tagQuery), CaseTag::getId);

        return stream(histories).filter(history -> customerHistoryPredicate(history, customerTagsIds))
                .collect(Collectors.toList());
    }

    private boolean customerHistoryPredicate(History history, List<Long> customerTagsIds){
        if (history.getType() == En_HistoryType.TAG) {
            return (history.getAction() == En_HistoryAction.ADD && customerTagsIds.contains(history.getNewId()))
                    || (history.getAction() == En_HistoryAction.REMOVE && customerTagsIds.contains(history.getOldId()));
        }
        return true;
    }

    private CaseComment createComment(CaseObject caseObject, Person person, String comment) {
        CaseComment caseComment = new CaseComment();
        caseComment.setCaseId(caseObject.getId());
        caseComment.setAuthor(person);
        caseComment.setCreated(new Date());
        caseComment.setOriginalAuthorFullName(person.getDisplayName());
        caseComment.setOriginalAuthorName(person.getDisplayName());
        caseComment.setText(comment);
        caseComment.setPrivateComment(caseObject.isPrivateCase());

        return caseComment;
    }

    private Collection<Long> getCompanyAndChildIds(Long companyId) {
        Company company = new Company();
        company.setId(companyId);
        jdbcManyRelationsHelper.fill(company, "childCompanies");
        return company.getCompanyAndChildIds();
    }

    private Result<List<CaseComment>> getList(CaseCommentQuery query) {
        List<CaseComment> comments = caseCommentDAO.getCaseComments(query);
        return getList(comments);
    }

    private void applyFilterByScope( AuthToken token, En_CaseType caseType, CaseCommentQuery query ) {
        if (token == null || caseType == null) {
            return;
        }
        Set<UserRole> roles = token.getRoles();
        switch (caseType) {
            case CRM_SUPPORT: {
                if (!policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
                    query.setViewPrivate(false);
                }
                return;
            }
            case PROJECT: {
                Result<List<PersonProjectMemberView>> team = projectService.getProjectTeam(token, getFirst(query.getCaseObjectIds()));
                if (team.isError()) {
                    query.setViewPrivate(false);
                    return;
                }
                if (!canAccessProjectPrivateElements(policyService, token, En_Privilege.PROJECT_VIEW, team.getData())) {
                    query.setViewPrivate(false);
                }
                return;
            }
        }
    }

    private boolean allowedPrivateComment(AuthToken token, En_CaseType caseType, CaseComment comment) {
        if (token == null || caseType == null) {
            return true;
        }
        Set< UserRole > roles = token.getRoles();
        switch (caseType) {
            case CRM_SUPPORT: {
                if (!comment.isPrivateComment()) {
                    return true;
                }
                return policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW);
            }
            case PROJECT: {
                if (!comment.isPrivateComment()) {
                    return true;
                }
                Result<List<PersonProjectMemberView>> team = projectService.getProjectTeam(token, comment.getCaseId());
                if (team.isError()) {
                    return false;
                }
                return canAccessProjectPrivateElements(policyService, token, En_Privilege.PROJECT_VIEW, team.getData());
            }
        }
        return false;
    }

    private Result<List<CaseComment>> getList(List<CaseComment> comments) {
        if (comments == null) {
            return error( En_ResultStatus.GET_DATA_ERROR);
        }

        jdbcManyRelationsHelper.fill(comments, "caseAttachments");

        // RESET PRIVACY INFO
        comments.forEach(comment -> {
            if (comment.getAuthor() != null) {
                comment.getAuthor().resetPrivacyInfo();
            }
        });

        return ok(comments);
    }

    private En_ResultStatus checkAccessForCaseObjectById(AuthToken token, En_CaseType caseType, Long id) {
        return checkAccessForCaseObject(token, caseType, caseObjectDAO.get(id));
    }

    private En_ResultStatus checkAccessForCaseObjectByNumber(AuthToken token, En_CaseType caseType, Long caseNumber) {
        if (caseNumber == null) {
            return null;
        }
        return checkAccessForCaseObject(token, caseType, caseObjectDAO.getCaseByNumber(caseType, caseNumber));
    }

    private En_ResultStatus checkAccessForCaseObject(AuthToken token, En_CaseType caseType, CaseObject caseObject) {
        if (token == null || caseType == null) {
            return null;
        }
        switch (caseType) {
            case CRM_SUPPORT: {
                if (!policyService.hasAccessForCaseObject(token, En_Privilege.ISSUE_VIEW, caseObject)) {
                    return En_ResultStatus.PERMISSION_DENIED;
                }
                break;
            }
            case PROJECT: {
                Result<List<PersonProjectMemberView>> team = projectService.getProjectTeam(token, caseObject.getId());
                if (team.isError()) {
                    return team.getStatus();
                }
                if (!canAccessProject(policyService, token, En_Privilege.PROJECT_VIEW, team.getData())) {
                    return En_ResultStatus.PERMISSION_DENIED;
                }
                break;
            }
        }
        return null;
    }

    private boolean isCaseCommentReadOnlyByTime(Date date) {
        Calendar c = Calendar.getInstance();
        long current = c.getTimeInMillis();
        c.setTime(date);
        long checked = c.getTimeInMillis();

        return current - checked > CHANGE_LIMIT_TIME;
    }

    private void removeAttachments(AuthToken token, En_CaseType caseType, Collection<CaseAttachment> list) {
        list.forEach(ca -> attachmentService.removeAttachment(token, caseType, ca.getCaseId(), ca.getAttachmentId()));
    }

    /**
     * Заменяет в списке объектов возможные логины, которые начинаются с символа "@", на Фамилия Имя
     *
     * @param  objects                  список объектов
     * @param  objectToStringFunction   функция, переводящая переданный объект в строку, в которой будет производиться замена
     * @param  replacementMapper        {@link ReplacementMapper}
     * @return список {@link ReplaceLoginWithUsernameInfo}, содержащий объект и набор логинов в объекте
     */
    private <T> Result<List<ReplaceLoginWithUsernameInfo<T>>> replaceLoginWithUsername(List<T> objects, Function<T, String> objectToStringFunction, ReplacementMapper<T> replacementMapper) {
        if (isEmpty(objects)) {
            return ok(new ArrayList<>());
        }

        Set<String> possibleLoginSet = new HashSet<>(getPossibleLoginSet(toList(objects, objectToStringFunction)).getData());

        if (possibleLoginSet.isEmpty()) {
            return ok(objects.stream().map(ReplaceLoginWithUsernameInfo::new).collect(Collectors.toList()));
        }

        UserLoginShortViewQuery query = new UserLoginShortViewQuery();
        query.setAdminState(En_AdminState.UNLOCKED);
        query.setLoginSet(possibleLoginSet);

        SearchResult<UserLoginShortView> searchResult = userLoginShortViewDAO.getSearchResult(query);

        List<UserLoginShortView> existingLoginList = searchResult.getResults()
                .stream()
                .sorted((login1, login2) -> login2.getUlogin().length() - login1.getUlogin().length())
                .collect(Collectors.toList());

        return ok(makeReplacementInfoList(objects, objectToStringFunction, replacementMapper, existingLoginList));
    }

    private <T> List<ReplaceLoginWithUsernameInfo<T>> makeReplacementInfoList(List<T> objects, Function<T, String> objectToStringFunction, ReplacementMapper<T> replacementMapper, List<UserLoginShortView> existingLoginList) {
        List<ReplaceLoginWithUsernameInfo<T>> replacementInfoList = new ArrayList<>();

        for (T object : objects) {
            replacementInfoList.add(new ReplaceLoginWithUsernameInfo<>(object));
        }

        for (UserLoginShortView nextUserLogin : existingLoginList) {
            for (ReplaceLoginWithUsernameInfo<T> info : replacementInfoList) {
                String textBeforeReplace = objectToStringFunction.apply(info.getObject());

                T objectWithReplace = replacementMapper
                        .replace(info.getObject(), "@" + nextUserLogin.getUlogin(), "@" + nextUserLogin.getLastName() + " " + nextUserLogin.getFirstName());

                String textAfterReplace = objectToStringFunction.apply(objectWithReplace);

                boolean isReplaced = !Objects.equals(textBeforeReplace, textAfterReplace);

                if (isReplaced) {
                    info.setObject(objectWithReplace);
                    info.addUserLoginShortView(nextUserLogin);
                }
            }
        }

        return replacementInfoList;
    }

    private Result<Set<String>> getPossibleLoginSet(List<String> texts) {
        if (isEmpty(texts)) {
            return ok(new HashSet<>());
        }

        Set<String> possibleLoginSet = new HashSet<>();

        fillPossibleLoginSet(texts, possibleLoginSet);

        return ok(possibleLoginSet);
    }

    private void fillPossibleLoginSet(List<String> texts, Set<String> possibleLoginSet) {
        texts
                .stream()
                .filter(StringUtils::isNotBlank)
                .map(String::trim)
                .flatMap(text -> Arrays.stream(text.split(CrmConstants.Masks.ONE_OR_MORE_SPACES)))
                .flatMap(text -> Arrays.stream(text.split(CrmConstants.Masks.ROUND_AND_SQUARE_BRACKETS)))
                .filter(text -> text.startsWith("@"))
                .map(text -> text.substring(1))
                .filter(text -> text.length() <= CrmConstants.ContactConstants.LOGIN_SIZE)
                .forEach(text -> {
                    List<String> result = new ArrayList<>();
                    result.add(text);
                    result.addAll(subLoginList(text));
                    possibleLoginSet.addAll(prepareLoginList(result));
                });
    }

    private List<String> subLoginList(String text) {
        List<String> subLoginList = new ArrayList<>();

        for (int i = 1; i < text.toCharArray().length; i++) {
            subLoginList.add(text.substring(0, i));
        }

        return subLoginList;
    }

    private CaseComment replaceTextAndGetComment(CaseComment comment, String replaceFrom, String replaceTo) {
        if (comment.getText() == null) {
            return comment;
        }

        comment.setText(comment.getText().replace(replaceFrom, replaceTo));
        return comment;
    }

    private <T> List<T> objectListFromReplacementInfoList(List<ReplaceLoginWithUsernameInfo<T>> infos) {
        return infos.stream().map(ReplaceLoginWithUsernameInfo::getObject).collect(Collectors.toList());
    }

    private List<String> prepareLoginList(List<String> loginList) {
        return stream(loginList).map(login -> login.replace("'", "\\'")).collect(Collectors.toList());
    }

    private Result<Long> addCaseAttachmentHistory(AuthToken authToken, Long caseId, Long caseAttachmentId, String attachmentName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.ADD, En_HistoryType.CASE_ATTACHMENT,null, null, caseAttachmentId, attachmentName);
    }

    private Result<Long> removeCaseAttachmentHistory(AuthToken authToken, Long caseId, Long oldCaseAttachmentId, String oldAttachmentName) {
        return historyService.createHistory(authToken, caseId, En_HistoryAction.REMOVE, En_HistoryType.CASE_ATTACHMENT,oldCaseAttachmentId, oldAttachmentName, null, null);
    }

}
