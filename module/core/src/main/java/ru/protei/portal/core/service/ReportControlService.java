package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;

/**
 * Сервис автоматического контролирования и управления отчетами
 */
public interface ReportControlService {

    /**
     * Запрос на обработку отчетов
     *
     * @return результат выполнения операции
     */
    Result processNewReports();

    /**
     * Обработать старые отчеты
     *
     * @return результат выполнения операции
     */
    Result processOldReports();

    /**
     * Обработать подвисшие отчеты
     *
     * @return результат выполнения операции
     */
    Result processHangReports();

    // Методы для автоматической обработки, контролирования и управления отчетами

    /** @hide */
    void processNewReportsSchedule();

    /** @hide */
    void processOldReportsSchedule();

    /** @hide */
    void processHangReportsSchedule();
}
