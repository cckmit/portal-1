package ru.protei.portal.core.model.dict;

public enum En_FileUploadError {

    OTHER_ERROR(1),
    TOO_BIG(2);


    private En_FileUploadError(int id) {
        this.id = id;
    }

    private int id;

    public int getId() {
        return id;
    }


    public static En_FileUploadError forId (int index) {
        return index == TOO_BIG.getId() ? TOO_BIG : OTHER_ERROR;
    }

    public static boolean isContained (String error){
        try {
            En_FileUploadError.valueOf(error);
            return true;
        } catch (IllegalArgumentException | NullPointerException e) {
           return false;
        }
    }

    public static En_FileUploadError getError (String error){
        if (isContained(error)){
            return En_FileUploadError.valueOf(error);
        }
        else return OTHER_ERROR;
    }
}
