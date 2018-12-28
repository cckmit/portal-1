package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.event.CaseCommentEvent;
import ru.protei.portal.core.exception.InsufficientPrivilegesException;
import ru.protei.portal.core.model.dao.CaseAttachmentDAO;
import ru.protei.portal.core.model.dao.CaseCommentDAO;
import ru.protei.portal.core.model.dao.CaseObjectDAO;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.struct.AuditableObject;
import ru.protei.portal.core.service.user.AuthService;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.stream.Collectors;

import static ru.protei.portal.core.model.helper.CollectionUtils.stream;

public class CaseCommentServiceImpl implements CaseCommentService {

    @Override
    public CoreResponse<List<CaseComment>> getCaseCommentList(AuthToken token, En_CaseType caseType, long caseObjectId) {
        checkPrivilegesGetList(token, caseType);
        En_ResultStatus preStatus = checkAccessForCaseObject(token, caseType, caseObjectId);
        if (preStatus != null) {
            return new CoreResponse<List<CaseComment>>().error(preStatus);
        }
        return getList(caseObjectId);
    }

    @Override
    public CoreResponse<List<CaseComment>> getCaseCommentList(AuthToken token, En_CaseType caseType, CaseCommentQuery query) {
        checkPrivilegesGetList(token, caseType);
        return getList(query);
    }

    @Override
    public CoreResponse<CaseComment> addCaseComment(AuthToken token, En_CaseType caseType, CaseComment comment, Person person) {
        checkPrivilegesAdd(token, caseType);

        En_ResultStatus preStatus = checkAccessForCaseObject(token, caseType, comment.getCaseId());
        if (preStatus != null) {
            return new CoreResponse<CaseComment>().error(preStatus);
        }

        CaseObject caseObjectOld = caseObjectDAO.get(comment.getCaseId());
        CoreResponse<CaseComment> response = add(token, comment);
        if (response.isError()) {
            return response;
        }
        CaseComment result = response.getData();

        postAdd(token, caseType, result, caseObjectOld, person);

        return new CoreResponse<CaseComment>().success(result);
    }

    @Override
    public CoreResponse<CaseComment> updateCaseComment(AuthToken token, En_CaseType caseType, CaseComment comment, Person person) {
        checkPrivilegesUpdate(token, caseType);

        En_ResultStatus preStatus = checkAccessForCaseObject(token, caseType, comment.getCaseId());
        if (preStatus != null) {
            return new CoreResponse<CaseComment>().error(preStatus);
        }

        CaseComment prevComment = caseCommentDAO.get(comment.getId());
        CaseObject caseObjectOld = caseObjectDAO.get(comment.getCaseId());
        Collection<CaseAttachment> removedCaseAttachments = new ArrayList<>();
        CoreResponse<CaseComment> response = update(token, comment, person, prevComment, removedCaseAttachments);
        if (response.isError()) {
            return response;
        }
        CaseComment result = response.getData();

        postUpdate(token, caseType, result, person, prevComment, caseObjectOld, removedCaseAttachments);

        return new CoreResponse<CaseComment>().success(result);
    }

