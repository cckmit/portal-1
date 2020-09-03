package ru.protei.portal.core.model.dict;

import java.util.Objects;

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

    ;

    public static boolean isTimeLimitMandatory(En_ReportType type) {
        if (type == null)
            return false;
        return Objects.equals(type, En_ReportType.CASE_TIME_ELAPSED)
                || Objects.equals(type, En_ReportType.CASE_RESOLUTION_TIME);
    }
}
