package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_DevUnitState;
import ru.protei.portal.core.model.dict.En_DevUnitType;
import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.HelperFunc;

/**
 * Created by michael on 12.10.16.
 */
public class ProductQuery extends BaseQuery {

    En_DevUnitState state;

    public ProductQuery() {
        sortField = En_SortField.prod_name;
        sortDir = En_SortDir.ASC;
    }

    public ProductQuery(String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
    }

    public En_DevUnitState getState() {
        return state;
    }

    public void setState(En_DevUnitState state) {
        this.state = state;
    }

    @Override
    public SqlCondition sqlCondition() {
        return new SqlCondition().build((condition, args) -> {
            condition.append("UTYPE_ID=?");
            args.add(En_DevUnitType.PRODUCT.getId());

            if (HelperFunc.isLikeRequired(searchString)) {
                condition.append(" and UNIT_NAME like ?");
                args.add(HelperFunc.makeLikeArg(searchString));
            }

            if (state != null) {
                condition.append(" and UNIT_STATE=?");
                args.add(state.getId());
            }
        });
    }
}
