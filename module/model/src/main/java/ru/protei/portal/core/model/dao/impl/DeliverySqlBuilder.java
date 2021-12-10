package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dict.En_CustomerType;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;

import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;

public class DeliverySqlBuilder {

    public SqlCondition getCondition(DeliveryQuery query) {

        return new SqlCondition().build((condition, args) -> {

            condition.append("1=1");

            if ( query.getId() != null ) {
                condition.append( " and delivery.id=?" );
                args.add( query.getId() );
            } else if (isNotEmpty(query.getSerialNumbers())) {
                condition.append(" and delivery.id in ( SELECT k.delivery_id FROM kit k where k.serial_number in " + makeInArg(query.getSerialNumbers(), true) + " )");
            }

            //компания это delivery.project.case_object.initiator_company
            if (isNotEmpty(query.getCompanyIds())) {
                condition
                        .append(" and (CO_PR.initiator_company IN ")
                        .append(makeInArg(query.getCompanyIds()))
                        .append(")");
            }

            // менеджер это delivery.project.case_object.MANAGER
            if (isNotEmpty(query.getManagerIds())) {
                condition
                        .append(" and (CO_PR.MANAGER IN ")
                        .append(makeInArg(query.getManagerIds()))
                        .append(")");
            }

            // продукт это delivery.project.project_to_product.product_id
            if (isNotEmpty(query.getProductIds())) {
                condition
                        .append(" and PRJ.ID IN")
                        .append(" (SELECT project_id from project_to_product where product_id IN ")
                        .append(makeInArg(query.getProductIds()))
                        .append(")");
            }
            //выборка по компании создателя в зависимости от UserRole.scope запрашиваемого
            if (isNotEmpty(query.getCreatorCompanyIds())){
                condition
                        .append(" and PER.company_id IN ")
                        .append(makeInArg(query.getCreatorCompanyIds()));
            }

            Interval deliveryInterval = makeInterval(query.getDepartureDateRange());

            if ( deliveryInterval != null ) {
                if (deliveryInterval.from != null) {
                    condition.append( " and delivery.departure_date >= ?" );
                    args.add( deliveryInterval.from );
                }
                if (deliveryInterval.to != null) {
                    condition.append( " and delivery.departure_date < ?" );
                    args.add( deliveryInterval.to );
                }
            }

            if ( isNotEmpty(query.getStateIds()) ) {
                condition.append(" and CO.state in " + makeInArg(query.getStateIds(), false));
            }

            if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
                Condition searchCondition = SqlQueryBuilder.condition()
                        .or( "CO.CASE_NAME" ).like( query.getSearchString() )
                        .or( "CO.CASE_NAME" ).like( query.getAlternativeSearchString() )
                        .or( "CO.info" ).like( query.getSearchString() )
                        .or( "CO.info" ).like( query.getAlternativeSearchString() );
                condition.append( " and (" )
                        .append( searchCondition.getSqlCondition() )
                        .append( ")" );
                args.addAll( searchCondition.getSqlParameters() );
            }

            if (query.getDeleted() != null) {
                condition.append(" and CO.deleted = ").append(query.getDeleted());
            }

            if (query.getMilitary() != null) {
                condition
                        .append(" and PRJ.customer_type")
                        .append(query.getMilitary() ? " = " : " != ")
                        .append(En_CustomerType.MINISTRY_OF_DEFENCE.getId());
            }
        });
    }
}
