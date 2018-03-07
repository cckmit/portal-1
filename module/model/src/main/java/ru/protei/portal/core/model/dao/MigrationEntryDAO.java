package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_MigrationEntry;
import ru.protei.portal.core.model.ent.LegacyEntity;
import ru.protei.portal.core.model.ent.MigrationEntry;

import java.util.Date;

/**
 * Created by michael on 05.07.16.
 */
public interface MigrationEntryDAO extends PortalBaseDAO<MigrationEntry> {

    MigrationEntry getOrCreateEntry(En_MigrationEntry entryType);

    MigrationEntry updateEntry (En_MigrationEntry entryType, LegacyEntity entity);

    MigrationEntry updateEntry (En_MigrationEntry entryType, long lastId, Date lastUpdate);

}
