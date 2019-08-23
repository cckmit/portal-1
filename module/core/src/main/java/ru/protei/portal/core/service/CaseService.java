package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.CasePrivileged;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.struct.CaseObjectWithCaseComment;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;

/**
 * Сервис управления обращениями
 */
public interface CaseService {

    @Privileged({ En_Privilege.ISSUE_VIEW })
    Result<SearchResult<CaseShortView>> getCaseObjects( AuthToken token, CaseQuery query);

    @Privileged({ En_Privilege.ISSUE_VIEW })
    Result<CaseObject> getCaseObject( AuthToken token, long number );

    @Privileged({ En_Privilege.ISSUE_CREATE })
    @Auditable( En_AuditType.ISSUE_CREATE )
    Result<CaseObject> saveCaseObject( AuthToken token, CaseObject p, Person initiator );

    @Privileged({ En_Privilege.ISSUE_EDIT })
    @Auditable( En_AuditType.ISSUE_MODIFY )
    Result<CaseObject> updateCaseObject( AuthToken token, CaseObject p, Person initiator );

    @Privileged({ En_Privilege.ISSUE_EDIT })
    @Auditable( En_AuditType.ISSUE_MODIFY )
    Result<CaseObjectWithCaseComment> updateCaseObjectAndSaveComment( AuthToken token, CaseObject p, CaseComment c, Person initiator );

    Result<List<En_CaseState>> stateList( En_CaseType caseType);

    Result<Boolean> updateCaseModified( AuthToken token, Long caseId, Date modified);

    @Privileged(forCases = {
            @CasePrivileged(caseType = En_CaseType.CRM_SUPPORT, requireAll = {En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT}),
            @CasePrivileged(caseType = En_CaseType.OFFICIAL, requireAll = {En_Privilege.OFFICIAL_VIEW, En_Privilege.OFFICIAL_EDIT}),
            @CasePrivileged(caseType = En_CaseType.PROJECT, requireAll = {En_Privilege.PROJECT_VIEW, En_Privilege.PROJECT_EDIT}),
            @CasePrivileged(caseType = En_CaseType.EMPLOYEE_REGISTRATION, requireAll = En_Privilege.EMPLOYEE_REGISTRATION_VIEW),
            @CasePrivileged(caseType = En_CaseType.SF_PLATFORM, requireAll = {En_Privilege.SITE_FOLDER_VIEW, En_Privilege.SITE_FOLDER_EDIT})
    })
    Result<Long> bindAttachmentToCaseNumber( AuthToken token, En_CaseType caseType, Attachment attachment, long caseNumber);

    Result<Long> attachToCaseId( Attachment attachment, long caseId);

    boolean isExistsAttachments(Long caseId);
    Result<Boolean> updateExistsAttachmentsFlag( Long caseId, boolean flag);
    Result<Boolean> updateExistsAttachmentsFlag( Long caseId);

    Result<Long> getEmailLastId( Long caseId);
    Result<Boolean> updateEmailLastId( Long caseId, Long emailLastId);

    @Privileged({ En_Privilege.ISSUE_VIEW })
    Result<CaseInfo> getCaseShortInfo( AuthToken token, Long caseNumber);

    boolean hasAccessForCaseObject(AuthToken token, En_Privilege privilege, CaseObject caseObject);

    Result<List<CaseLink>> getCaseLinks( AuthToken token, Long caseId );
}
