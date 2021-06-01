package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.CasePrivileged;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseAttachment;

import java.util.Collection;
import java.util.List;

/**
 * Service to get/save/remove attachments {@link ru.protei.portal.core.model.ent.Attachment}
 * of types {@link ru.protei.portal.core.model.dict.En_CaseType}
 */
public interface AttachmentService {

    /**
     * Удаляет вложение из таблиц
     * {@link Attachment},
     * {@link ru.protei.portal.core.model.ent.CaseAttachment}
     * и из облака
     * @return Идентификатор удаленного вложения
     */
    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAll = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAll = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAll = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.SF_PLATFORM, requireAll = {En_Privilege.SITE_FOLDER_VIEW, En_Privilege.SITE_FOLDER_EDIT}),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAny = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAny = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    @Auditable( En_AuditType.ATTACHMENT_REMOVE )
    Result<Long> removeAttachmentEverywhere( AuthToken token, En_CaseType caseType, Long attachmentId);

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAll = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAll = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAll = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.SF_PLATFORM, requireAll = {En_Privilege.SITE_FOLDER_VIEW, En_Privilege.SITE_FOLDER_EDIT}),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAny = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAny = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    @Auditable( En_AuditType.ATTACHMENT_REMOVE )
    Result<Long> removeAttachment( AuthToken token, En_CaseType caseType, Long id);

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAny = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAny = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAny = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAny = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.SF_PLATFORM, requireAny = {En_Privilege.SITE_FOLDER_VIEW, En_Privilege.SITE_FOLDER_EDIT}),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAny = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAny = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    Result<List<Attachment>> getAttachmentsByCaseId( AuthToken token, En_CaseType caseType, Long caseId);

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAny = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAny = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAny = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAny = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.SF_PLATFORM, requireAny = {En_Privilege.SITE_FOLDER_VIEW, En_Privilege.SITE_FOLDER_EDIT}),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAny = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAny = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    Result<List<Attachment>> getAttachments( AuthToken token, En_CaseType caseType, List<Long> ids);

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAny = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAny = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAny = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAny = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.SF_PLATFORM, requireAny = {En_Privilege.SITE_FOLDER_VIEW, En_Privilege.SITE_FOLDER_EDIT}),
            @CasePrivileged(caseType = En_CaseType.CONTRACT, requireAny = {En_Privilege.CONTRACT_VIEW, En_Privilege.CONTRACT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.DELIVERY, requireAny = {En_Privilege.DELIVERY_VIEW, En_Privilege.DELIVERY_EDIT})
    })
    Result<List<Attachment>> getAttachments( AuthToken token, En_CaseType caseType, Collection<CaseAttachment> caseAttachments);

    /**
     * Сохранение вложения
     */
    Result<Long> saveAttachment(Attachment attachment);

    Result<Attachment> getAttachmentByExtLink( String extLink);
}
