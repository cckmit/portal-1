package ru.protei.portal.core.model.dict;

/**
 *  Статусы выполнения операций
 */
public enum En_ResultStatus {
    OK,
    CONNECTION_ERROR,
    INTERNAL_ERROR,
    INVALID_SESSION_ID,
    INVALID_LOGIN_OR_PWD,
    GET_DATA_ERROR,
    NOT_FOUND,
    NOT_CREATED,
    NOT_UPDATED,
    UNDEFINED_OBJECT,
    ALREADY_EXIST,
    VALIDATION_ERROR,
    INCORRECT_PARAMS
}
