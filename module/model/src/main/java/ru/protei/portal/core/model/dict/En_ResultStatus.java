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
     * действие недоступно
     */
    NOT_AVAILABLE,

    /**
     * объект не определен
     */
    UNDEFINED_OBJECT,

    /**
     * объект уже существует
     */
    ALREADY_EXIST,

    /**
     * Уже существует связанный объект
     */
    ALREADY_EXIST_RELATED,

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
    DB_COMMON_ERROR,

    /**
     * ошибка прав доступа
     */
    PERMISSION_DENIED,

    /**
     * ошибка сессии
     */
    SESSION_NOT_FOUND,

    /**
     * уже существует такой инвентарный номер
     */
    INVENTORY_NUMBER_ALREADY_EXIST,

    /**
     * уже существует такой инвентарный номер
     */
    DECIMAL_NUMBER_ALREADY_EXIST,

    /**
     * запрещено создавать приватные сообщения
     */
    PROHIBITED_PRIVATE_COMMENT,

    /**
     * запрещено менять статус обращения с verified на не verified
     */
    INVALID_CASE_UPDATE_CASE_IS_CLOSED,

    /**
     * неверный текущий пароль при изменении пароля
     */
    INVALID_CURRENT_PASSWORD,

    AUTH_FAILURE,

    /**
     * Редактирование названия или описания чужих обращений запрещено
     */
    NOT_ALLOWED_CHANGE_ISSUE_NAME_OR_DESCRIPTION,

    /**
     * Изменение компании у площадки запрещено
     */
    NOT_ALLOWED_CHANGE_PLATFORM_COMPANY,

    /**
     * Изменение компании у проекта запрещено
     */
    NOT_ALLOWED_CHANGE_PROJECT_COMPANY,

    /**
     * Невозможно привязать задачу саму к себе
     */

    NOT_ALLOWED_LINK_ISSUE_TO_ITSELF,

    /**
     * Эта ссылка уже привязана
     */

    THIS_LINK_ALREADY_ADDED,

    /**
     * Удаление использующегося типа документа запрещено
     */
    NOT_ALLOWED_REMOVE_USED_DOCUMENT_TYPE,

    /**
     * Ошибка обновления или удаления связанного объекта
     */
    UPDATE_OR_REMOVE_LINKED_OBJECT_ERROR,

    /**
     * Ошибка при использовании SVN
     */
    SVN_ERROR,

    /**
     * Существует сотрудник уже привязанный к данному отделу
     */
    WORKER_WITH_THIS_DEPARTMENT_ALREADY_EXIST,

    /**
     * Существует сотрудник занимающий данную должность
     */
    WORKER_WITH_THIS_POSITION_ALREADY_EXIST,

    /**
     * Такой отдел уже существует
     */
    DEPARTMENT_ALREADY_EXIST,

    /**
     * Такая должность уже существует
     */
    POSITION_ALREADY_EXIST,

    /**
     * Такой сотрудник уже существует
     */
    EMPLOYEE_ALREADY_EXIST,

    /**
     * Нельзя уволить сотрудника, работающего в текущих компаниях
     */
    EMPLOYEE_NOT_FIRED_FROM_THESE_COMPANIES,

    /**
     * Подсеть не существует
     */
    SUBNET_DOES_NOT_EXIST,

    /**
     * Резервирование комнат: событие уже началось/закончилось
     */
    ROOM_RESERVATION_OUTDATED,

    /**
     * Резервирование комнат: доступ к комнате отклонен
     */
    ROOM_RESERVATION_ROOM_NOT_ACCESSIBLE,

    /**
     * Резервирование комнат: найдены пересечения с другими событиями
     */
    ROOM_RESERVATION_HAS_INTERSECTIONS,

    /**
     * Фильты обращений : фильтр используется
     */
    ISSUE_FILTER_IS_USED,

    /**
     * Найдено пересечение с другими отсутствиями
     */
    ABSENCE_HAS_INTERSECTIONS,

    /**
     * Отсутствие ещё не началось либо уже закончилось
     */
    NOT_CURRENT_ABSENCE
    ;
}
