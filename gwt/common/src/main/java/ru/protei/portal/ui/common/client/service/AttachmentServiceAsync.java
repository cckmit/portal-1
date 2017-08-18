package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.Attachment;

import java.util.List;

public interface AttachmentServiceAsync {

    void getAttachmentsByCaseId(Long caseId, AsyncCallback<List<Attachment>> async);

    void getAttachments(List<Long> attachmentIds, AsyncCallback<List<Attachment>> async);

    /**
     * Удаляет вложение из таблиц
     * {@link Attachment},
     * {@link ru.protei.portal.core.model.ent.CaseAttachment}
     * и из облака
     */
    void removeAttachmentEverywhere(Long attachmentId, AsyncCallback<Boolean> async);

}
