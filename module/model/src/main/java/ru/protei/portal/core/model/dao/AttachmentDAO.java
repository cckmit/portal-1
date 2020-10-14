package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.Attachment;

import java.util.List;

/**
 * Created by bondarenko on 26.01.17.
 */
public interface AttachmentDAO extends PortalBaseDAO<Attachment>{
    List<Attachment> getAttachmentsByCaseId(Long caseId);

    List<Attachment> getPublicAttachmentsByCaseId(Long caseId);

    boolean hasPublicAttachments(Long caseId);

    List<Attachment> getPublicAttachmentsByIds(List<Long> ids);
}
