package ru.protei.portal.core.model.dao.impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DutyLogDAO;
import ru.protei.portal.core.model.ent.DutyLog;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.DutyLogQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.winter.jdbc.annotations.EnumType;

import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.winter.jdbc.JdbcHelper.makeSqlStringCollection;

public class DutyLogDAO_Impl extends PortalBaseJdbcDAO<DutyLog> implements DutyLogDAO {

    private static final Logger log = LoggerFactory.getLogger( DutyLogDAO_Impl.class );

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

            if (query.getPersonIds() != null){
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
}
