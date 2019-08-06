package ru.protei.portal.core.model.dict;

public enum En_FileUploadError {

    INNER,

    SIZE_EXCEED;

    public static En_FileUploadError getError (String error){
        try {
            return En_FileUploadError.valueOf(error);
        }catch (IllegalArgumentException | NullPointerException e) {
            return INNER;
        }
    }
}
