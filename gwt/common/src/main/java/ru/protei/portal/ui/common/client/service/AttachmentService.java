package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Created by bondarenko on 12.01.17.
 */

@RemoteServiceRelativePath( "springGwtServices/AttachmentService" )
public interface AttachmentService extends RemoteService {

    List<Attachment> getAttachmentsByCaseId(Long caseId) throws RequestFailedException;

    List<Attachment> getAttachmentsByCommentId(Long caseId);

    List<Attachment> getAttachments(List<Long> attachmentIds) throws RequestFailedException;

    /**
     * Удаляет вложения из таблиц
     * {@link Attachment},
     * {@link ru.protei.portal.core.model.ent.CaseAttachment}
     * и из облака
     */
    boolean removeAttachmentEverywhere(Long attachmentId) throws RequestFailedException;

    boolean bindAttachmentToCase(List<Attachment> attachments, Long caseId);

    /**
     * Удаляет вложение из таблиц
     * {@link Attachment},
     * {@link ru.protei.portal.core.model.ent.CaseAttachment}
     * и из облака
     */
}
