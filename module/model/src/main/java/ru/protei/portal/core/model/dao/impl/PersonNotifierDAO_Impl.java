package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.PersonNotifierDAO;
import ru.protei.portal.core.model.ent.PersonNotifier;

import java.util.Collections;
import java.util.List;

public class PersonNotifierDAO_Impl extends PortalBaseJdbcDAO<PersonNotifier> implements PersonNotifierDAO {

    @Override
    public List<PersonNotifier> getByPersonId(Long personId) {
        List<PersonNotifier> result = getListByCondition("person_id = ?", personId);
        return result == null ? Collections.emptyList() : result;
    }

    @Override
    public List<PersonNotifier> getByNotifierId(Long notifierId) {
        List<PersonNotifier> result = getListByCondition("notifier_id = ?", notifierId);
        return result == null ? Collections.emptyList() : result;
    }
}