    @Override
    public CoreResponse<Boolean> removeCaseComment(AuthToken token, En_CaseType caseType, CaseComment comment, Long personId) {
        checkPrivilegesRemove(token, caseType);

        En_ResultStatus preStatus = null;
        if (comment == null || comment.getId() == null || personId == null) {
            preStatus = En_ResultStatus.INCORRECT_PARAMS;
        }
        if (preStatus == null) {
            preStatus = checkAccessForCaseObject(token, caseType, comment.getCaseId());
        }
        if (preStatus == null) {
            if (!personId.equals(comment.getAuthorId()) || isCaseCommentReadOnly(comment.getCreated())) {
                preStatus = En_ResultStatus.NOT_REMOVED;
            }
        }
        if (preStatus != null) {
            return new CoreResponse<Boolean>().error(preStatus);
        }

        CoreResponse<Boolean> response = remove(token, comment);
        if (response.isError()) {
            return response;
        }
        Boolean result = response.getData();

        postRemove(token, caseType, comment);

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

    // -> Get comments -> //

    private void checkPrivilegesGetList(AuthToken token, En_CaseType caseType) {
        switch (caseType) {
            case CRM_SUPPORT: checkRequireAnyPrivileges(token, En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT); break;
            case OFFICIAL: checkRequireAnyPrivileges(token, En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT); break;
            case PROJECT: checkRequireAnyPrivileges(token, En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT); break;
            case EMPLOYEE_REGISTRATION: checkRequireAnyPrivileges(token, En_Privilege.EMPLOYEE_REGISTRATION_VIEW); break;
        }
    }

    private CoreResponse<List<CaseComment>> getList(long caseId) {
        List<CaseComment> comments = caseCommentDAO.getCaseComments(caseId);
        return getList(comments);
    }

    private CoreResponse<List<CaseComment>> getList(CaseCommentQuery query) {
        List<CaseComment> comments = caseCommentDAO.getCaseComments(query);
        return getList(comments);
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

    // -> Add comment -> //

    private void checkPrivilegesAdd(AuthToken token, En_CaseType caseType) {
        switch (caseType) {
            case CRM_SUPPORT: checkRequireAllPrivileges(token, En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT); break;
            case OFFICIAL: checkRequireAllPrivileges(token, En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT); break;
            case PROJECT: checkRequireAllPrivileges(token, En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT); break;
            case EMPLOYEE_REGISTRATION: checkRequireAllPrivileges(token, En_Privilege.EMPLOYEE_REGISTRATION_VIEW); break;
        }
    }

    private void postAdd(AuthToken token, En_CaseType caseType, CaseComment comment, CaseObject caseObjectOld, Person person) {
        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            CaseObject caseObjectNew = caseObjectDAO.get(comment.getCaseId());
            jdbcManyRelationsHelper.fill(caseObjectNew, "attachments");
            jdbcManyRelationsHelper.fill(caseObjectNew, "notifiers");
            caseObjectOld.setAttachments(caseObjectNew.getAttachments());
            caseObjectOld.setNotifiers(caseObjectNew.getNotifiers());

            Collection<Long> addedAttachmentsIds = comment.getCaseAttachments()
                    .stream()
                    .map(CaseAttachment::getAttachmentId)
                    .collect(Collectors.toList());

            Collection<Attachment> addedAttachments = caseObjectNew.getAttachments()
                    .stream()
                    .filter(a -> addedAttachmentsIds.contains(a.getId()))
                    .collect(Collectors.toList());

            publisherService.publishEvent(new CaseCommentEvent(this, caseObjectNew, caseObjectOld, comment, addedAttachments, person));
        }
        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            tryDoAudit(token, En_AuditType.ISSUE_COMMENT_CREATE, comment);
        }
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

    // -> Update comment -> //

    private void checkPrivilegesUpdate(AuthToken token, En_CaseType caseType) {
        checkPrivilegesAdd(token, caseType);
    }

    private void postUpdate(AuthToken token, En_CaseType caseType, CaseComment comment, Person person,
                            CaseComment prevComment, CaseObject caseObjectOld, Collection<CaseAttachment> removedCaseAttachments) {
        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            CaseObject newState = caseObjectDAO.get(comment.getCaseId());
            jdbcManyRelationsHelper.fill( newState, "attachments");
            jdbcManyRelationsHelper.fill(newState, "notifiers");
            caseObjectOld.setAttachments(newState.getAttachments());
            caseObjectOld.setNotifiers(newState.getNotifiers());

            Collection<Attachment> removedAttachments = attachmentService.getAttachments(token, removedCaseAttachments).getData();
            Collection<Attachment> addedAttachments = attachmentService.getAttachments(token,
                    HelperFunc.subtract(comment.getCaseAttachments(), prevComment.getCaseAttachments())
            ).getData();

            publisherService.publishEvent(
                    new CaseCommentEvent(this, newState, caseObjectOld, prevComment, removedAttachments, comment, addedAttachments, person)
            );
        }
        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            tryDoAudit(token, En_AuditType.ISSUE_COMMENT_MODIFY, comment);
        }
    }

    private CoreResponse<CaseComment> update(AuthToken token, CaseComment comment, Person person,
                                             CaseComment prevComment, Collection<CaseAttachment> removedCaseAttachments) {

        if (comment == null || comment.getId() == null) {
            return new CoreResponse<CaseComment>().error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (person == null) {
            return new CoreResponse<CaseComment>().error(En_ResultStatus.NOT_UPDATED);
        }

        if (!person.getId().equals(comment.getAuthorId()) || !isCaseCommentReadOnly(comment.getCreated())) {
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
            removeAttachments(token, removedCaseAttachments);
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

    // -> Remove comment -> //

    private void checkPrivilegesRemove(AuthToken token, En_CaseType caseType) {
        checkPrivilegesAdd(token, caseType);
    }

    private void postRemove(AuthToken token, En_CaseType caseType, CaseComment comment) {
        if (En_CaseType.CRM_SUPPORT.equals(caseType)) {
            tryDoAudit(token, En_AuditType.ISSUE_COMMENT_REMOVE, comment);
        }
    }

    private CoreResponse<Boolean> remove(AuthToken token, CaseComment comment) {

        long caseId = comment.getCaseId();

        boolean isRemoved = caseCommentDAO.remove(comment);

        if (!isRemoved) {
            return new CoreResponse<Boolean>().error(En_ResultStatus.NOT_REMOVED);
        }

        boolean isCaseChanged = true;
        if (CollectionUtils.isNotEmpty(comment.getCaseAttachments())) {
            caseAttachmentDAO.removeByCommentId(caseId);
            comment.getCaseAttachments().forEach(ca -> attachmentService.removeAttachment(token, ca.getAttachmentId()));

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

    // -> Utils -> //

    private void checkRequireAllPrivileges(AuthToken token, En_Privilege... privileges) {
        if (token == null) {
            return;
        }
        UserSessionDescriptor descriptor = authService.findSession(token);
        if (!policyService.hasEveryPrivilegeOf(descriptor.getLogin().getRoles(), privileges)) {
            throw new InsufficientPrivilegesException();
        }
    }

    private void checkRequireAnyPrivileges(AuthToken token, En_Privilege... privileges) {
        if (token == null) {
            return;
        }
        UserSessionDescriptor descriptor = authService.findSession(token);
        if (!policyService.hasAnyPrivilegeOf(descriptor.getLogin().getRoles(), privileges)) {
            throw new InsufficientPrivilegesException();
        }
    }

    private void tryDoAudit(AuthToken token, En_AuditType auditType, AuditableObject auditableObject) {
        if (token == null) {
            return;
        }
        auditService.publishAuditObject(token, auditType, auditableObject);
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

        return current - checked < CHANGE_LIMIT_TIME;
    }

    private void removeAttachments(AuthToken token, Collection<CaseAttachment> list) {
        list.forEach(ca -> attachmentService.removeAttachment(token, ca.getAttachmentId()));
    }

    @Autowired
    AuthService authService;
    @Autowired
    PolicyService policyService;
    @Autowired
    AuditService auditService;
    @Autowired
    CaseService caseService;
    @Autowired
    AttachmentService attachmentService;
    @Autowired
    EventPublisherService publisherService;

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
