package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.PersonFavoriteIssues;

import java.util.List;

public interface PersonFavoriteIssuesDAO extends PortalBaseDAO<PersonFavoriteIssues> {
    List<Long> getIssueIdListByPersonId(Long personId);

    boolean removeState(Long personId, Long caseObjectId);
}
