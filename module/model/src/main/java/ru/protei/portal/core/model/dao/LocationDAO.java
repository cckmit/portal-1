package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.annotations.SqlConditionBuilder;
import ru.protei.portal.core.model.ent.DevUnit;
import ru.protei.portal.core.model.ent.Location;
import ru.protei.portal.core.model.query.DistrictQuery;
import ru.protei.portal.core.model.query.SqlCondition;

/**
 * DAO для местоположений
 */
public interface LocationDAO extends PortalBaseDAO< Location > {

    @SqlConditionBuilder
    SqlCondition createDistrictSqlCondition( DistrictQuery query );
}
