package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Whitelist;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.ServiceModule;
import ru.protei.portal.core.event.CaseAttachmentEvent;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.event.ProjectAttachmentEvent;
import ru.protei.portal.core.event.ProjectCommentEvent;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.exception.RollbackTransactionException;
import ru.protei.portal.core.model.dao.*;
import ru.protei.portal.core.model.dict.*;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.event.CaseCommentRemovedClientEvent;
import ru.protei.portal.core.model.event.CaseCommentSavedClientEvent;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.struct.CaseCommentSaveOrUpdateResult;
import ru.protei.portal.core.model.struct.receivedmail.MailReceiveContentAndType;
import ru.protei.portal.core.model.struct.receivedmail.ReceivedMail;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseCommentShortView;
import ru.protei.portal.core.model.view.EmployeeShortView;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.pushevent.ClientEventService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.dict.En_Privilege.ISSUE_EDIT;
import static ru.protei.portal.core.model.helper.CollectionUtils.stream;
import static ru.protei.portal.core.model.helper.CollectionUtils.toList;

public class CaseCommentServiceImpl implements CaseCommentService {

    @Override
    public Result<List<CaseComment>> getCaseCommentList(AuthToken token, En_CaseType caseType, long caseObjectId) {
        En_ResultStatus checkAccessStatus = checkAccessForCaseObjectById(token, caseType, caseObjectId);
        if (checkAccessStatus != null) {
            return error(checkAccessStatus);
        }
        CaseCommentQuery query = new CaseCommentQuery(caseObjectId);
        applyFilterByScope(token, query);
        return getList(query);
    }

    @Override
    public Result<SearchResult<CaseCommentShortView>> getCaseCommentShortViewList(AuthToken token, En_CaseType caseType, CaseCommentQuery query) {
        En_ResultStatus checkAccessStatus = checkAccessForCaseObjectByNumber(token, caseType, query.getCaseNumber());
        if (checkAccessStatus != null) {
            return error(checkAccessStatus);
        }
        applyFilterByScope(token, query);
        return ok(caseCommentShortViewDAO.getSearchResult(query));
    }

    @Override
    @Transactional
    public Result<CaseComment> addCaseComment( AuthToken token, En_CaseType caseType, CaseComment comment) {

        if (comment == null) {
            throw new ResultStatusException(En_ResultStatus.INCORRECT_PARAMS);
        }

        Result<CaseCommentSaveOrUpdateResult> result = addCaseCommentWithoutEvent(token, caseType, comment);
        if (result.isError()) {
            throw new ResultStatusException(result.getStatus());
        }
        CaseCommentSaveOrUpdateResult resultData = result.getData();

        Result<CaseComment> okResult = ok( resultData.getCaseComment() );
        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            okResult.publishEvent( new CaseAttachmentEvent(this, ServiceModule.GENERAL, token.getPersonId(), comment.getCaseId(),
                    resultData.getAddedAttachments(), null
            ));
            boolean isEagerEvent = En_ExtAppType.REDMINE.getCode().equals( caseObjectDAO.getExternalAppName( comment.getCaseId() ) );

            okResult.publishEvent( new CaseCommentEvent( this, ServiceModule.GENERAL, token.getPersonId(), comment.getCaseId(), isEagerEvent,
                    null, resultData.getCaseComment(), null) );

        }

        if (En_CaseType.PROJECT.equals(caseType)) {
            okResult.publishEvent(new ProjectCommentEvent(
                    this, null, resultData.getCaseComment(), null,
                    token.getPersonId(), comment.getCaseId())
            );

            okResult.publishEvent(new ProjectAttachmentEvent(this, resultData.getAddedAttachments(), resultData.getRemovedAttachments(), comment.getId(),
                    token.getPersonId(), comment.getCaseId())
            );
        }

        if (resultData.getCaseComment() != null) {
            clientEventService.fireEvent( new CaseCommentSavedClientEvent( token.getPersonId(), comment.getCaseId(), resultData.getCaseComment().getId() ) );
        }

