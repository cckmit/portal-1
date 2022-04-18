package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.Attachment;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

/**
 * Created by bondarenko on 12.01.17.
 */

@RemoteServiceRelativePath( "springGwtServices/AttachmentController" )
public interface AttachmentController extends RemoteService {

    List<Attachment> getAttachmentsByCaseId(En_CaseType caseType, Long caseId) throws RequestFailedException;

    List<Attachment> getAttachments(En_CaseType caseType, List<Long> attachmentIds) throws RequestFailedException;

    /**
     * Удаляет вложения из таблиц
     * {@link Attachment},
     * {@link ru.protei.portal.core.model.ent.CaseAttachment}
     * и из облака
     * @return Идентификатор удаленного вложения
     */
    Long removeAttachmentEverywhere(En_CaseType caseType, Long caseId, Long attachmentId) throws RequestFailedException;

    /**
     * Очистка кэша загрузки
     */
    void clearUploadedAttachmentsCache();

    Long addCaseAttachmentHistory(Long caseObjectId, Long attachmentId, String fileName) throws RequestFailedException;
}
