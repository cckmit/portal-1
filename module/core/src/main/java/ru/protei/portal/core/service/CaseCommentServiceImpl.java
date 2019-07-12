package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.exception.ResultStatusException;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.struct.CaseCommentSaveOrUpdateResult;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class CaseCommentServiceImpl implements CaseCommentService {

    @Override
    public CoreResponse<List<CaseComment>> getCaseCommentList(AuthToken token, En_CaseType caseType, long caseObjectId) {
        En_ResultStatus checkAccessStatus = checkAccessForCaseObject(token, caseType, caseObjectId);
        if (checkAccessStatus != null) {
            return new CoreResponse<List<CaseComment>>().error(checkAccessStatus);
        }
        CaseCommentQuery query = new CaseCommentQuery(caseObjectId);
        applyFilterByScope( token, query );
        return getList(query);
    }

    @Override
    public CoreResponse<List<CaseComment>> getCaseCommentList(AuthToken token, En_CaseType caseType, CaseCommentQuery query) {
        applyFilterByScope( token, query );
        return getList(query);
    }

    @Override
    @Transactional
    public CoreResponse<CaseComment> addCaseComment(AuthToken token, En_CaseType caseType, CaseComment comment, Person person) {

        if (comment == null) {
            throw new ResultStatusException(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseObject caseObjectOld = caseObjectDAO.get(comment.getCaseId());

        CoreResponse<CaseCommentSaveOrUpdateResult> result = addCaseCommentWithoutEvent(token, caseType, comment);
        if (result.isError()) {
            throw new ResultStatusException(result.getStatus());
        }
        CaseCommentSaveOrUpdateResult resultData = result.getData();

        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            CaseObject caseObjectNew = getNewStateAndFillOldState(resultData.getCaseComment().getCaseId(), caseObjectOld);
            publisherService.publishEvent(new CaseCommentEvent.Builder(this)
                    .withPerson(person)
                    .withOldState(caseObjectOld)
                    .withNewState(caseObjectNew)
                    .withCaseComment(resultData.getCaseComment())
                    .withAddedAttachments(resultData.getAddedAttachments())
                    .build());
        }

        return new CoreResponse<CaseComment>().success(comment);
    }

    @Override
    @Transactional
    public CoreResponse<CaseCommentSaveOrUpdateResult> addCaseCommentWithoutEvent(AuthToken token, En_CaseType caseType, CaseComment comment) {

        if (comment == null) {
            throw new ResultStatusException(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_ResultStatus checkAccessStatus = checkAccessForCaseObject(token, caseType, comment.getCaseId());
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

        List<Long> addedAttachmentsIds = result.getCaseAttachments()
                .stream()
                .map(CaseAttachment::getAttachmentId)
                .collect(Collectors.toList());

        Collection<Attachment> addedAttachments = attachmentService.getAttachments(
                token,
                caseType,
                addedAttachmentsIds
        ).getData();

        return new CoreResponse<CaseCommentSaveOrUpdateResult>().success(new CaseCommentSaveOrUpdateResult(comment, addedAttachments));
    }

    @Override
    @Transactional
    public CoreResponse<CaseComment> updateCaseComment(AuthToken token, En_CaseType caseType, CaseComment comment, Person person) {

        CaseObject caseObjectOld = caseObjectDAO.get(comment.getCaseId());

        CoreResponse<CaseCommentSaveOrUpdateResult> result = updateCaseCommentWithoutEvent(token, caseType, comment, person);
        if (result.isError()) {
            throw new ResultStatusException(result.getStatus());
        }
        CaseCommentSaveOrUpdateResult resultData = result.getData();

        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            CaseObject caseObjectNew = getNewStateAndFillOldState(resultData.getCaseComment().getCaseId(), caseObjectOld);
            publisherService.publishEvent(new CaseCommentEvent.Builder(this)
                    .withPerson(person)
                    .withOldState(caseObjectOld)
                    .withNewState(caseObjectNew)
                    .withOldCaseComment(resultData.getOldCaseComment())
                    .withCaseComment(resultData.getCaseComment())
                    .withRemovedAttachments(resultData.getRemovedAttachments())
                    .withAddedAttachments(resultData.getAddedAttachments())
                    .build());
        }

        return new CoreResponse<CaseComment>().success(resultData.getCaseComment());
    }

    @Override
    @Transactional
    public CoreResponse<CaseCommentSaveOrUpdateResult> updateCaseCommentWithoutEvent(AuthToken token, En_CaseType caseType, CaseComment comment, Person person) {

        if (comment == null || comment.getId() == null || person == null) {
            throw new ResultStatusException(En_ResultStatus.INCORRECT_PARAMS);
        }

        En_ResultStatus checkAccessStatus = checkAccessForCaseObject(token, caseType, comment.getCaseId());
        if (checkAccessStatus != null) {
            throw new ResultStatusException(checkAccessStatus);
        }

        if (caseType == En_CaseType.CRM_SUPPORT && prohibitedPrivateComment(token, comment)) {
            throw new ResultStatusException(En_ResultStatus.PROHIBITED_PRIVATE_COMMENT);
        }

        if (!Objects.equals(person.getId(), comment.getAuthorId()) || isCaseCommentReadOnly(comment.getCreated())) {
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

        return new CoreResponse<CaseCommentSaveOrUpdateResult>().success(new CaseCommentSaveOrUpdateResult(comment, prevComment, addedAttachments, removedAttachments));
    }

    @Override
    @Transactional
    public CoreResponse<Boolean> removeCaseComment(AuthToken token, En_CaseType caseType, CaseComment removedComment, Person person) {

        En_ResultStatus checkAccessStatus = null;
        if (removedComment == null || removedComment.getId() == null || person == null || person.getId() == null) {
            checkAccessStatus = En_ResultStatus.INCORRECT_PARAMS;
        }
        if (checkAccessStatus == null) {
            checkAccessStatus = checkAccessForCaseObject(token, caseType, removedComment.getCaseId());
        }
        if (checkAccessStatus == null) {
            if (!Objects.equals(person.getId(), removedComment.getAuthorId()) || isCaseCommentReadOnly(removedComment.getCreated())) {
                checkAccessStatus = En_ResultStatus.NOT_REMOVED;
            }
        }
        if (checkAccessStatus != null) {
            throw new ResultStatusException(checkAccessStatus);
        }

        CaseObject caseObjectOld = caseObjectDAO.get(removedComment.getCaseId());
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
            if (!caseService.isExistsAttachments(removedComment.getCaseId())) {
                isCaseChanged = caseService.updateExistsAttachmentsFlag(removedComment.getCaseId(), false).getData();
            }
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

        CaseObject caseObjectNew = getNewStateAndFillOldState(removedComment.getCaseId(), caseObjectOld);
        publisherService.publishEvent(new CaseCommentEvent.Builder(this)
                .withOldState(caseObjectOld)
                .withNewState(caseObjectNew)
                .withRemovedCaseComment(removedComment)
                .withRemovedAttachments(removedAttachments)
                .withPerson(person)
                .build());

        return new CoreResponse<Boolean>().success(isRemoved);
    }

    @Override
    public CoreResponse<Long> getTimeElapsed(Long caseId) {
        List<CaseComment> allCaseComments = caseCommentDAO.partialGetListByCondition("CASE_ID=?", Collections.singletonList(caseId), "id", "time_elapsed");
        long sum = stream(allCaseComments)
                .filter(cmnt -> cmnt.getTimeElapsed() != null)
                .mapToLong(CaseComment::getTimeElapsed).sum();
        return new CoreResponse<Long>().success(sum);
    }

    @Override
    public CoreResponse<Boolean> updateTimeElapsed(AuthToken token, Long caseId) {
        long timeElapsed = getTimeElapsed(caseId).getData();
        return updateCaseTimeElapsed(token, caseId, timeElapsed);
    }

    @Override
    public CoreResponse<Boolean> updateCaseTimeElapsed(AuthToken token, Long caseId, long timeElapsed) {
        if (caseId == null || !caseObjectDAO.checkExistsByKey(caseId)) {
            return new CoreResponse<Boolean>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        CaseObject caseObject = new CaseObject(caseId);
        caseObject.setTimeElapsed(timeElapsed);

        boolean isUpdated = caseObjectDAO.partialMerge(caseObject, "time_elapsed");

        return new CoreResponse<Boolean>().success(isUpdated);
    }

    @Override
    @Transactional
    public CoreResponse<Long> addCommentOnSentReminder( CaseComment comment ) {
        comment.setCreated( new Date() );
        comment.setAuthorId( CrmConstants.Person.SYSTEM_USER_ID );
        Long commentId = caseCommentDAO.persist(comment);

        if (commentId == null) {
            return new CoreResponse<Long>().error(En_ResultStatus.NOT_CREATED);
        }

        return new CoreResponse<Long>().success(commentId);
    }


    private CoreResponse<List<CaseComment>> getList(CaseCommentQuery query) {
        List<CaseComment> comments = caseCommentDAO.getCaseComments(query);
        return getList(comments);
    }

    private void applyFilterByScope( AuthToken token, CaseCommentQuery query ) {
        if (token != null) {
            UserSessionDescriptor descriptor = authService.findSession(token);
            Set<UserRole> roles = descriptor.getLogin().getRoles();
            if (!policyService.hasGrantAccessFor(roles, En_Privilege.ISSUE_VIEW)) {
                query.setViewPrivate(false);
            }
        }
    }
    private boolean prohibitedPrivateComment(AuthToken token, CaseComment comment) {
        if (token != null) {
            UserSessionDescriptor descriptor = authService.findSession( token );
            Set< UserRole > roles = descriptor.getLogin().getRoles();
            return comment.isPrivateComment() && !policyService.hasGrantAccessFor( roles, En_Privilege.ISSUE_VIEW );
        } else {
            return false;
        }
    }

    private CoreResponse<List<CaseComment>> getList(List<CaseComment> comments) {
        if (comments == null) {
            return new CoreResponse<List<CaseComment>>().error(En_ResultStatus.GET_DATA_ERROR);
        }

        jdbcManyRelationsHelper.fill(comments, "caseAttachments");

        // RESET PRIVACY INFO
        comments.forEach(comment -> {
            if (comment.getAuthor() != null) {
                comment.getAuthor().resetPrivacyInfo();
            }
        });

        return new CoreResponse<List<CaseComment>>().success(comments);
    }

    private En_ResultStatus checkAccessForCaseObject(AuthToken token, En_CaseType caseType, long caseObjectId) {
        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            CaseObject caseObject = caseObjectDAO.get(caseObjectId);
            if (!caseService.hasAccessForCaseObject(token, En_Privilege.ISSUE_VIEW, caseObject)) {
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
    CaseAttachmentDAO caseAttachmentDAO;

    private static final long CHANGE_LIMIT_TIME = 300000;  // 5 минут (в мсек)
    private static Logger log = LoggerFactory.getLogger(CaseCommentServiceImpl.class);
}
