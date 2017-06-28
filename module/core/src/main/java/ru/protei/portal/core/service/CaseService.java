package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.*;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.Date;
import java.util.List;
import java.util.Set;

/**
 * Сервис управления обращениями
 */
public interface CaseService {

    CoreResponse<Long> count( CaseQuery query, Set< UserRole > roles );
    CoreResponse<List<CaseShortView>> caseObjectList( CaseQuery query, Set< UserRole > roles );
    CoreResponse<CaseObject> getCaseObject( long id, Set< UserRole > roles );
    CoreResponse<CaseObject> saveCaseObject( CaseObject p, Person initiator, Set< UserRole > roles );
    CoreResponse<CaseObject> updateCaseObject( CaseObject p, Person currentPerson, Set< UserRole > roles );
    CoreResponse<List<En_CaseState>> stateList(En_CaseType caseType);
    CoreResponse<List<CaseComment>> getCaseCommentList( long caseId );
    CoreResponse<CaseComment> addCaseComment( CaseComment p, Person currentPerson, Set< UserRole > roles );
    CoreResponse<CaseComment> updateCaseComment( CaseComment p, Person person, Set< UserRole > roles );
    CoreResponse removeCaseComment( CaseComment caseComment, Long personId, Set< UserRole > roles );
    CoreResponse<Boolean> updateCaseModified(Long caseId, Date modified);
    CoreResponse<Long> bindAttachmentToCase(Attachment attachment, long caseId);
    boolean isExistsAttachments(Long caseId);
    CoreResponse<Boolean> updateExistsAttachmentsFlag(Long caseId, boolean flag);
    CoreResponse<Boolean> updateExistsAttachmentsFlag(Long caseId);
}
