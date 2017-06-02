package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.CaseComment;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.view.CaseShortView;

import java.util.Date;
import java.util.List;

/**
 * Сервис управления обращениями
 */
public interface CaseService {

    CoreResponse<Long> count( CaseQuery query );
    CoreResponse<List<CaseShortView>> caseObjectList( CaseQuery query);
    CoreResponse<CaseObject> getCaseObject(long id);
    CoreResponse<CaseObject> saveCaseObject( CaseObject p, Person initiator );
    CoreResponse<CaseObject> updateCaseObject( CaseObject p, Person currentPerson );
    CoreResponse<List<En_CaseState>> stateList(En_CaseType caseType);
    CoreResponse<List<CaseComment>> getCaseCommentList( long caseId );
    CoreResponse<CaseComment> addCaseComment( CaseComment p, Person currentPerson );
    CoreResponse<CaseComment> updateCaseComment(CaseComment p, Person person );
    CoreResponse removeCaseComment( CaseComment caseComment, Long personId );
    CoreResponse<Boolean> updateCaseModified(Long caseId, Date modified);
    CoreResponse<Long> bindAttachmentToCase(Attachment attachment, long caseId);
    boolean isExistsAttachments(Long caseId);
    CoreResponse<Boolean> updateExistsAttachmentsFlag(Long caseId, boolean flag);
    CoreResponse<Boolean> updateExistsAttachmentsFlag(Long caseId);
}
