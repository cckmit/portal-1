package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.PersonToCaseFilter;

import java.util.List;

public interface PersonToCaseFilterDAO extends PortalBaseDAO<PersonToCaseFilter> {
    List<PersonToCaseFilter> getByPersonId(Long personId);
}
