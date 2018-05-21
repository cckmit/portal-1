package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.CoreResponse;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.model.struct.ReportContent;

import java.util.List;
import java.util.Set;

/**
 * Сервис управления отчетами
 */
public interface ReportService {

    /**
     * Запрос создания отчета
     *
     * @param authToken токен авторизации
     * @param report    информация об отчете
     * @return идентификатор отчета
     */
    CoreResponse<Long> createReport(AuthToken authToken, Report report);

    /**
     * Получение отчёта по идентификатору
     *
     * @param authToken токен авторизации
     * @param id        идентификатор отчета
     * @return отчёт
     */
    CoreResponse<Report> getReport(AuthToken authToken, Long id);

    /**
     * Получение информации об отчетах по фильтру
     *
     * @param authToken токен авторизации
     * @param query     фильтр для выборки отчетов
     * @return список отчетов
     */
    CoreResponse<List<Report>> getReportsByQuery(AuthToken authToken, ReportQuery query);

    /**
     * Получение файла отчета
     *
     * @param authToken токен авторизации
     * @param id        идентификатор отчета
     * @return файловый контент
     */
    CoreResponse<ReportContent> downloadReport(AuthToken authToken, Long id);

    /**
     * Запрос на удаление отчётов по идентификаторам
     *
     * @param authToken токен авторизации
     * @param include   набор идентификаторов отчётов, включаемых в удаление
     * @param exclude   набор идентификаторов отчётов, исключаемых из удаления
     */
    CoreResponse removeReports(AuthToken authToken, Set<Long> include, Set<Long> exclude);

    /**
     * Запрос на удаление отчётов по фильтру
     *
     * @param authToken токен авторизации
     * @param query     фильтр для удаления
     * @param exclude   набор идентификаторов отчётов, исключаемых из удаления
     */
    CoreResponse removeReports(AuthToken authToken, ReportQuery query, Set<Long> exclude);
}
