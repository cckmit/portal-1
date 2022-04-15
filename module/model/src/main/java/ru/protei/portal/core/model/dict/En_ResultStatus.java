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
     *  учетная запись заблокирована
     */
    ACCOUNT_IS_LOCKED,

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
     * Отменено
     */
    CANCELED,

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
     * отрудник с таким email уже существует
     */
    EMPLOYEE_EMAIL_ALREADY_EXIST,

    /**
     * Ошибка миграции сотрудника на старый портал
     */
    EMPLOYEE_MIGRATION_FAILED,

    /**
     * Логин уже существует
     */
    LOGIN_ALREADY_EXIST,

    /**
     * Подсеть не существует
     */
    SUBNET_DOES_NOT_EXIST,

    /**
     * Подсеть недоступна для резервирования
     */
    SUBNET_NOT_ALLOWED_FOR_RESERVE,

    /**
     * Резервирование комнат: событие уже закончилось
     */
    ROOM_RESERVATION_FINISHED,

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
     * Ошибка при запросе в 1С
     */
    REQUEST_1C_FAILED,

    /**
     * Не найдено при запросе в 1С
     */
    REQUEST_1C_NOT_FOUND,

    /**
     * Найдено пересечение с другими отсутствиями
     */
    ABSENCE_HAS_INTERSECTIONS,

    /**
     * Отсутствие ещё не началось либо уже закончилось
     */
    NOT_CURRENT_ABSENCE,
    /**
     * Ошибка синхронизации с youtrack
     */
    YOUTRACK_SYNCHRONIZATION_FAILED,

    /**
     * Проект не выбран
     */
    PROJECT_NOT_SELECTED,

    /**
     * Нельзя удалить контрагента, который выставлен в договорах
     */
    CONTRACTOR_NOT_REMOVED_HAS_CONTRACTS,

    /**
     * Запрещено редактирование комментария по истечению времени
     */
    NOT_ALLOWED_EDIT_COMMENT_BY_TIME,

    /**
     * Запрещено удаление комментария по истечению времени
     */
    NOT_ALLOWED_REMOVE_COMMENT_BY_TIME,

    /**
     * Сервис отчётов не настроен
     */
    REPORTING_SERVICE_NOT_CONFIGURED,

    /**
     * NRPE. IP адрес занят
     */
    NRPE_IP_NON_AVAILABLE,

    /**
     * NRPE. Ошибка сервиса
     */
    NRPE_ERROR,

    /**
     *  NRPE. Недостаточно свободных IP адресов
     */
    NRPE_NO_FREE_IPS,

    /**
     *  NRPE. Сервис не настроен
     */
    NRPE_NOT_CONFIGURED,

    /**
     * Пользователь не найдем
     */
    USER_NOT_FOUND,

    /**
     * Родительский объект не найден
     */
    NOT_FOUND_PARENT,

    /**
     * Запрещено менять статус обращения на verified, когда не все подзадачи в verified
     */
    INVALID_CASE_UPDATE_SUBTASK_NOT_CLOSED,

    /**
     * Родительская задача не может быть в статусе created или verified
     */
    NOT_ALLOWED_PARENT_STATE,

    /**
     * Родительская задача или подзадача не может быть с автоматическим открытием
     */
    NOT_ALLOWED_AUTOOPEN_ISSUE,

    /**
     * Родительская задача не может быть интеграционной
     */
    NOT_ALLOWED_INTEGRATION_ISSUE,

    /**
     * Данные превышают размерность, заданную в MySQL
     */
    MYSQL_DATA_TRUNCATION,

    /**
     * Неверный формат файла
     */
    INVALID_FILE_FORMAT,

    /**
     * Поставки, серийный номер занят
     */
    NOT_AVAILABLE_DELIVERY_KIT_SERIAL_NUMBER,

    /**
     * Модули, серийный номер занят
     */
    NOT_AVAILABLE_MODULE_SERIAL_NUMBER,

    /**
     * Поставки, серийный номер комплекта не соответствует номеру поставки
     */
    KIT_SERIAL_NUMBER_NOT_MATCH_DELIVERY_NUMBER,

    /**
     * Поставки, нет прав менять статус
     */
    DELIVERY_FORBIDDEN_CHANGE_STATUS,

    /**
     * Поставки, запрещено менять проект поставки
     */
    DELIVERY_FORBIDDEN_CHANGE_PROJECT,

    /**
     * Есть пересечение в расписании
     */
    SCHEDULE_HAS_OVERLAP,

    /**
     * Некорректный период времени (расписание)
     */
    SCHEDULE_INCORRECT_TIME_RANGE,

    /**
     * Нужно заполнить дни (расписание)
     */
    SCHEDULE_NEED_FEEL_DAYS,

    /**
     * Нужно заполнить время (расписание)
     */
    SCHEDULE_NEED_FEEL_TIME_RANGES,

    /**
     *  Партии плат, в партии есть платы
     */
    CARD_BATCH_HAS_CARD,

    /**
     *  Сотрудник не синхронизируется с 1С (worker_extId == null)
     */
    EMPLOYEE_NOT_SYNCHRONIZING_WITH_1C
    ;
}
