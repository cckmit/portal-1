package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.PersonToCaseFilter;

public interface PersonCaseFilterDAO extends PortalBaseDAO<PersonToCaseFilter> {
    boolean isExist(Long personId, Long caseFilterId);
    boolean removeByPersonIdAndCaseFilterId(Long personId, Long caseFilterId);
    PersonToCaseFilter getByPersonIdAndCaseFilterId(Long personId, Long caseFilterId);
}
