package ru.protei.portal.api.model;

/**
 * Created by turik on 06.09.16.
 */
public enum En_ErrorCode {

    EMPTY_COMP_CODE("WE-10001","Company's code is empty."),
    UNKNOWN_COMP("WE-10002", "Unknown company."),
    EMPTY_DEP_ID("WE-10003", "Department's identifier is empty."),
    UNKNOWN_DEP("WE-10004", "Unknown company's department."),
    EMPTY_POS("WE-10005", "Position is empty."),
    EMPTY_PER_ID("WE-10006","Person's identifier is empty."),
    UNKNOWN_PER("WE-10007", "Unknown person."),
    EMPTY_WOR_ID("WE-10008","Worker's identifier is empty."),
    UNKNOWN_WOR("WE-10009", "Unknown worker."),
    EMPTY_FIRST_NAME("WE-10010", "First name is empty."),
    EMPTY_LAST_NAME("WE-10011", "Last name is empty."),
    INV_FORMAT_IP("WE-10012", "Invalid format's ip-address."),
    EMPTY_DEP_NAME("WE-10013", "Department's name is empty."),
    EXIST_WOR("WE-10014", "Worker already exist."),
    UNKNOWN_PAR_DEP("WE-10015", "Unknown parent of department."),
    UNKNOWN_HEAD_DEP("WE-10016", "Unknown head of department."),
    EXIST_CHILD_DEP("WE-10017", "The department has child departments."),
    EXIST_DEP_WOR("WE-10018", "The department has workers."),
    NOT_CREATE("WE-10019", "Can not create."),
    NOT_UPDATE("WE-10020", "Can not update."),
    NOT_DELETE("WE-10021", "Can not delete."),
    EMPTY_PHOTO("WE-10022", "Photo is empty."),
    EMPTY_PHOTO_CONTENT("WE-10023", "Photo's content is empty.");

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
