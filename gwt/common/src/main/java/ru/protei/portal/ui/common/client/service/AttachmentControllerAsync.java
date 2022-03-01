package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.core.model.ent.Attachment;

import java.util.List;

public interface AttachmentControllerAsync {

    void getAttachmentsByCaseId(En_CaseType caseType, Long caseId, AsyncCallback<List<Attachment>> async);

    void getAttachments(En_CaseType caseType, List<Long> attachmentIds, AsyncCallback<List<Attachment>> async);

    /**
     * Удаляет вложение из таблиц
     * {@link Attachment},
     * {@link ru.protei.portal.core.model.ent.CaseAttachment}
     * и из облака
     */
    void removeAttachmentEverywhere(En_CaseType caseType, Long caseId, Long attachmentId, AsyncCallback<Long> async);

    /**
     * Очистка кэша загрузки
     * @param async
     */
    void clearUploadedAttachmentsCache(AsyncCallback<Void> async);

    void addCaseAttachmentHistory(Long caseObjectId, Long attachmentId, String fileName, AsyncCallback<Long> async);
}
