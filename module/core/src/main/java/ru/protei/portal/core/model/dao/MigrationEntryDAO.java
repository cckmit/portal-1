package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.MigrationEntry;

/**
 * Created by michael on 05.07.16.
 */
public interface MigrationEntryDAO extends PortalBaseDAO<MigrationEntry> {

    public MigrationEntry getOrCreateEntry(String code);

}
