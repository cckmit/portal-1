package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_ReportScheduledType;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcHelper;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Set;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;

public class ReportDAO_Impl extends PortalBaseJdbcDAO<Report> implements ReportDAO {

    @Override
    public Report getReport(Long creatorId, Long reportId) {
        Condition condition = condition()
                .and(getTableName() + ".is_removed").equal(false)
                .and(getTableName() + ".creator").equal(creatorId)
                .and(getTableName() + ".id").equal(reportId);

        return getByCondition(condition.getSqlCondition(), condition.getSqlParameters());
    }

    @Override
    public SearchResult<Report> getSearchResult(Long creatorId, ReportQuery query, Set<Long> excludeIds) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query, creatorId, excludeIds);
        return getSearchResult(parameters);
    }

    @Override
    public List<Report> getReportsByIds(Long creatorId, Set<Long> includeIds, Set<Long> excludeIds) {
        ReportQuery query = new ReportQuery();
        query.setCreatorId(creatorId);
        query.setIncludeIds(includeIds);
        query.setExcludeIds(excludeIds);
        SqlCondition where = createSqlCondition(query);
        return getList(new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
        );
    }

    @Override
    public List<Report> getReportsByStatuses(List<En_ReportStatus> statuses, int limit) {
        ReportQuery query = new ReportQuery();
        query.setStatuses(statuses);
        query.setLimit(limit);
        SqlCondition where = createSqlCondition(query);
        return getList(new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
                .withLimit(query.getLimit())
        );
    }

    @Override
    public List<Report> getReportsByStatuses(List<En_ReportStatus> statuses, Date lastModifiedBefore, List<En_ReportScheduledType> scheduledTypes) {
        ReportQuery query = new ReportQuery();
        query.setStatuses(statuses);
        query.setToModified(lastModifiedBefore);
        query.setScheduledTypes(scheduledTypes);
        SqlCondition where = createSqlCondition(query);
        return getList(new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
        );
    }

    @Override
    public List<Report> getScheduledReports(En_ReportScheduledType enReportScheduledType) {
        ReportQuery query = new ReportQuery();
        query.setScheduledTypes(Arrays.asList(enReportScheduledType));
        SqlCondition where = createSqlCondition(query);
        return getList(new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
        );
    }

    private JdbcQueryParameters buildJdbcQueryParameters(ReportQuery query, Long creatorId, Set<Long> excludeIds) {
        query.setCreatorId(creatorId);
        query.setExcludeIds(excludeIds);
        SqlCondition where = createSqlCondition(query);
        return new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
                .withOffset(query.getOffset())
                .withLimit(query.getLimit())
                .withSort(TypeConverters.createSort(query));
    }

    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(ReportQuery query) {
        return new SqlCondition().build((condition, args) -> {

            condition.append("1=1");

            if (query == null) {
                return;
            }

            if (query.getCreatorId() != null) {
                condition.append(" and report.creator = ?");
                args.add(query.getCreatorId());
            }

            if (query.getSearchString() != null) {
                condition.append(" and report.name like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }

            if (query.getLocale() != null) {
                condition.append(" and report.locale = ?");
                args.add(query.getLocale());
            }

            if (CollectionUtils.isNotEmpty(query.getStatuses())) {
                condition.append(" and report.status in ").append(JdbcHelper.makeSqlStringCollection(query.getStatuses(), args, null));
            }

            if (query.getFromCreated() != null) {
                condition.append(" and report.created >= ?");
                args.add(query.getFromCreated());
            }

            if (query.getToCreated() != null) {
                condition.append(" and report.created < ?");
                args.add(query.getToCreated());
            }

            if (query.getFromModified() != null) {
                condition.append(" and report.modified >= ?");
                args.add(query.getFromModified());
            }

            if (query.getToModified() != null) {
                condition.append(" and report.modified < ?");
                args.add(query.getToModified());
            }

            if (CollectionUtils.isNotEmpty(query.getIncludeIds())) {
                condition.append(" and report.id in ").append(JdbcHelper.makeSqlStringCollection(query.getIncludeIds(), args, null));
            }

            if (CollectionUtils.isNotEmpty(query.getExcludeIds())) {
                condition.append(" and report.id not in ").append(JdbcHelper.makeSqlStringCollection(query.getExcludeIds(), args, null));
            }

            if (CollectionUtils.isNotEmpty(query.getScheduledTypes())) {
                condition.append(" and report.scheduled_type in ").append(JdbcHelper.makeSqlStringCollection(query.getScheduledTypes(), args, null));
            }

            if (query.isRemoved() != null) {
                condition.append(" and report.is_removed = ?");
                args.add(query.isRemoved());
            }
        });
    }
}
