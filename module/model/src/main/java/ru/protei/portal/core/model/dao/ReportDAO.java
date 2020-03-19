package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.Date;
import java.util.List;
import java.util.Set;

public interface ReportDAO extends PortalBaseDAO<Report> {

    /**
     * Получить отчет по идентификатору
     *
     * @param creatorId идентификатор профиля, который является создателем отчета
     * @param reportId  идентификатор отчета
     * @return отчет
     */
    Report getReport(Long creatorId, Long reportId);

    /**
     * Получить {@code SearchResult} по фильтру
     *
     * @param creatorId  идентификатор профиля, который является создателем отчетов
     * @param query      фильтр для выборки отчетов
     * @param excludeIds не выбирать указанные идентификаторы
     * @return список отчетов
     */
    SearchResult<Report> getSearchResult(Long creatorId, ReportQuery query, Set<Long> excludeIds);

    /**
     * Получить информацию об отчетах по фильтру
     *
     * @param creatorId  идентификатор профиля, который является создателем отчетов
     * @param includeIds выбирать указанные идентификаторы
     * @param excludeIds не выбирать указанные идентификаторы
     * @return список отчетов
     */
    List<Report> getReportsByIds(Long creatorId, Set<Long> includeIds, Set<Long> excludeIds);

    /**
     * Получить отчеты с указанным статусом
     *
     * @param statuses статусы отчетов
     * @param limit    лимит на количество отчетов для обработки
     * @return список отчетов
     */
    List<Report> getReportsByStatuses(List<En_ReportStatus> statuses, int limit);

    /**
     * Получить отчеты с указанным статусом
     *
     * @param statuses           статусы отчетов
     * @param lastModifiedBefore дата до которой были последние изменения
     * @return список отчетов
     */
    List<Report> getReportsByStatuses(List<En_ReportStatus> statuses, Date lastModifiedBefore, List<En_ReportScheduledType> scheduledTypes);

    /**
     * Получить отчеты для запланированной рассылки
     *
     * @return список отчетов
     */
    List<Report> getScheduledReports(En_ReportScheduledType enReportScheduledType);

    @SqlConditionBuilder
    SqlCondition createSqlCondition(ReportQuery query);
}
