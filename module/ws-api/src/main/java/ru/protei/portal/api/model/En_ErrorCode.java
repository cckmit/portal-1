package ru.protei.portal.api.model;

/**
 * Created by turik on 06.09.16.
 */
public enum En_ErrorCode {

    EMPTY_COMP_CODE("WE-10001","The company code is empty."),
    UNKNOWN_COMP("WE-10002", "Unknown company."),
    EMPTY_DEP_ID("WE-10003", "The department code is empty."),
    UNKNOWN_DEP("WE-10004", "Unknown department of the company."),
    EMPTY_POS("WE-10005", "Position is empty."),
    EMPTY_PER_ID("WE-10006","The person identifier is empty."),
    UNKNOWN_PER("WE-10007", "Unknown person."),
    EMPTY_WOR_ID("WE-10008","The worker code is empty."),
    UNKNOWN_WOR("WE-10009", "Unknown worker."),
    EMPTY_FIRST_NAME("WE-10010", "First name is empty."),
    EMPTY_LAST_NAME("WE-10011", "Last name is empty."),
    INV_FORMAT_IP("WE-10012", "Invalid ip-address format."),
    EMPTY_DEP_NAME("WE-10013", "The department name is empty."),
    EXIST_WOR("WE-10014", "Worker already exist."),
    UNKNOWN_PAR_DEP("WE-10015", "Unknown parent of department."),
    UNKNOWN_HEAD_DEP("WE-10016", "Unknown head of department."),
    EXIST_CHILD_DEP("WE-10017", "The department has child departments."),
    EXIST_DEP_WOR("WE-10018", "The department has workers."),
    NOT_CREATE("WE-10019", "Can not create."),
    NOT_UPDATE("WE-10020", "Can not update."),
    NOT_DELETE("WE-10021", "Can not delete."),
    EMPTY_PHOTO("WE-10022", "Photo is empty."),
    EMPTY_PHOTO_CONTENT("WE-10023", "The content of the photo is empty."),
    UNKNOWN_POS("WE-10024", "Unknown position."),
    EXIST_POS_WOR("WE-10025", "Exists workers with this position."),
    EXIST_POS("WE-10026", "Position already exist."),
    INV_FORMAT_DEP_CODE("WE-10027", "Invalid department code."),
    INV_FORMAT_WOR_CODE("WE-10028", "Invalid worker code."),
    DELETED_OR_FIRED_RECORD("WE-10029", "Deleted or fired record.");


    En_ErrorCode (String code, String message) {
        this.code = code;
        this.message = message;
    }

    private final String code;
    private final String message;

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }
}
