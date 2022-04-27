package ru.protei.portal.core.model.dict;

/**
 * Типы отчетов
 */
public enum En_ReportType {

    /**
     * Отчет по задачам
     */
    CASE_OBJECTS,

    /**
     * Отчет по затраченному времени
     */
    CASE_TIME_ELAPSED,

    /**
     * Отчет по времени завершения
     */
    CASE_RESOLUTION_TIME,

    /**
     * Фильтр проектам
     */
    PROJECT,

    /**
     * Отчет по контрактам
     */
    CONTRACT,

    /**
     * Отчет по ночным работам
     */
    NIGHT_WORK,

    /**
     * Отчет по трудозатратам
     */
    YT_WORK,

    /**
     * Отчет по отправкам (пока из YT)
     */
    SEND
    ;

    public static boolean isTimeLimitMandatory(En_ReportType type) {
        if (type == null)
            return false;
        switch (type) {
            case CASE_TIME_ELAPSED:
            case CASE_RESOLUTION_TIME:
            case NIGHT_WORK:
            case YT_WORK:
                return true;
            default:
                return false;
        }
    }
}
