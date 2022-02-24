package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseTimeElapsedApiSumDAO;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.ent.CaseTimeElapsedApiSum;
import ru.protei.portal.core.model.query.CaseTimeElapsedApiQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;

import static ru.protei.portal.core.model.ent.CaseTimeElapsedApiSum.*;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class CaseTimeElapsedApiSumDAO_Impl extends PortalBaseJdbcDAO<CaseTimeElapsedApiSum> implements CaseTimeElapsedApiSumDAO {
    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CaseTimeElapsedApiQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("case_comment.time_elapsed is not NULL");

            if (query.getPeriod() != null) {
                Interval interval = makeInterval(query.getPeriod());
                if (interval.from != null) {
                    condition.append( " and created >= ?" );
                    args.add(interval.from );
                }
                if (interval.to != null) {
                    condition.append( " and created < ?" );
                    args.add( interval.to );
                }
            }

            if (query.getProductIds() != null) {
                condition.append(" and " + CASE_OBJECT_ALIAS + "." + CaseObject.Columns.PRODUCT_ID + " in")
                        .append(makeInArg(query.getProductIds(), true));
            }

            if (query.getCompanyIds() != null) {
                condition.append(" and " + CASE_OBJECT_ALIAS + "." + CaseObject.Columns.INITIATOR_COMPANY + " in")
                        .append(makeInArg(query.getCompanyIds(), true));
            }

            if (query.getEmployeeIds() != null) {
                condition.append(" and author_id in")
                        .append(makeInArg(query.getEmployeeIds(), true));
            }
        });
    }
}
