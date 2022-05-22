package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.LocationDAO;
import ru.protei.portal.core.model.ent.Location;
import ru.protei.portal.core.model.helper.CollectionUtils;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.LocationQuery;
import ru.protei.portal.core.model.query.SqlCondition;

/**
 *  DAO к запросам местоположений
 */
public class LocationDAO_Impl extends PortalBaseJdbcDAO<Location> implements LocationDAO {

    @SqlConditionBuilder
    public SqlCondition createLocationSqlCondition(LocationQuery query ) {
        return new SqlCondition().build((condition, args) -> {
            condition.append( "1=1" );
            if ( query.getType() != null ) {
                condition.append( " and TYPE_ID=?" );
                args.add( query.getType().getId() );
            }

            if (CollectionUtils.isNotEmpty(query.getDistrictIds())) {
                condition.append( " and PARENT_ID in " );
                condition.append(HelperFunc.makeInArg(query.getDistrictIds()));
            }

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                condition.append(" and NAME like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString(), true));
            }
        });
    }
}
