package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.MigrationEntryDAO;
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
    public MigrationEntry getOrCreateEntry(String code) {
        MigrationEntry entry = getByCondition("entry_code=?", code);

        if (entry == null) {
            entry = new MigrationEntry();
            entry.setCode(code);
            entry.setLastId(0L);
            entry.setLastUpdate(new Date(0L));

            persist(entry);
        }

        return entry;
    }

}
