package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.CasePrivileged;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.dict.En_TimeElapsedType;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CommentsAndHistories;
import ru.protei.portal.core.model.ent.History;
import ru.protei.portal.core.model.query.CaseCommentQuery;
import ru.protei.portal.core.model.query.HistoryQuery;
import ru.protei.portal.core.model.struct.CaseCommentSaveOrUpdateResult;
import ru.protei.portal.core.model.struct.ReplaceLoginWithUsernameInfo;
import ru.protei.portal.core.model.struct.receivedmail.ReceivedMail;
import ru.protei.portal.core.model.view.CaseCommentShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

/**
 * Service to get/add/update/remove comments {@link CaseComment}
 * of types {@link En_CaseType}
 */
public interface CaseCommentService {

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAny = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAny = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAny = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAny = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAny = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAny = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT}),
            @CasePrivileged(caseType = En_CaseType.MODULE, requireAny = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    Result<List<CaseComment>> getCaseCommentList( AuthToken token, En_CaseType caseType, long caseObjectId);

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAny = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAny = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAny = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAny = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAny = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAny = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT}),
            @CasePrivileged(caseType = En_CaseType.MODULE, requireAny = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    Result<SearchResult<CaseCommentShortView>> getCaseCommentShortViewList(AuthToken token, En_CaseType caseType, CaseCommentQuery query);

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAll = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAll = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAll = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAll = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAll = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT}),
            @CasePrivileged(caseType = En_CaseType.MODULE, requireAll = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    @Auditable(value = En_AuditType.ISSUE_COMMENT_CREATE, forCases = En_CaseType.CRM_SUPPORT)
    Result<CaseComment> addCaseComment( AuthToken token, En_CaseType caseType, CaseComment comment );

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAll = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAll = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAll = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAll = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAll = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT}),
            @CasePrivileged(caseType = En_CaseType.MODULE, requireAll = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    @Auditable(value = En_AuditType.ISSUE_COMMENT_CREATE, forCases = En_CaseType.CRM_SUPPORT)
    Result<CaseCommentSaveOrUpdateResult> addCaseCommentWithoutEvent( AuthToken token, En_CaseType caseType, CaseComment comment);


    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAll = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAll = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAll = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAll = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAll = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT}),
            @CasePrivileged(caseType = En_CaseType.MODULE, requireAll = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    @Auditable(value = En_AuditType.ISSUE_COMMENT_MODIFY, forCases = En_CaseType.CRM_SUPPORT)
    Result<CaseComment> updateCaseComment( AuthToken token, En_CaseType caseType, CaseComment comment );

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAll = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAll = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAll = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAll = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAll = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT}),
            @CasePrivileged(caseType = En_CaseType.MODULE, requireAll = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    @Auditable(value = En_AuditType.ISSUE_COMMENT_MODIFY, forCases = En_CaseType.CRM_SUPPORT)
    Result<CaseCommentSaveOrUpdateResult> updateCaseCommentWithoutEvent( AuthToken token, En_CaseType caseType, CaseComment comment );


    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAll = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAll = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAll = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAll = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAll = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT}),
            @CasePrivileged(caseType = En_CaseType.MODULE, requireAll = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    @Auditable(value = En_AuditType.ISSUE_COMMENT_REMOVE, forCases = En_CaseType.CRM_SUPPORT)
    Result<Long> removeCaseComment( AuthToken token, En_CaseType caseType, CaseComment comment );

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAll = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAll = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAll = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAll = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAll = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT}),
            @CasePrivileged(caseType = En_CaseType.MODULE, requireAll = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    @Auditable(value = En_AuditType.ISSUE_COMMENT_MODIFY, forCases = En_CaseType.CRM_SUPPORT)
    Result<Boolean> updateCaseTimeElapsedType(AuthToken token, Long caseCommentId, En_TimeElapsedType type);

    Result<Long> getTimeElapsed( Long caseId);

    Result<Boolean> updateTimeElapsed( AuthToken token, Long caseId);

    Result<Boolean> updateCaseTimeElapsed( AuthToken token, Long caseId, long timeElapsed);

    Result<Long> addCommentOnSentReminder(CaseComment comment );

    Result<CaseComment> getCaseComment( AuthToken token, Long commentId );

    Result<Boolean> updateProjectCommentsFromYoutrack(AuthToken token, CaseComment comment );

    Result<Boolean> deleteProjectCommentsFromYoutrack(AuthToken token, String commentRemoteId);

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
    })
    Result<List<String>> replaceLoginWithUsername(AuthToken token, List<String> texts);

    Result<List<ReplaceLoginWithUsernameInfo<CaseComment>>> replaceLoginWithUsername(List<CaseComment> comments);

    Result<CommentsAndHistories> getCommentsAndHistories(AuthToken token, En_CaseType caseType, long caseObjectId);

    interface ReplacementMapper<T> {
        /**
         * Заменяет в объекте одну строку на другую
         *
         * @param replaceFrom подстрока, которая заменяется
         * @param replaceTo   строка, на которую происходит замена
         * @return объект, с произведенной заменой
         */
        T replace(T object, String replaceFrom, String replaceTo);
    }

    Result<Boolean> addCommentReceivedByMail(ReceivedMail receivedMails);
}
