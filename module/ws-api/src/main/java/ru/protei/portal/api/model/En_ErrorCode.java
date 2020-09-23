package ru.protei.portal.api.model;

/**
 * Created by turik on 06.09.16.
 */
public enum En_ErrorCode {

    EMPTY_COMP_CODE("The company code is empty."),
    UNKNOWN_COMP( "Unknown company."),
    EMPTY_DEP_ID("The department code is empty."),
    UNKNOWN_DEP("Unknown department of the company."),
    EMPTY_POS("Position is empty."),
    EMPTY_PER_ID("The person identifier is empty."),
    UNKNOWN_PER("Unknown person."),
    EMPTY_WOR_ID("The worker code is empty."),
    UNKNOWN_WOR("Unknown worker."),
    EMPTY_FIRST_NAME("First name is empty."),
    EMPTY_LAST_NAME( "Last name is empty."),
    INV_FORMAT_IP("Invalid ip-address format."),
    EMPTY_DEP_NAME("The department name is empty."),
    EXIST_WOR("Worker already exist."),
    UNKNOWN_PAR_DEP("Unknown parent of department."),
    UNKNOWN_HEAD_DEP("Unknown head of department."),
    EXIST_CHILD_DEP("The department has child departments."),
    EXIST_DEP_WOR("The department has workers."),
    NOT_CREATE("Can not create."),
    NOT_UPDATE("Can not update."),
    NOT_DELETE("Can not delete."),
    EMPTY_PHOTO("Photo is empty."),
    EMPTY_PHOTO_CONTENT("The content of the photo is empty."),
    UNKNOWN_POS("Unknown position."),
    EXIST_POS_WOR("Exists workers with this position."),
    EXIST_POS("Position already exist."),
    INV_FORMAT_DEP_CODE("Invalid department code."),
    INV_FORMAT_WOR_CODE("Invalid worker code."),
    DELETED_OR_FIRED_RECORD("Deleted or fired record."),
    EMPTY_BIRTHDAY("The birthday is empty."),
    EMPTY_FIRE_DATE("The fire date is empty."),
    LOGIN_ALREADY_EXIST("Worker with this login already exist."),
    UNKNOWN_REG("Unknown EmployeeRegistration.");

    En_ErrorCode (String message) {
        this.message = message;
    }

    private final String message;

    public String getMessage() {
        return message;
    }
}
