package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcHelper;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.Date;
import java.util.List;
import java.util.Set;

public class ReportDAO_Impl extends PortalBaseJdbcDAO<Report> implements ReportDAO {

    @Override
    public Report getReport(Long creatorId, Long reportId) {
        return getByCondition("report.creator = ? and report.id = ?", creatorId, reportId);
    }

    @Override
    public List<Report> getReportsByQuery(Long creatorId, ReportQuery query, Set<Long> excludeIds) {
        query.setCreatorId(creatorId);
        query.setExcludeIds(excludeIds);
        SqlCondition where = createSqlCondition(query);
        return getList(new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
                .withOffset(query.getOffset())
                .withLimit(query.getLimit())
                .withSort(TypeConverters.createSort(query))
        );
    }

    @Override
    public Long countReportsByQuery(Long creatorId, ReportQuery query, Set<Long> excludeIds) {
        query.setCreatorId(creatorId);
        query.setExcludeIds(excludeIds);
        return count(query);
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
    public List<Report> getReportsByStatuses(List<En_ReportStatus> statuses, Date lastModifiedBefore) {
        ReportQuery query = new ReportQuery();
        query.setStatuses(statuses);
        query.setToModified(lastModifiedBefore);
        SqlCondition where = createSqlCondition(query);
        return getList(new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
        );
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
        });
    }
}
