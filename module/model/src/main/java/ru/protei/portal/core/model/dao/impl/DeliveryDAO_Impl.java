package ru.protei.portal.core.model.dao.impl;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DeliveryDAO;
import ru.protei.portal.core.model.ent.Delivery;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.utils.TypeConverters;
import ru.protei.winter.core.utils.beans.SearchResult;
import ru.protei.winter.jdbc.JdbcQueryParameters;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;

/**
 * DAO для поставок
 */
public class DeliveryDAO_Impl extends PortalBaseJdbcDAO<Delivery> implements DeliveryDAO {

    @Autowired
    DeliverySqlBuilder deliverySqlBuilder;

    public static final String LEFT_JOIN_PROJECT_CASE_OBJECT =
            " LEFT JOIN project PRJ ON delivery.project_id = PRJ.ID" +
                    " LEFT JOIN case_object CO_PR on CO_PR.ID= PRJ.ID";

    private static final String LEFT_JOIN_CREATOR_PERSON =
            " LEFT JOIN person PER ON CO.CREATOR = PER.ID";

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
        String joins = LEFT_JOIN_CREATOR_PERSON;

        if (isNotEmpty(query.getCompanyIds())
                || isNotEmpty(query.getManagerIds())
                || isNotEmpty(query.getProductIds())
                || query.getMilitary() != null) {
            joins += LEFT_JOIN_PROJECT_CASE_OBJECT;
        }
        parameters.withJoins(joins);

        return parameters;
    }
}
