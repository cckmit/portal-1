package ru.protei.portal.core.service;

import org.apache.commons.collections4.CollectionUtils;
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
import ru.protei.portal.core.model.helper.StringUtils;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.UserLoginShortViewQuery;
import ru.protei.portal.core.model.struct.CaseCommentSaveOrUpdateResult;
import ru.protei.portal.core.model.struct.ReplaceLoginWithUsernameInfo;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.view.CaseCommentShortView;
import ru.protei.portal.core.service.auth.AuthService;
import ru.protei.portal.core.service.events.EventPublisherService;
import ru.protei.portal.core.service.policy.PolicyService;
import ru.protei.portal.core.service.pushevent.ClientEventService;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcManyRelationsHelper;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class CaseCommentServiceImpl implements CaseCommentService {

    @Override
    public Result<List<CaseComment>> getCaseCommentList(AuthToken token, En_CaseType caseType, long caseObjectId) {
        En_ResultStatus checkAccessStatus = checkAccessForCaseObjectById(token, caseType, caseObjectId);
        if (checkAccessStatus != null) {
            return error(checkAccessStatus);
        }
        CaseCommentQuery query = new CaseCommentQuery(caseObjectId);
        applyFilterByScope(token, query);

        List<CaseComment> comments = getList(query).getData();

        if (needReplaceLoginWithUsername(caseType)) {
            return replaceLoginWithUsername(comments).map(this::objectListFromReplacementInfoList);
        }

        return ok(comments);
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
    public Result<List<String>> replaceLoginWithUsername(AuthToken token, List<String> texts) {
        return replaceLoginWithUsername(texts, Function.identity(), String::replace).map(this::objectListFromReplacementInfoList);
    }

    @Override
    public Result<List<ReplaceLoginWithUsernameInfo<CaseComment>>> replaceLoginWithUsername(List<CaseComment> comments) {
        return replaceLoginWithUsername(comments, CaseComment::getText, this::replaceTextAndGetComment);
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
            return ok(objects.stream().map(ReplaceLoginWithUsernameInfo::new).collect(Collectors.toList()));
        }

        Set<String> loginSet = new HashSet<>(getPossibleLoginSet(toList(objects, objectToStringFunction)).getData());

        if (loginSet.isEmpty()) {
            return ok(objects.stream().map(ReplaceLoginWithUsernameInfo::new).collect(Collectors.toList()));
        }

        UserLoginShortViewQuery query = new UserLoginShortViewQuery();
        query.setAdminState(En_AdminState.UNLOCKED);
        query.setLoginSet(loginSet);

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
                    info.addData(nextUserLogin);
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
                .filter(text -> text.startsWith("@"))
                .map(text -> text.substring(1))
                .filter(text -> text.length() <= CrmConstants.ContactConstants.LOGIN_SIZE)
                .forEach(text -> {
                    possibleLoginSet.add(text);
                    possibleLoginSet.addAll(subLoginList(text));
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

    private boolean needReplaceLoginWithUsername(En_CaseType caseType) {
        return En_CaseType.CRM_SUPPORT.equals(caseType);
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
    UserLoginShortViewDAO userLoginShortViewDAO;

    @Autowired
    private ClientEventService clientEventService;


    private static final long CHANGE_LIMIT_TIME = 300000;  // 5 минут (в мсек)
    private static Logger log = LoggerFactory.getLogger(CaseCommentServiceImpl.class);
}
