package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.struct.ContactItem;

import java.util.List;

public interface ContactItemDAO extends PortalBaseDAO<ContactItem> {
    List<ContactItem> getForPersonsIds( List<Long> peronsIds );

}
