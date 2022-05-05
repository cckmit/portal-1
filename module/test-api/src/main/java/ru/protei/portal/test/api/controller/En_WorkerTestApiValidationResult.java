package ru.protei.portal.test.api.controller;

public enum En_WorkerTestApiValidationResult {
    EMPTY_COMPANY_ID("The company id is empty."),
    EMPTY_DEPARTMENT_ID("The department id is empty."),
    EMPTY_POSITION_ID("The position id is empty"),
    EMPTY_FIRST_NAME("First name is empty."),
    EMPTY_LAST_NAME( "Last name is empty."),
    EMPTY_GENDER("Gender is empty"),
    EMPTY_BIRTHDAY("The birthday is empty."),
    EMPTY_PHONE("Phone is empty"),
    EMPTY_MAIL("Mail is empty"),
    EMPTY_IP("The ip is empty"),
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
    UNKNOWN_POSITION("Unknown position"),
    UNKNOWN_DEPARTMENT("Unknown department"),
    LOGIN_ALREADY_EXIST("Worker with this login already exist."),
    EMAIL_ALREADY_EXIST("Worker with this email already exist.");


    En_WorkerTestApiValidationResult(String message) {
        this.message = message;
    }

    private final String message;

    public String getMessage() {
        return message;
    }

}

