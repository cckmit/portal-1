package ru.protei.portal.ui.common.client.widget.uploader;

import ru.protei.portal.core.model.dict.En_CaseType;
import ru.protei.portal.ui.common.client.widget.uploader.impl.AttachmentUploader;
import ru.protei.portal.ui.common.client.widget.uploader.impl.PasteInfo;

import java.util.List;

public interface AbstractAttachmentUploader {
    void uploadBase64File(String json, PasteInfo pasteInfo);
    void uploadBase64Files(List<String> jsons, PasteInfo pasteInfo);
    void setUploadHandler(AttachmentUploader.FileUploadHandler fileUploadHandler);
    void autoBindingToCase(En_CaseType caseType, Long caseNumber);
}
