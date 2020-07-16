package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.ent.Person;
import ru.protei.portal.core.model.query.AbsenceQuery;

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

    /**
     * Обработать отчеты по расписанию
     *
     * @return результат выполнения операции
     */
    Result<Void> processScheduledMailReports(En_ReportScheduledType enReportScheduledType);

    /**
     * Запустить формирование отчета по отсутствиям
     */
    Result<Void> processAbsenceReport(Person initiator, String title, AbsenceQuery query);
}
