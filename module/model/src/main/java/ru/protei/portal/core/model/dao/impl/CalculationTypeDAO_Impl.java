package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.CalculationTypeDAO;
import ru.protei.portal.core.model.ent.CalculationType;
import ru.protei.portal.core.model.util.sqlcondition.Query;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class CalculationTypeDAO_Impl extends PortalBaseJdbcDAO<CalculationType> implements CalculationTypeDAO {

    @Override
    public CalculationType getCalculationTypeBy(String refKey) {
        Query query = query().asCondition()
                             .and("ref_key").equal(refKey)
                             .asQuery();

        return getByCondition(query.buildSql(), query.args());
    }
}
