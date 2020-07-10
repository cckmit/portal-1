package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.ContractorDAO;
import ru.protei.portal.core.model.ent.Contractor;
import ru.protei.portal.core.model.util.sqlcondition.Query;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class ContractorDAO_Impl extends PortalBaseJdbcDAO<Contractor>
        implements ContractorDAO {

    @Override
    public Contractor getContractorByRefKey(String refKey) {
        Query query = query().asCondition()
                .and("ref_key").equal(refKey)
                .asQuery();

        return getByCondition(query.buildSql(), query.args());
    }
}