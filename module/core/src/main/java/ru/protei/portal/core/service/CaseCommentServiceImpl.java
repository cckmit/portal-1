package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseCommentQuery;
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
        En_ResultStatus checkAccessStatus = checkAccessForCaseObject(token, caseType, comment.getCaseId());
        if (checkAccessStatus != null) {
            return new CoreResponse<CaseComment>().error(checkAccessStatus);
        }
        if (caseType == En_CaseType.CRM_SUPPORT && prohibitedPrivateComment(token, comment)) {
            return new CoreResponse<CaseComment>().error(En_ResultStatus.PROHIBITED_PRIVATE_COMMENT);
        }

        CaseObject caseObjectOld = caseObjectDAO.get(comment.getCaseId());
        CoreResponse<CaseComment> response = add(token, comment);
        if (response.isError()) {
            return response;
        }
        comment = response.getData();

        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            CaseObject caseObjectNew = getNewStateAndFillOldState(comment.getCaseId(), caseObjectOld);

            Collection<Long> addedAttachmentsIds = comment.getCaseAttachments()
                    .stream()
                    .map(CaseAttachment::getAttachmentId)
                    .collect(Collectors.toList());

            Collection<Attachment> addedAttachments = caseObjectNew.getAttachments()
                    .stream()
                    .filter(a -> addedAttachmentsIds.contains(a.getId()))
                    .collect(Collectors.toList());

            publisherService.publishEvent(new CaseCommentEvent(this, caseObjectNew, caseObjectOld, comment, addedAttachments, person, null, null ));
        }

        return new CoreResponse<CaseComment>().success(comment);
    }

    @Override
    @Transactional
    public CoreResponse<CaseComment> updateCaseComment(AuthToken token, En_CaseType caseType, CaseComment comment, Person person) {
        En_ResultStatus checkAccessStatus = checkAccessForCaseObject(token, caseType, comment.getCaseId());
        if (checkAccessStatus != null) {
            return new CoreResponse<CaseComment>().error(checkAccessStatus);
        }
        if (caseType == En_CaseType.CRM_SUPPORT && prohibitedPrivateComment(token, comment)) {
            return new CoreResponse<CaseComment>().error(En_ResultStatus.PROHIBITED_PRIVATE_COMMENT);
        }

        CaseComment prevComment = caseCommentDAO.get(comment.getId());
        CaseObject caseObjectOld = caseObjectDAO.get(comment.getCaseId());
        Collection<CaseAttachment> removedCaseAttachments = new ArrayList<>();
        CoreResponse<CaseComment> response = update(token, caseType, comment, person, prevComment, removedCaseAttachments);
        if (response.isError()) {
            return response;
        }
        comment = response.getData();

        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            CaseObject caseObjectNew = getNewStateAndFillOldState(comment.getCaseId(), caseObjectOld);

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

            publisherService.publishEvent(new CaseCommentEvent(this, caseObjectNew, caseObjectOld, prevComment, removedAttachments, comment, addedAttachments, person));
        }

        return new CoreResponse<CaseComment>().success(comment);
    }

    @Override
    @Transactional
    public CoreResponse<Boolean> removeCaseComment(AuthToken token, En_CaseType caseType, CaseComment removedComment, Person person) {
        En_ResultStatus checkAccessStatus = null;
        if (removedComment == null || removedComment.getId() == null || person == null) {
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
            return new CoreResponse<Boolean>().error(checkAccessStatus);
        }

        CaseObject caseObjectOld = caseObjectDAO.get( removedComment.getCaseId());
        Collection<Attachment> removedAttachments = attachmentService.getAttachmentsByCaseId(token, caseType, removedComment.getCaseId())
                .getData();

        CoreResponse<Boolean> response = remove(token, caseType, removedComment);
        if (response.isError()) {
            return response;
        }

        if (!updateTimeElapsed(token, removedComment.getCaseId()).getData()) {
            throw new RuntimeException("failed to update time elapsed on removeCaseComment");
        }

        CaseObject caseObjectNew = getNewStateAndFillOldState(removedComment.getCaseId(), caseObjectOld);

        publisherService.publishEvent(new CaseCommentEvent(this, caseObjectNew, caseObjectOld, null, null, person, removedComment, removedAttachments));

        Boolean result = response.getData();

        return new CoreResponse<Boolean>().success(result);
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

    private CoreResponse<CaseComment> add(AuthToken token, CaseComment comment) {

        if (comment == null) {
            return new CoreResponse<CaseComment>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Date now = new Date();
        comment.setCreated(now);

        Long commentId = caseCommentDAO.persist(comment);

        if (commentId == null) {
            return new CoreResponse<CaseComment>().error(En_ResultStatus.NOT_CREATED);
        }

        if (CollectionUtils.isNotEmpty(comment.getCaseAttachments())) {
            caseService.updateExistsAttachmentsFlag(comment.getCaseId(), true);
            comment.getCaseAttachments().forEach(ca -> ca.setCommentId(commentId));
            caseAttachmentDAO.persistBatch(comment.getCaseAttachments());
        }

        boolean isCaseChanged = caseService.updateCaseModified(token, comment.getCaseId(), comment.getCreated()).getData();

        if (!isCaseChanged) {
            throw new RuntimeException("failed to update case modifiedDate");
        }

        if (!updateTimeElapsed(token, comment.getCaseId()).getData()) {
            throw new RuntimeException("failed to update time elapsed on addCaseComment");
        }

        // re-read data from db to get full-filled object
        CaseComment result = caseCommentDAO.get(commentId);

        // attachments won't read now from DAO
        result.setCaseAttachments(comment.getCaseAttachments());

        return new CoreResponse<CaseComment>().success( result );
    }

    private CoreResponse<CaseComment> update(AuthToken token, En_CaseType caseType, CaseComment comment, Person person,
                                             CaseComment prevComment, Collection<CaseAttachment> removedCaseAttachments) {

        if (comment == null || comment.getId() == null) {
            return new CoreResponse<CaseComment>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (person == null) {
            return new CoreResponse<CaseComment>().error(En_ResultStatus.NOT_UPDATED);
        }

        if (!person.getId().equals(comment.getAuthorId()) || isCaseCommentReadOnly(comment.getCreated())) {
            return new CoreResponse<CaseComment>().error(En_ResultStatus.NOT_UPDATED);
        }

        jdbcManyRelationsHelper.fill(prevComment, "caseAttachments");

        boolean isCommentUpdated = caseCommentDAO.merge(comment);

        if (!isCommentUpdated) {
            return new CoreResponse<CaseComment>().error(En_ResultStatus.NOT_UPDATED);
        }

        removedCaseAttachments.addAll(caseAttachmentDAO.calcDiffAndSynchronize(
                prevComment.getCaseAttachments(),
                comment.getCaseAttachments()
        ));

        if (!removedCaseAttachments.isEmpty()) {
            removeAttachments(token, caseType, removedCaseAttachments);
        }

        boolean isCaseChanged =
                caseService.updateExistsAttachmentsFlag(comment.getCaseId()).getData()
                        && caseService.updateCaseModified(token, comment.getCaseId(), new Date()).getData();

        if (!isCaseChanged) {
            throw new RuntimeException("failed to update case modifiedDate");
        }

        if (!updateTimeElapsed(token, comment.getCaseId()).getData()) {
            throw new RuntimeException("failed to update time elapsed on updateCaseComment");
        }

        return new CoreResponse<CaseComment>().success(comment);
    }

    private CoreResponse<Boolean> remove(AuthToken token, En_CaseType caseType, CaseComment comment) {

        long caseId = comment.getCaseId();

        boolean isRemoved = caseCommentDAO.remove(comment);

        if (!isRemoved) {
            return new CoreResponse<Boolean>().error(En_ResultStatus.NOT_REMOVED);
        }

        boolean isCaseChanged = true;
        if (CollectionUtils.isNotEmpty(comment.getCaseAttachments())) {
            caseAttachmentDAO.removeByCommentId(caseId);
            comment.getCaseAttachments().forEach(ca -> attachmentService.removeAttachment(token, caseType, ca.getAttachmentId()));

            if (!caseService.isExistsAttachments(comment.getCaseId())) {
                isCaseChanged = caseService.updateExistsAttachmentsFlag(comment.getCaseId(), false).getData();
            }
        }

        isCaseChanged &= caseService.updateCaseModified(token, caseId, new Date()).getData();

        if (!isCaseChanged) {
            throw new RuntimeException("failed to update case modifiedDate");
        }

        return new CoreResponse<Boolean>().success(isRemoved);
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
