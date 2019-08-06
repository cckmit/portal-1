package ru.protei.portal.core.model.struct;

import ru.protei.portal.core.model.dict.En_FileUploadError;

public class UploadResult {

    private En_FileUploadError error;
    private String details;

    public UploadResult() {
    }

    public UploadResult(En_FileUploadError error, String details) {
        this.error = error;
        this.details = details;
    }

    public En_FileUploadError getError() {
        return error;
    }

    public void setError(En_FileUploadError error) {
        this.error = error;
    }

    public String getDetails() {
        return details;
    }

    public void setDetails(String details) {
        this.details = details;
    }
}
