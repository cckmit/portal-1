package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
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
public interface DevUnitDAO extends PortalBaseDAO<DevUnit> {

    DevUnit checkExistsByName(En_DevUnitType type, String name);


    @SqlConditionBuilder
    SqlCondition createProductSqlCondition(ProductQuery query);
}
