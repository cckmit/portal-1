package ru.protei.portal.core.model.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DutyLogDAO;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcQueryParameters;
import ru.protei.winter.jdbc.JdbcSort;
import ru.protei.winter.jdbc.annotations.EnumType;

import java.util.LinkedHashMap;

import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.utils.TypeConverters.toWinter;
import static ru.protei.winter.jdbc.JdbcHelper.makeSqlStringCollection;

public class DutyLogDAO_Impl extends PortalBaseJdbcDAO<DutyLog> implements DutyLogDAO {

    private static final Logger log = LoggerFactory.getLogger( DutyLogDAO_Impl.class );

    public SearchResult<DutyLog> getSearchResultByQuery(DataQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    @SqlConditionBuilder
    public SqlCondition createSqlCondition(DutyLogQuery query) {
        log.info( "createSqlCondition(): query={}", query );
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            Interval dateRange = makeInterval(query.getDateRange());
            if ( dateRange != null ) {
                if (dateRange.from != null) {
                    condition.append(" and (duty_log.date_from >= ? or duty_log.date_to >= ?)");
                    args.add(dateRange.from);
                    args.add(dateRange.from);
                }
                if (dateRange.to != null) {
                    condition.append(" and (duty_log.date_to <= ? or duty_log.date_from <= ?)");
                    args.add(dateRange.to);
                    args.add(dateRange.to);
                }
            }

            if (CollectionUtils.isNotEmpty(query.getPersonIds())) {
                condition.append(" and duty_log.person_id in ").append(HelperFunc.makeInArg(query.getPersonIds()));
            }

            if (CollectionUtils.isNotEmpty(query.getTypes())){
                condition.append(" and duty_log.type in ").append(makeSqlStringCollection(query.getTypes(), args, EnumType.ID));
            }
        });
    }

    @Override
    public boolean checkExists(DutyLog value) {
        return checkExistsByCondition("duty_log.date_from = ? and duty_log.date_to = ? and duty_log.person_id = ?" +
                " and duty_log.type = ?", value.getFrom(), value.getTo(), value.getPersonId(), value.getType().getId());
    }

    private JdbcQueryParameters buildJdbcQueryParameters(DataQuery query) {

        SqlCondition where = createSqlCondition(query);

        JdbcQueryParameters parameters = new JdbcQueryParameters();
        if (where.isConditionDefined())
            parameters.withCondition(where.condition, where.args);

        parameters.withOffset(query.getOffset());
        parameters.withLimit(query.getLimit());
        final JdbcSort sort;
        if (query.getSortField() == En_SortField.duty_log_date_from) {
            sort = TypeConverters.createSort(query);
        } else {

            LinkedHashMap<String, JdbcSort.DescriptionParam> params = new LinkedHashMap<>();
            params.put(query.getSortField().getFieldName(), new JdbcSort.DescriptionParam(toWinter(query.getSortDir()), null));
            params.put(En_SortField.duty_log_date_from.getFieldName(), new JdbcSort.DescriptionParam(toWinter(En_SortDir.ASC), null));
            sort = new JdbcSort(params);
        }
        parameters.withSort(sort);

        return parameters;
    }
}