        return okResult;
    }

    @Override
    @Transactional
    public Result<CaseCommentSaveOrUpdateResult> addCaseCommentWithoutEvent( AuthToken token, En_CaseType caseType, CaseComment comment) {

        if (comment == null) {
            throw new ResultStatusException(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_ResultStatus checkAccessStatus = checkAccessForCaseObjectById(token, caseType, comment.getCaseId());
        if (checkAccessStatus != null) {
            throw new ResultStatusException(checkAccessStatus);
        }

        if (caseType == En_CaseType.CRM_SUPPORT && prohibitedPrivateComment(token, comment)) {
            throw new ResultStatusException(En_ResultStatus.PROHIBITED_PRIVATE_COMMENT);
        }

        comment.setCreated(new Date());
        Long commentId = caseCommentDAO.persist(comment);
        if (commentId == null) {
            log.info("Failed to create comment at db: {}", comment);
            throw new ResultStatusException(En_ResultStatus.NOT_CREATED);
        }

        if (CollectionUtils.isNotEmpty(comment.getCaseAttachments())) {
            caseService.updateExistsAttachmentsFlag(comment.getCaseId(), true);
            comment.getCaseAttachments().forEach(ca -> ca.setCommentId(commentId));
            caseAttachmentDAO.persistBatch(comment.getCaseAttachments());
        }

        boolean isCaseChanged = caseService.updateCaseModified(token, comment.getCaseId(), comment.getCreated()).getData();
        if (!isCaseChanged) {
            log.info("Failed to update case modifiedDate: {}", comment);
            throw new ResultStatusException(En_ResultStatus.NOT_CREATED);
        }

        if (!updateTimeElapsed(token, comment.getCaseId()).getData()) {
            log.info("Failed to update time elapsed on addCaseComment: {}", comment);
            throw new ResultStatusException(En_ResultStatus.NOT_CREATED);
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
            throw new ResultStatusException(result.getStatus());
        }
        CaseCommentSaveOrUpdateResult resultData = result.getData();

        Result<CaseComment> okResult = ok( resultData.getCaseComment() );
        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            boolean isEagerEvent = En_ExtAppType.REDMINE.getCode().equals( caseObjectDAO.getExternalAppName( comment.getCaseId() ) );
            okResult.publishEvent( new CaseAttachmentEvent(this, ServiceModule.GENERAL, token.getPersonId(), comment.getCaseId(),
                    resultData.getAddedAttachments(), resultData.getRemovedAttachments())
            );
            okResult.publishEvent( new CaseCommentEvent(this, ServiceModule.GENERAL, token.getPersonId(), comment.getCaseId(),
                            isEagerEvent, resultData.getOldCaseComment(), resultData.getCaseComment(), null ));
        }

        if (En_CaseType.PROJECT.equals(caseType)) {
            okResult.publishEvent(new ProjectCommentEvent(this,
                    resultData.getOldCaseComment(), resultData.getCaseComment(), null,
                    token.getPersonId(), comment.getCaseId())
            );

            okResult.publishEvent(new ProjectAttachmentEvent(this, resultData.getAddedAttachments(), resultData.getRemovedAttachments(), comment.getId(),
                    token.getPersonId(), comment.getCaseId())
            );
        }

        if (resultData.getCaseComment() != null) {
            clientEventService.fireEvent( new CaseCommentSavedClientEvent( token.getPersonId(), comment.getCaseId(), resultData.getCaseComment().getId() ) );
        }

        return okResult;
    }

    @Override
    @Transactional
    public Result<CaseCommentSaveOrUpdateResult> updateCaseCommentWithoutEvent( AuthToken token, En_CaseType caseType, CaseComment comment) {

        if (comment == null || comment.getId() == null || token.getPersonId() == null) {
            throw new ResultStatusException(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_ResultStatus checkAccessStatus = checkAccessForCaseObjectById(token, caseType, comment.getCaseId());
        if (checkAccessStatus != null) {
            throw new ResultStatusException(checkAccessStatus);
        }

        if (caseType == En_CaseType.CRM_SUPPORT && prohibitedPrivateComment(token, comment)) {
            throw new ResultStatusException(En_ResultStatus.PROHIBITED_PRIVATE_COMMENT);
        }

        if (!Objects.equals(token.getPersonId(), comment.getAuthorId()) || isCaseCommentReadOnly(comment.getCreated())) {
            throw new ResultStatusException(En_ResultStatus.NOT_AVAILABLE);
        }

        CaseComment prevComment = caseCommentDAO.get(comment.getId());

        jdbcManyRelationsHelper.fill(prevComment, "caseAttachments");

        boolean isCommentUpdated = caseCommentDAO.merge(comment);
        if (!isCommentUpdated) {
            log.info("Failed to update comment {} at db", comment.getId());
            throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
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
            throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
        }

        if (!updateTimeElapsed(token, comment.getCaseId()).getData()) {
            log.info("Failed to update time elapsed on updateCaseComment for comment {}", comment.getId());
            throw new ResultStatusException(En_ResultStatus.NOT_UPDATED);
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

        return ok( new CaseCommentSaveOrUpdateResult(comment, prevComment, addedAttachments, removedAttachments));
    }

    @Override
    @Transactional
    public Result<Boolean> removeCaseComment( AuthToken token, En_CaseType caseType, CaseComment removedComment) {

        En_ResultStatus checkAccessStatus = null;
        if (removedComment == null || removedComment.getId() == null || token.getPersonId() == null) {
            checkAccessStatus = En_ResultStatus.INCORRECT_PARAMS;
        }
        if (checkAccessStatus == null) {
            checkAccessStatus = checkAccessForCaseObjectById(token, caseType, removedComment.getCaseId());
        }
        if (checkAccessStatus == null) {
            if (!Objects.equals(token.getPersonId(), removedComment.getAuthorId()) || isCaseCommentReadOnly(removedComment.getCreated())) {
                checkAccessStatus = En_ResultStatus.NOT_REMOVED;
            }
        }
        if (checkAccessStatus != null) {
            throw new ResultStatusException(checkAccessStatus);
        }

        Collection<Attachment> removedAttachments = attachmentService.getAttachments(
                token,
                caseType,
                removedComment.getCaseAttachments()
        ).getData();

        long caseId = removedComment.getCaseId();

        boolean isRemoved = caseCommentDAO.remove(removedComment);
        if (!isRemoved) {
            log.info("Failed to remove comment {} at db", removedComment.getId());
            throw new ResultStatusException(En_ResultStatus.NOT_REMOVED);
        }

        boolean isCaseChanged = true;
        if (CollectionUtils.isNotEmpty(removedComment.getCaseAttachments())) {
            caseAttachmentDAO.removeByCommentId(caseId);
            removedComment.getCaseAttachments().forEach(ca -> attachmentService.removeAttachment(token, caseType, ca.getAttachmentId()));
            isCaseChanged = caseService.isExistAnyAttachments( toList(removedComment.getCaseAttachments(), CaseAttachment::getAttachmentId) ).flatMap(isExists -> {
                if (isExists) {
                    return ok( false );
                }
                return caseService.updateExistsAttachmentsFlag( removedComment.getCaseId(), false );
            } ).orElseGet( result -> ok( false ) ).getData();
        }
        isCaseChanged &= caseService.updateCaseModified(token, caseId, new Date()).getData();
        if (!isCaseChanged) {
            log.info("Failed to update case modifiedDate for comment {}", removedComment.getId());
            throw new ResultStatusException(En_ResultStatus.NOT_REMOVED);
        }

        if (!updateTimeElapsed(token, removedComment.getCaseId()).getData()) {
            log.info("Failed to update time elapsed on removeCaseComment for comment {}", removedComment.getId());
            throw new ResultStatusException(En_ResultStatus.NOT_REMOVED);
        }

        Result<Boolean> okResult = ok(isRemoved);

        if (En_CaseType.PROJECT.equals(caseType)) {
            okResult.publishEvent(new ProjectCommentEvent(this,
                    null, null, removedComment, token.getPersonId(), caseId)
            );

            okResult.publishEvent(new ProjectAttachmentEvent(this, Collections.emptyList(), removedAttachments, removedComment.getId(),
                    token.getPersonId(), caseId)
            );
        }

        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            boolean isEagerEvent = En_ExtAppType.REDMINE.getCode().equals(caseObjectDAO.getExternalAppName(caseId));

            okResult
                    .publishEvent(new CaseAttachmentEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, null, removedAttachments))
                    .publishEvent(new CaseCommentEvent(this, ServiceModule.GENERAL, token.getPersonId(), caseId, isEagerEvent, null, null, removedComment));
        }

        if(isRemoved) {
            clientEventService.fireEvent( new CaseCommentRemovedClientEvent( token.getPersonId(), caseId, removedComment.getId() ));
        }

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
    public Result<Boolean> updateTimeElapsed( AuthToken token, Long caseId) {
        long timeElapsed = getTimeElapsed(caseId).getData();
        return updateCaseTimeElapsed(token, caseId, timeElapsed);
    }

    @Override
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
        comment.setAuthorId( CrmConstants.Person.SYSTEM_USER_ID );
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
            throw new RollbackTransactionException("updatedCaseComments size = " + updatedCaseComments.size() + " but updatedCount = " + updatedCount);
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
            throw new RollbackTransactionException("caseComments size = " + caseComments.size() + " but removedCount = " + removedCount);
        }

        List<ApplicationEvent> events = new ArrayList<>();
        for (CaseComment caseComment : caseComments) {
            events.add(new ProjectCommentEvent(this,
                    null, null, caseComment, token.getPersonId(), caseComment.getCaseId()));
        }

        return ok(true).publishEvents(events);
    }

    @Override
    @Transactional
    public Result<Void> addCommentReceivedByMail(ReceivedMail receivedMail) {
        if (receivedMail.getCaseNo() == null || receivedMail.getSenderEmail() == null) {
            log.warn("addCommentsReceivedByMail(): no case no or sender mail receivedMail ={}", receivedMail);
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        EmployeeShortView employeeShortView = employeeShortViewDAO.getEmployeeByEmail(receivedMail.getSenderEmail());
        if (employeeShortView == null) {
            log.warn("addCommentsReceivedByMail(): no found employee by email={}", receivedMail.getSenderEmail());
            return error(En_ResultStatus.NOT_FOUND);
        }

        List<UserLogin> userLogins = userLoginDAO.findByPersonId( employeeShortView.getId() );
        if (userLogins.isEmpty()) {
            log.warn("addCommentsReceivedByMail(): no found user login by email ={}", receivedMail.getSenderEmail());
            return error(En_ResultStatus.NOT_FOUND);
        }

        UserLogin login = userLogins.get(0);
        jdbcManyRelationsHelper.fill( login, "roles" );
        if (!policyService.hasGrantAccessFor(login.getRoles(), ISSUE_EDIT)) {
            log.warn("addCommentsReceivedByMail(): no privilege for create comment ={}", receivedMail.getSenderEmail());
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        Person person = personDAO.get(employeeShortView.getId());
        if (person == null) {
            log.warn("addCommentsReceivedByMail(): no found person person by mail ={}", receivedMail.getSenderEmail());
            return error(En_ResultStatus.NOT_FOUND);
        }

        CaseObject caseObject = caseObjectDAO.getCaseByCaseno(receivedMail.getCaseNo());
        if (caseObject == null) {
            log.warn("addCommentsReceivedByMail(): no found case for case no ={}", receivedMail.getCaseNo());
            return error(En_ResultStatus.NOT_FOUND);
        }

        log.info("addCommentsReceivedByMail(): process receivedMail={}", receivedMail);
        caseCommentDAO.persist(
                createComment(caseObject, person, receivedMail.getContentAndTypes())
        );

        return ok();
    }

    private CaseComment createComment(CaseObject caseObject, Person person, List<MailReceiveContentAndType> contentAndTypes) {
        CaseComment caseComment = new CaseComment();
        caseComment.setCaseId(caseObject.getId());
        caseComment.setAuthor(person);
        caseComment.setCreated(new Date());
        caseComment.setOriginalAuthorFullName(person.getDisplayName());
        caseComment.setOriginalAuthorName(person.getDisplayName());
        String comment = contentAndTypes.stream()
                .map(contentAndType -> contentAndType.getContentType().startsWith("TEXT/HTML") ?
                        cleanHTMLContent(contentAndType.getContent())
                        : contentAndType.getContent())
                .collect(Collectors.joining("\n"));
        caseComment.setText(comment);

        return caseComment;
    }

    private String cleanHTMLContent(String htmlContent) {
        return Jsoup.clean(Jsoup.parse(htmlContent).html(),
                "", Whitelist.none(), new Document.OutputSettings().prettyPrint(false));
    }

    private Result<List<CaseComment>> getList(CaseCommentQuery query) {
        List<CaseComment> comments = caseCommentDAO.getCaseComments(query);
        return getList(comments);
    }

    private void applyFilterByScope( AuthToken token, CaseCommentQuery query ) {
        if (token != null) {
            Set<UserRole> roles = token.getRoles();
            if (!policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
                query.setViewPrivate(false);
            }
        }
    }
    private boolean prohibitedPrivateComment(AuthToken token, CaseComment comment) {
        if (token != null) {
            Set< UserRole > roles = token.getRoles();
            return comment.isPrivateComment() && !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_VIEW );
        } else {
            return false;
        }
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
        return checkAccessForCaseObject(token, caseType, caseObjectDAO.getCaseByCaseno(caseNumber));
    }

    private En_ResultStatus checkAccessForCaseObject(AuthToken token, En_CaseType caseType, CaseObject caseObject) {
        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            if (!policyService.hasAccessForCaseObject(token, En_Privilege.ISSUE_VIEW, caseObject)) {
                return En_ResultStatus.PERMISSION_DENIED;
            }
        }
        return null;
    }

    private boolean isCaseCommentReadOnly(Date date) {
        Calendar c = Calendar.getInstance();
        long current = c.getTimeInMillis();
        c.setTime(date);
        long checked = c.getTimeInMillis();

        return current - checked > CHANGE_LIMIT_TIME;
    }

    private void removeAttachments(AuthToken token, En_CaseType caseType, Collection<CaseAttachment> list) {
        list.forEach(ca -> attachmentService.removeAttachment(token, caseType, ca.getAttachmentId()));
    }

    private CaseObject getNewStateAndFillOldState(Long caseId, CaseObject oldState) {
        CaseObject newState = caseObjectDAO.get(caseId);
        jdbcManyRelationsHelper.fill(newState, "attachments");
        jdbcManyRelationsHelper.fill(newState, "notifiers");
        oldState.setAttachments(newState.getAttachments());
        oldState.setNotifiers(newState.getNotifiers());
        return newState;
    }

    @Autowired
    CaseService caseService;
    @Autowired
    AttachmentService attachmentService;
    @Autowired
    EventPublisherService publisherService;

    @Autowired
    PolicyService policyService;
    @Autowired
    AuthService authService;

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
    EmployeeShortViewDAO employeeShortViewDAO;
    @Autowired
    PersonDAO personDAO;
    @Autowired
    UserLoginDAO userLoginDAO;

    @Autowired
    private ClientEventService clientEventService;


    private static final long CHANGE_LIMIT_TIME = 300000;  // 5 минут (в мсек)
    private static Logger log = LoggerFactory.getLogger(CaseCommentServiceImpl.class);
}
