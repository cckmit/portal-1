package ru.protei.portal.core.model.query;

import ru.protei.portal.core.model.dict.En_SortDir;
import ru.protei.portal.core.model.dict.En_SortField;
import ru.protei.portal.core.model.helper.HelperFunc;

/**
 * Created by michael on 22.11.16.
 */
public class CompanyGroupQuery extends BaseQuery {

    public CompanyGroupQuery() {
    }

    public CompanyGroupQuery(String searchString, En_SortField sortField, En_SortDir sortDir) {
        super(searchString, sortField, sortDir);
    }

    @Override
    public SqlCondition sqlCondition() {
        return  new SqlCondition().build((condition, args) -> {
            if (HelperFunc.isLikeRequired(searchString)) {
                condition.append("group_name like ?");
                args.add(HelperFunc.makeLikeArg(searchString));
            }
        });
    }
}
