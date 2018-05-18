package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
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
import java.util.stream.Collectors;

public class ReportDAO_Impl extends PortalBaseJdbcDAO<Report> implements ReportDAO {

    @Override
    public Report getReport(Long creatorId, Long reportId) {
        return getByCondition("creator = ? and id = ?", creatorId, reportId);
    }

    @Override
    public List<Report> getReportsByQuery(Long creatorId, ReportQuery query, Set<Long> excludeIds) {
        SqlCondition where = createSqlCondition(creatorId, query, null, excludeIds);
        return getList(new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
                .withOffset(query.getOffset())
                .withLimit(query.getLimit())
                .withSort(TypeConverters.createSort(query))
        );
    }

    @Override
    public List<Report> getReportsByIds(Long creatorId, Set<Long> includeIds, Set<Long> excludeIds) {
        SqlCondition where = createSqlCondition(creatorId, null, includeIds, excludeIds);
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
        SqlCondition where = createSqlCondition(null, query, null, null);
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
        SqlCondition where = createSqlCondition(null, query, null, null);
        return getList(new JdbcQueryParameters()
                .withCondition(where.condition, where.args)
                .withDistinct(true)
        );
    }

    @Override
    public SqlCondition createSqlCondition(Long creatorId, ReportQuery query, Set<Long> includeIds, Set<Long> excludeIds) {
        return new SqlCondition().build((condition, args) -> {

            condition.append("1=1");

            if (creatorId != null) {
                condition.append(" and creator = ?");
                args.add(creatorId);
            }

            if (query != null) {

                if (query.getSearchString() != null) {
                    condition.append(" and name like ?");
                    args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
                }

                if (query.getLocale() != null) {
                    condition.append(" and locale = ?");
                    args.add(query.getLocale());
                }

                if (CollectionUtils.isNotEmpty(query.getStatuses())) {
                    condition.append(" and status in ( ");
                    condition.append(query.getStatuses().stream()
                            .map(En_ReportStatus::name)
                            .collect(Collectors.joining(","))
                    );
                    condition.append(" )");
                }

                if (query.getFromCreated() != null) {
                    condition.append(" and created >= ?");
                    args.add(query.getFromCreated());
                }

                if (query.getToCreated() != null) {
                    condition.append(" and created < ?");
                    args.add(query.getToCreated());
                }

                if (query.getFromModified() != null) {
                    condition.append(" and modified >= ?");
                    args.add(query.getFromModified());
                }

                if (query.getToModified() != null) {
                    condition.append(" and modified < ?");
                    args.add(query.getToModified());
                }
            }

            if (CollectionUtils.isNotEmpty(includeIds)) {
                condition.append(" and id in ").append(JdbcHelper.makeSqlStringCollection(includeIds, args, null));
            }

            if (CollectionUtils.isNotEmpty(excludeIds)) {
                condition.append(" and id not in ").append(JdbcHelper.makeSqlStringCollection(excludeIds, args, null));
            }
        });
    }
}
