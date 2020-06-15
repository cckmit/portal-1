package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.PersonToCaseFilterDAO;
import ru.protei.portal.core.model.ent.PersonToCaseFilter;
import ru.protei.portal.core.model.util.sqlcondition.Query;

import java.util.List;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class PersonToCaseFilterDAO_Impl extends PortalBaseJdbcDAO<PersonToCaseFilter> implements PersonToCaseFilterDAO  {
    @Override

    public List<PersonToCaseFilter> getByPersonId(Long personId) {
        Query query = query()
                .where("person_id").equal(personId)
                .asQuery();

        return getListByCondition(query.buildSql(),  query.args());
    }
}
