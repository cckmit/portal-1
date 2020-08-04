package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.PersonFavoriteIssuesDAO;
import ru.protei.portal.core.model.ent.PersonFavoriteIssues;
import ru.protei.portal.core.model.util.sqlcondition.Query;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ru.protei.portal.core.model.util.sqlcondition.SqlQueryBuilder.query;

public class PersonFavoriteIssuesDAO_Impl extends PortalBaseJdbcDAO<PersonFavoriteIssues> implements PersonFavoriteIssuesDAO {
    @Override
    public List<Long> getIssueIdListByPersonId(Long personId) {
        Query query = query()
                .select("case_object_id")
                .from(getTableName())
                .where("person_id")
                .equal(personId)
                .asQuery();

        return jdbcTemplate.queryForList(query.buildSql(), Long.class, query.args());
    }

    @Override
    public boolean removeState(Long personId, Long caseObjectId) {
        return removeByCondition(getTableName() + ".person_id = ? AND " + getTableName() + ".case_object_id = ?",
                Arrays.asList(personId, caseObjectId)) > 0;
    }
}
