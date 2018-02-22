package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.dict.En_MigrationEntry;
import ru.protei.portal.core.model.ent.LegacyEntity;
import ru.protei.portal.core.model.ent.MigrationEntry;

import java.util.Date;

/**
 * Created by michael on 05.07.16.
 */
public class MigrationEntryDAO_Impl extends PortalBaseJdbcDAO<MigrationEntry> implements MigrationEntryDAO {

    public MigrationEntryDAO_Impl () {
        super();
    }


    @Override
    public MigrationEntry getOrCreateEntry(En_MigrationEntry entryType) {
        MigrationEntry entry = getByCondition("entry_code=?", entryType.getCode());

        if (entry == null) {
            entry = new MigrationEntry();
            entry.setCode(entryType.getCode());
            entry.setLastId(0L);
            entry.setLastUpdate(new Date(0L));

            persist(entry);
        }

        return entry;
    }


    @Override
    public void updateEntry(En_MigrationEntry entryType, LegacyEntity entity) {
        if (entity != null) {
            MigrationEntry migrationEntry = getOrCreateEntry(entryType);

            if (migrationEntry.getLastId() == null || migrationEntry.getLastId() < entity.getId())
                migrationEntry.setLastId(entity.getId());

            migrationEntry.setLastUpdate(new Date());
            saveOrUpdate(migrationEntry);
        }
    }
}
