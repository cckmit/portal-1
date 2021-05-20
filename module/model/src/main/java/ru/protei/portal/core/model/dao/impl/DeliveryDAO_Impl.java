package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DeliveryDAO;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.CaseQuery;
import ru.protei.portal.core.model.query.DataQuery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.view.CaseShortView;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import static java.lang.Boolean.TRUE;

/**
 * DAO для поставок
 */
public class DeliveryDAO_Impl extends PortalBaseJdbcDAO<Delivery> implements DeliveryDAO {

    @Autowired
    DeliverySqlBuilder deliverySqlBuilder;

    @Override
    @SqlConditionBuilder
    public SqlCondition baseQueryCondition(DeliveryQuery query) {
        return deliverySqlBuilder.getCondition(query);
    }

    @Override
    public SearchResult<Delivery> getSearchResult(DeliveryQuery query) {
        JdbcQueryParameters parameters = buildJdbcQueryParameters(query);
        return getSearchResult(parameters);
    }

    private JdbcQueryParameters buildJdbcQueryParameters(DeliveryQuery query) {

        JdbcQueryParameters parameters = new JdbcQueryParameters();

        SqlCondition where = createSqlCondition(query);
        if (where.isConditionDefined()) {
            parameters.withCondition(where.condition, where.args);
        }

        parameters.withOffset(query.getOffset());
        parameters.withLimit(query.getLimit());
        parameters.withSort(TypeConverters.createSort( query ));

//        if (query.getPlanId() != null) {
//            parameters.withDistinct(false);
//            parameters.withJoins(LEFT_JOIN_PLAN_ORDER);
//        } else {
//            String joins = "";
//            if (isSearchAtComments(query)) {
//                joins += LEFT_JOIN_CASE_COMMENT;
//            }
//            if (isFilterByTagNames(query)) {
//                joins += LEFT_JOIN_CASE_TAG;
//            }
//            if (TRUE.equals(query.isCheckImportanceHistory())) {
//                joins += LEFT_JOIN_HISTORY;
//            }
//            if (!joins.equals("")) {
//                parameters.withDistinct(true);
//                parameters.withJoins(joins);
//            }
//        }

        return parameters;
    }
}
