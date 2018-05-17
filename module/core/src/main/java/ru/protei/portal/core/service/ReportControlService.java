package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;

/**
 * Сервис автоматического контролирования и управления отчетами
 */
public interface ReportControlService {

    /**
     * Запрос на обработку отчетов
     *
     * @return результат выполнения операции
     */
    CoreResponse processNewReports();

    /**
     * Обработать старые отчеты
     *
     * @return результат выполнения операции
     */
    CoreResponse processOldReports();

    /**
     * Обработать подвисшие отчеты
     *
     * @return результат выполнения операции
     */
    CoreResponse processHangReports();
}
