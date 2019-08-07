package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_FileUploadStatus;

public class UploadResult {

    private En_FileUploadStatus status;
    private String details;

    public UploadResult() {
    }

    public UploadResult(En_FileUploadStatus status, String details) {
        this.status = status;
        this.details = details;
    }

    public En_FileUploadStatus getStatus() {
        return status;
    }

    public void setStatus(En_FileUploadStatus status) {
        this.status = status;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
