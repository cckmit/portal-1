package ru.protei.portal.core.model.dict;

public enum En_FileUploadStatus {

    OK,

    SERVER_ERROR,

    PARSE_ERROR,

    SIZE_EXCEED_ERROR;

    public static En_FileUploadStatus getStatus (String status){
        try {
            return En_FileUploadStatus.valueOf(status);
        }catch (IllegalArgumentException | NullPointerException e) {
            return null;
        }
    }
}
