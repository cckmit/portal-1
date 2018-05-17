package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import java.util.List;

public interface ReportDAO extends PortalBaseDAO<Report> {

    /**
     * Получить информацию об отчетах по фильтру
     *
     * @param creatorId идентификатор профиля, который является создателем отчетов
     * @param query     фильтр для выборки отчетов
     * @return список отчетов
     */
    List<Report> getReportsByQuery(Long creatorId, ReportQuery query);

    /**
     * Получить отчеты, которые следует обработать
     *
     * @param limit лимит на количество отчетов для обработки
     * @return список отчетов
     */
    List<Report> getReportsToProcess(int limit);

    @SqlConditionBuilder
    SqlCondition createSqlCondition(Long creatorId, ReportQuery query);
}
