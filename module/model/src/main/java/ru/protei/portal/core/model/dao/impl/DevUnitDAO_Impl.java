package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ProductDirectionQuery;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.core.utils.collections.CollectionUtils;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Created by michael on 23.05.16.
 */
public class DevUnitDAO_Impl extends PortalBaseJdbcDAO<DevUnit> implements DevUnitDAO {

    @Override
    public DevUnit checkExistsByName(En_DevUnitType type, String name) {
        return getByCondition("UTYPE_ID=? and UNIT_NAME=?", type.getId(), name);
    }

    @Override
    public DevUnit getByLegacyId(En_DevUnitType type, Long legacyId) {
        return getByCondition("UTYPE_ID=? and OLD_ID=?", type.getId(), legacyId);
    }

    @Override
    public boolean updateState(DevUnit newState) {
        return partialMerge(newState, "UNIT_STATE");
    }

    @Override
    public Map<Long, Long> getProductOldToNewMap() {
        Map<Long,Long> result = new HashMap<>();
        getListByCondition("UTYPE_ID=? and old_id is not null", En_DevUnitType.PRODUCT.getId())
                .forEach(unit -> result.put(unit.getOldId(), unit.getId()));
        return result;
    }

    @SqlConditionBuilder
    public SqlCondition createProductSqlCondition(ProductQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("1=1");

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                String arg = HelperFunc.makeLikeArg(query.getSearchString(), true);
                condition.append(" and (UNIT_NAME like ? or ALIASES like ?)");
                args.add(arg);
                args.add(arg);
            }

            if (query.getState() != null) {
                condition.append(" and UNIT_STATE=?");
                args.add(query.getState().getId());
            }

            if (!CollectionUtils.isEmpty(query.getTypes())) {
                condition
                        .append(" and UTYPE_ID in (")
                        .append(query.getTypes().stream()
                                .map((type) -> String.valueOf(type.getId()))
                                .collect(Collectors.joining(","))
                        )
                        .append(")");
            } else {
                condition.append(" and UTYPE_ID <> ?");
                args.add(En_DevUnitType.DIRECTION.getId());
            }
        });
    }

    @SqlConditionBuilder
    public SqlCondition createProductDirectionSqlCondition( ProductDirectionQuery query ) {
        return new SqlCondition().build( (condition, args)->{
            condition.append( "UTYPE_ID=?" );
            args.add( En_DevUnitType.DIRECTION.getId() );
        } );
    }

}
