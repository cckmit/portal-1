package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseAttachment;

import java.util.Collection;
import java.util.List;

/**
 * Service to get/save/remove attachments {@link ru.protei.portal.core.model.ent.Attachment}
 * of types {@link ru.protei.portal.core.model.dict.En_CaseType}
 * Privileges are checked manually
 */
public interface AttachmentService {

    /**
     * Удаляет вложение из таблиц
     * {@link Attachment},
     * {@link ru.protei.portal.core.model.ent.CaseAttachment}
     * и из облака
     */
    @Auditable( En_AuditType.ATTACHMENT_REMOVE )
    CoreResponse<Boolean> removeAttachmentEverywhere(AuthToken token, En_CaseType caseType, Long attachmentId);

    @Auditable( En_AuditType.ATTACHMENT_REMOVE )
    CoreResponse<Boolean> removeAttachment(AuthToken token, En_CaseType caseType, Long id);

    CoreResponse<List<Attachment>> getAttachmentsByCaseId(AuthToken token, En_CaseType caseType, Long caseId);

    CoreResponse<List<Attachment>> getAttachments(AuthToken token, En_CaseType caseType, List<Long> ids);

    CoreResponse<List<Attachment>> getAttachments(AuthToken token, En_CaseType caseType, Collection<CaseAttachment> caseAttachments);

    CoreResponse<Long> saveAttachment(Attachment attachment);
}
