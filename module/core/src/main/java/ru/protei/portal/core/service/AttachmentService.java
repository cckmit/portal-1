package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.Attachment;

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
    CoreResponse<Boolean> removeAttachmentEverywhere(Long attachmentId);

    CoreResponse<List<Attachment>> getAttachmentsByCaseId(Long caseId);

    CoreResponse<List<Attachment>> getAttachments(List<Long> ids);

}
