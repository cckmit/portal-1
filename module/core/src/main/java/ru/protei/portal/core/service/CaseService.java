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
import ru.protei.portal.core.model.struct.CaseNameAndDescriptionChangeRequest;
import ru.protei.portal.core.model.struct.CaseObjectMetaJira;
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

    Result<CaseObject> getCaseObjectById( AuthToken token, Long caseID );

    @Privileged({ En_Privilege.ISSUE_VIEW })
    Result<CaseObject> getCaseObjectByNumber( AuthToken token, long number );

    @Privileged({ En_Privilege.ISSUE_CREATE })
    @Auditable( En_AuditType.ISSUE_CREATE )
    Result<CaseObject> createCaseObject( AuthToken token, CaseObject p, Long initiatorId );

    @Deprecated
    @Privileged({ En_Privilege.ISSUE_EDIT })
    @Auditable( En_AuditType.ISSUE_MODIFY )
    Result<CaseObject> updateCaseObject( AuthToken token, CaseObject p, Long initiatorId );

    @Privileged({ En_Privilege.ISSUE_EDIT })
    @Auditable( En_AuditType.ISSUE_MODIFY )
    Result updateCaseObject(AuthToken token, CaseNameAndDescriptionChangeRequest changeRequest, Long initiatorId);

    @Privileged({ En_Privilege.ISSUE_EDIT })
    @Auditable( En_AuditType.ISSUE_MODIFY )
    Result<CaseObjectMeta> updateCaseObjectMeta( AuthToken token, CaseObjectMeta caseMeta, Long initiatorId );

    @Privileged({ En_Privilege.ISSUE_EDIT })
    @Auditable( En_AuditType.ISSUE_MODIFY )
    Result<CaseObjectMetaNotifiers> updateCaseObjectMetaNotifiers( AuthToken token, CaseObjectMetaNotifiers caseMetaNotifiers, Long initiatorId );

    @Privileged({ En_Privilege.ISSUE_EDIT })
    @Auditable( En_AuditType.ISSUE_MODIFY )
    Result< CaseObjectMetaJira > updateCaseObjectMetaJira( AuthToken token, CaseObjectMetaJira caseMetaJira, Long initiatorId );

    Result<List<En_CaseState>> stateList(En_CaseType caseType);

    Result<List<CaseState>> stateListWithViewOrder(En_CaseType caseType);

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

    Result<Boolean> isExistsAttachments(Long caseId);
    Result<Boolean> updateExistsAttachmentsFlag( Long caseId, boolean flag);
    Result<Boolean> updateExistsAttachmentsFlag( Long caseId);

    Result<Long> getAndIncrementEmailLastId( Long caseId );

    @Privileged({ En_Privilege.ISSUE_VIEW })
    Result<CaseInfo> getCaseShortInfo( AuthToken token, Long caseNumber);

    Result<List<CaseLink>> getCaseLinks( AuthToken token, Long caseId );

    Result<Long> getCaseIdByNumber( AuthToken token, Long caseNumber );
    Result<Long> getCaseNumberById( AuthToken token, Long caseId );
}
