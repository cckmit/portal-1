package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.beans.SearchResult;

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

    List<Report> getReports(ReportQuery query);

    /**
     * Получить информацию об отчетах по фильтру
     *
     * @param creatorId  идентификатор профиля, который является создателем отчетов
     * @param includeIds выбирать указанные идентификаторы
     * @param excludeIds не выбирать указанные идентификаторы
     * @param systemId идентификатор системы
     * @return список отчетов
     */
    List<Report> getReportsByIds(Long creatorId, Set<Long> includeIds, Set<Long> excludeIds, String systemId);

    /**
     * Получить отчеты для запланированной рассылки
     *
     * @return список отчетов
     */
    List<Report> getScheduledReports(En_ReportScheduledType enReportScheduledType, String systemId);

    @SqlConditionBuilder
    SqlCondition createSqlCondition(ReportQuery query);
}
