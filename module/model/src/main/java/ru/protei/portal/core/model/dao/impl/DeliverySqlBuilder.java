package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dict.En_Gender;
import ru.protei.portal.core.model.query.DeliveryQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.portal.core.model.struct.Interval;
import ru.protei.portal.core.model.util.CrmConstants;
import ru.protei.portal.core.model.util.sqlcondition.Condition;
import ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder;

import java.util.ArrayList;
import java.util.List;

import static ru.protei.portal.core.model.dao.impl.CaseShortViewDAO_Impl.isSearchAtComments;
import static ru.protei.portal.core.model.helper.CollectionUtils.isNotEmpty;
import static ru.protei.portal.core.model.helper.DateRangeUtils.makeInterval;
import static ru.protei.portal.core.model.helper.HelperFunc.makeInArg;
import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.condition;

public class DeliverySqlBuilder {

    public SqlCondition getCondition(DeliveryQuery query) {

        return new SqlCondition().build((condition, args) -> {
            condition.append("1 = 1");
        });

//        return new SqlCondition().build((condition, args) -> {
//            condition.append("deleted = 0");
//
//            if ( query.getId() != null ) {
//                condition.append( " and delivery.id=?" );
//                args.add( query.getId() );
//            }
//
//            if (isNotEmpty(query.getCompanyIds())) {
//                condition.append(" and initiator_company in " + makeInArg(query.getCompanyIds(), false));
//            }
//
//            if (isNotEmpty(query.getManagerIds())) {
//                List<Long> managerIds = new ArrayList<>(query.getManagerIds());
//                boolean isWithoutManager = managerIds.remove(CrmConstants.Employee.UNDEFINED);
//
//                if (!isWithoutManager) {
//                    condition
//                            .append(" and manager IN ")
//                            .append(makeInArg(managerIds, false));
//                } else if (managerIds.isEmpty()) {
//                    condition.append(" and (manager IS NULL or (SELECT person.sex FROM person WHERE person.id = manager) = ?)");
//                    args.add(En_Gender.UNDEFINED.getCode());
//                } else {
//                    condition
//                            .append(" and (manager IN ")
//                            .append(makeInArg(managerIds, false))
//                            .append(" or manager IS NULL or (SELECT person.sex FROM person WHERE person.id = manager) = ?)");
//                    args.add(En_Gender.UNDEFINED.getCode());
//                }
//            }
//
//            if (isNotEmpty(query.getProductIds())) {
//                if (query.getProductIds().remove(CrmConstants.Product.UNDEFINED)) {
//                    condition.append(" and (product_id is null");
//                    if (!query.getProductIds().isEmpty()) {
//                        condition.append(" or product_id in " + makeInArg(query.getProductIds(), false));
//                    }
//                    condition.append(")");
//                } else {
//                    condition.append(" and product_id in " + makeInArg(query.getProductIds(), false));
//                }
//            }
//
//            Interval deliveryInterval = makeInterval(query.getDeliveryRange());
//
//            if ( deliveryInterval != null ) {
//                if (deliveryInterval.from != null) {
//                    condition.append( " and delivery.departure_date >= ?" );
//                    args.add( deliveryInterval.from );
//                }
//                if (deliveryInterval.to != null) {
//                    condition.append( " and delivery.departure_date < ?" );
//                    args.add( deliveryInterval.to );
//                }
//            }
//
//            if (query.getSearchString() != null && !query.getSearchString().trim().isEmpty()) {
//                Condition searchCondition = condition()
//                        .or( "delivery.name" ).like( query.getSearchString() )
//                        .or( "delivery.name" ).like( query.getAlternativeSearchString() )
//                        .or( "delivery.description" ).like( query.getSearchString() )
//                        .or( "delivery.description" ).like( query.getAlternativeSearchString() );
//                condition.append( " and (" )
//                        .append( searchCondition.getSqlCondition() )
//                        .append( ")" );
//                args.addAll( searchCondition.getSqlParameters() );
//            }
//        });
    }
}
