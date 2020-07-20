package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.PersonNotifier;

import java.util.List;

public interface PersonNotifierDAO extends PortalBaseDAO<PersonNotifier> {
    List<PersonNotifier> getByPersonId(Long personId);
    List<PersonNotifier> getByNotifierId(Long notifierId);
}
