package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.annotations.Auditable;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_AuditType;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.CaseAttachment;

import java.util.Collection;
import java.util.List;

/**
 * Created by bondarenko on 23.01.17.
 */
public interface AttachmentService {

    /**
     * Удаляет вложение из таблиц
     * {@link Attachment},
     * {@link ru.protei.portal.core.model.ent.CaseAttachment}
     * и из облака
     */
    @Privileged( En_Privilege.ISSUE_EDIT )
    @Auditable( En_AuditType.ATTACHMENT_REMOVE )
    CoreResponse<Boolean> removeAttachmentEverywhere( AuthToken token, Long attachmentId);

    @Privileged( En_Privilege.ISSUE_EDIT )
    @Auditable( En_AuditType.ATTACHMENT_REMOVE )
    CoreResponse<Boolean> removeAttachment( AuthToken token, Long id);

    @Privileged( En_Privilege.ISSUE_VIEW )
    CoreResponse<List<Attachment>> getAttachmentsByCaseId( AuthToken token, Long caseId);

    @Privileged( En_Privilege.ISSUE_VIEW )
    CoreResponse<List<Attachment>> getAttachments( AuthToken token, List<Long> ids);

    @Privileged( En_Privilege.ISSUE_VIEW )
    CoreResponse<List<Attachment>> getAttachments( AuthToken token, Collection<CaseAttachment> caseAttachments);

    CoreResponse<Long> saveAttachment(Attachment attachment);

}
