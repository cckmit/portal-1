package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.PersonFavoriteIssuesDAO;
import ru.protei.portal.core.model.ent.PersonFavoriteIssues;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

public class PersonFavoriteIssuesDAO_Impl extends PortalBaseJdbcDAO<PersonFavoriteIssues> implements PersonFavoriteIssuesDAO {
    @Override
    public List<PersonFavoriteIssues> getListByPersonId(Long personId) {
        return getListByCondition(getTableName() + ".person_id = ?", Collections.singletonList(personId));
    }

    @Override
    public boolean removeState(Long personId, Long caseObjectId) {
        return removeByCondition(getTableName() + ".person_id = ? AND " + getTableName() + ".case_object_id = ?",
                Arrays.asList(personId, caseObjectId)) > 0;
    }
}
