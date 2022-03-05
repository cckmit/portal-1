package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.CaseElapsedTimeApiDAO;
import ru.protei.portal.core.model.ent.CaseElapsedTimeApi;
import ru.protei.portal.core.model.ent.CaseObject;
import ru.protei.portal.core.model.query.CaseElapsedTimeApiQuery;
import ru.protei.portal.core.model.query.SqlCondition;

import static ru.protei.portal.core.model.ent.CaseElapsedTimeApi.CASE_OBJECT_ALIAS;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class CaseElapsedTimeApiDAO_Impl extends PortalBaseJdbcDAO<CaseElapsedTimeApi> implements CaseElapsedTimeApiDAO {
    @Override
    @SqlConditionBuilder
    public SqlCondition createSqlCondition(CaseElapsedTimeApiQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("case_comment.time_elapsed is not NULL");

            if (query.getFrom() != null) {
                condition.append( " and case_comment.created >= ?" );
                args.add(query.getFrom());
            }
            if (query.getTo() != null) {
                condition.append( " and case_comment.created < ?" );
                args.add( query.getTo() );
            }

            if (query.getProductIds() != null) {
                condition.append(" and " + CASE_OBJECT_ALIAS + "." + CaseObject.Columns.PRODUCT_ID + " in")
                        .append(makeInArg(query.getProductIds(), true));
            }

            if (query.getCompanyIds() != null) {
                condition.append(" and " + CASE_OBJECT_ALIAS + "." + CaseObject.Columns.INITIATOR_COMPANY + " in")
                        .append(makeInArg(query.getCompanyIds(), true));
            }

            if (query.getAuthorIds() != null) {
                condition.append(" and author_id in")
                        .append(makeInArg(query.getAuthorIds(), false));
            }
        });
    }
}
