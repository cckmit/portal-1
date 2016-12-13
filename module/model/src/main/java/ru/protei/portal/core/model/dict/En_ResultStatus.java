package ru.protei.portal.core.model.dict;

/**
 *  Статусы выполнения операций
 */
public enum En_ResultStatus {

    /**
     *  успешно
     */
    OK,

    /**
     * ошибка соединения
     */
    CONNECTION_ERROR,

    /**
     * внутренняя ошибка
     */
    INTERNAL_ERROR,

    /**
     * неправильный идентификатор сессии
     */
    INVALID_SESSION_ID,

    /**
     *  ошибка авторизации
     */
    INVALID_LOGIN_OR_PWD,

    /**
     * ошибка получения данных
     */
    GET_DATA_ERROR,

    /**
     * объект не найден
     */
    NOT_FOUND,

    /**
     *  объект не был создан
     */
    NOT_CREATED,

    /**
     * объект не был изменен
     */
    NOT_UPDATED,

    /**
     * объект не был удален
     */
    NOT_REMOVED,

    /**
     * объект не определен
     */
    UNDEFINED_OBJECT,

    /**
     * объект уже существует
     */
    ALREADY_EXIST,

    /**
     * ошибка валидации объекта
     */
    VALIDATION_ERROR,

    /**
     * некорректные параметры
     */
    INCORRECT_PARAMS,

    /**
     * Временная ошибка взаимодействия с базой данных
     */
    DB_TEMP_ERROR,

    /**
     * Ошибка взаимодействия с базой данных
     */
    DB_COMMON_ERROR
}
