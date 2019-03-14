package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.CaseAuditable;
import ru.protei.portal.core.model.annotations.CasePrivileged;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.CaseCommentQuery;

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
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAny = En_Privilege.EMPLOYEE_REGISTRATION_VIEW)
    })
    CoreResponse<List<CaseComment>> getCaseCommentList(AuthToken token, En_CaseType caseType, long caseObjectId);

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAny = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAny = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAny = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAny = En_Privilege.EMPLOYEE_REGISTRATION_VIEW)
    })
    CoreResponse<List<CaseComment>> getCaseCommentList(AuthToken token, En_CaseType caseType, CaseCommentQuery query);

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAll = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAll = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAll = En_Privilege.EMPLOYEE_REGISTRATION_VIEW)
    })
    @Auditable(forCases = {
            @CaseAuditable(caseType = En_CaseType.CRM_SUPPORT, auditType = En_AuditType.ISSUE_COMMENT_CREATE)
    })
    CoreResponse<CaseComment> addCaseComment(AuthToken token, En_CaseType caseType, CaseComment comment, Person person);

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAll = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAll = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAll = En_Privilege.EMPLOYEE_REGISTRATION_VIEW)
    })
    @Auditable(forCases = {
            @CaseAuditable(caseType = En_CaseType.CRM_SUPPORT, auditType = En_AuditType.ISSUE_COMMENT_MODIFY)
    })
    CoreResponse<CaseComment> updateCaseComment(AuthToken token, En_CaseType caseType, CaseComment comment, Person person);

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAll = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAll = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAll = En_Privilege.EMPLOYEE_REGISTRATION_VIEW)
    })
    @Auditable(forCases = {
            @CaseAuditable(caseType = En_CaseType.CRM_SUPPORT, auditType = En_AuditType.ISSUE_COMMENT_REMOVE)
    })
    CoreResponse<Boolean> removeCaseComment(AuthToken token, En_CaseType caseType, CaseComment comment, Long personId);

    CoreResponse<Long> getTimeElapsed(Long caseId);

    CoreResponse<Boolean> updateTimeElapsed(AuthToken token, Long caseId);

    CoreResponse<Boolean> updateCaseTimeElapsed(AuthToken token, Long caseId, long timeElapsed);

    CoreResponse<Long> addCommentOnSentReminder( CaseComment comment );
}
