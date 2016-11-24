package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.dao.DevUnitDAO;
import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.helper.HelperFunc;
import ru.protei.portal.core.model.query.ProductQuery;
import ru.protei.portal.core.model.query.SqlCondition;
import ru.protei.winter.jdbc.JdbcSort;

import java.util.List;

/**
 * Created by michael on 23.05.16.
 */
public class DevUnitDAO_Impl extends PortalBaseJdbcDAO<DevUnit> implements DevUnitDAO {

    @Override
    public DevUnit checkExistsByName(En_DevUnitType type, String name) {
        return getByCondition("UTYPE_ID=? and UNIT_NAME=?", type.getId(), name);
    }


    @SqlConditionBuilder
    public SqlCondition createProductSqlCondition(ProductQuery query) {
        return new SqlCondition().build((condition, args) -> {
            condition.append("UTYPE_ID=?");
            args.add(En_DevUnitType.PRODUCT.getId());

            if (HelperFunc.isLikeRequired(query.getSearchString())) {
                condition.append(" and UNIT_NAME like ?");
                args.add(HelperFunc.makeLikeArg(query.getSearchString()));
            }

            if (query.getState() != null) {
                condition.append(" and UNIT_STATE=?");
                args.add(query.getState().getId());
            }
        });
    }

}
