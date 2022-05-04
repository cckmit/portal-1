package ru.protei.portal.test.api;

public enum En_WorkerRecordTestApiValidationResult {

    OK("Ok"),
    EMPTY_COMPANY_ID("The company id is empty."),
    EMPTY_DEPARTMENT_ID("The department id is empty."),
    EMPTY_POSITION_ID("The position id is empty"),
    EMPTY_FIRST_NAME("First name is empty."),
    EMPTY_LAST_NAME( "Last name is empty."),
    EMPTY_GENDER("Gender is empty"),
    EMPTY_BIRTHDAY("The birthday is empty."),
    EMPTY_PHONE("Phone is empty"),
    EMPTY_MAIL("Mail is empty"),
    EMPTY_IP("The is is empty"),
    EMPTY_CONTRACT_AGREEMENT("Contract agreement is empty"),
    EMPTY_INN("Inn is empty"),
    EMPTY_LOCALE("Locale is empty"),
    EMPTY_IS_FIRED("IsFired is empty"),
    EMPTY_LOGIN("Login is empty"),
    EMPTY_PASSWORD("Password is empty"),
    EMPTY_ROLE_IDS("Roles ids is empty"),
    INVALID_FORMAT_IP("Invalid ip-address format"),
    INVALID_FORMAT_INN("Invalid inn format"),
    INVALID_FORMAT_LOCALE("Invalid locale format"),
    NOT_EXIST_POSITION_ID("Position with this id does not exist"),
    NOT_EXIST_DEPARTMENT_ID("Department with this id does not exist");

    En_WorkerRecordTestApiValidationResult(String message) {
        this.message = message;
    }

    private final String message;

    public String getMessage() {
        return message;
    }

}

