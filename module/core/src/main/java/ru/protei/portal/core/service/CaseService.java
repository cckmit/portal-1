package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.Date;
import java.util.List;

/**
 * Сервис управления обращениями
 */
public interface CaseService {

    @Privileged({ En_Privilege.ISSUE_VIEW })
    CoreResponse<Long> count( AuthToken token, CaseQuery query );

    @Privileged({ En_Privilege.ISSUE_VIEW })
    CoreResponse<List<CaseShortView>> caseObjectList( AuthToken token, CaseQuery query );

    @Privileged({ En_Privilege.ISSUE_VIEW })
    CoreResponse<CaseObject> getCaseObject( AuthToken token, long number );

    @Privileged({ En_Privilege.ISSUE_CREATE })
    @Auditable( En_AuditType.ISSUE_CREATE )
    CoreResponse<CaseObject> saveCaseObject( AuthToken token, CaseObject p, Person initiator );

    @Privileged({ En_Privilege.ISSUE_CREATE })
    @Auditable( En_AuditType.ISSUE_MODIFY )
    CoreResponse<CaseObject> updateCaseObject( AuthToken token, CaseObject p );

    @Auditable( En_AuditType.ISSUE_MODIFY )
    CoreResponse<CaseObject> updateCaseObject( CaseObject p, Person initiator );

    CoreResponse<List<En_CaseState>> stateList(En_CaseType caseType);

    @Privileged( requireAny = { En_Privilege.ISSUE_VIEW, En_Privilege.ISSUE_EDIT })
    CoreResponse<List<CaseComment>> getCaseCommentList( AuthToken token, long caseId );

    @Privileged({ En_Privilege.ISSUE_EDIT, En_Privilege.ISSUE_VIEW })
    @Auditable( En_AuditType.ISSUE_COMMENT_CREATE )
    CoreResponse<CaseComment> addCaseComment( AuthToken token, CaseComment p, Person currentPerson );

    @Privileged({ En_Privilege.ISSUE_EDIT, En_Privilege.ISSUE_VIEW })
    @Auditable( En_AuditType.ISSUE_COMMENT_MODIFY )
    CoreResponse<CaseComment> updateCaseComment( AuthToken token, CaseComment p, Person person );

    @Privileged({ En_Privilege.ISSUE_EDIT, En_Privilege.ISSUE_VIEW })
    @Auditable( En_AuditType.ISSUE_COMMENT_REMOVE )
    CoreResponse removeCaseComment( AuthToken token, CaseComment caseComment, Long personId );

    @Privileged( En_Privilege.ISSUE_EDIT )
    CoreResponse<Boolean> updateCaseModified( AuthToken token, Long caseId, Date modified);

    @Privileged( En_Privilege.ISSUE_EDIT )
    CoreResponse<Long> bindAttachmentToCaseNumber(AuthToken token, Attachment attachment, long caseNumber);

    CoreResponse<Long> attachToCaseId(Attachment attachment, long caseId);

    boolean isExistsAttachments(Long caseId);
    CoreResponse<Boolean> updateExistsAttachmentsFlag(Long caseId, boolean flag);
    CoreResponse<Boolean> updateExistsAttachmentsFlag(Long caseId);

    CoreResponse<Long> getEmailLastId(Long caseId);
    CoreResponse<Boolean> updateEmailLastId(CaseObject caseObject);

    @Privileged({ En_Privilege.ISSUE_VIEW })
    CoreResponse<CaseInfo> getCaseShortInfo(AuthToken token, Long caseNumber);
}
