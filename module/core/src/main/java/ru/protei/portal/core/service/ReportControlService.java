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
    Result<Void> processNewReports();

    /**
     * Обработать старые отчеты
     *
     * @return результат выполнения операции
     */
    Result<Void> processOldReports();

    /**
     * Обработать подвисшие отчеты
     *
     * @return результат выполнения операции
     */
    Result<Void> processHangReports();

}
