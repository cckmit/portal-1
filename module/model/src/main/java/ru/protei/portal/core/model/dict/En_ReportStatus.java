package ru.protei.portal.core.model.dict;

public enum En_ReportStatus {

    /**
     * отчет запрошен, но не обработан
     */
    CREATED,

    /**
     * отчет запрошен и обрабатывается
     */
    PROCESS,

    /**
     * отчет готов
     */
    READY,

    /**
     * отчет не готов, произошла ошибка
     */
    ERROR
}
