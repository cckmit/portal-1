package ru.protei.portal.core.model.dao.impl;

import org.apache.commons.collections4.CollectionUtils;
import ru.protei.portal.core.model.dao.ReportDAO;
import ru.protei.portal.core.model.dict.En_ReportStatus;
import ru.protei.portal.core.model.ent.Report;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ReportQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

public class ReportDAO_Impl extends PortalBaseJdbcDAO<Report> implements ReportDAO {

    @Override
    public List<Report> getReportsByQuery(Long creatorId, ReportQuery query) {
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
    public List<Report> getReportsToProcess(int limit) {
        return getListByCondition("status = ? limit ?", En_ReportStatus.CREATED.name(), limit);
    }

    @Override
    public List<Report> getOutdatedReports(long liveTime) {
        Date date = new Date(System.currentTimeMillis() - liveTime);
        return getListByCondition("status in (?, ?) and modified < ?", En_ReportStatus.READY.name(), En_ReportStatus.ERROR.name(), date);
    }

    @Override
    public List<Report> getHangReports(long hangInterval) {
        Date date = new Date(System.currentTimeMillis() - hangInterval);
        return getListByCondition("status = ? and modified < ?", En_ReportStatus.PROCESS.ordinal(), date);
    }

    @Override
    public SqlCondition createSqlCondition(Long creatorId, ReportQuery query) {
        return new SqlCondition().build((condition, args) -> {

            condition.append("1=1");

            if (creatorId != null) {
                condition.append(" and creator = ?");
                args.add(creatorId);
            }

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

            if (query.getFromDate() != null) {
                condition.append(" and created >= ?");
                args.add(query.getFromDate());
            }

            if (query.getToDate() != null) {
                condition.append(" and created < ?");
                args.add(query.getToDate());
            }
        });
    }
}
