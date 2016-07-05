package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.MigrationEntryDAO;
import ru.protei.portal.core.model.ent.MigrationEntry;

/**
 * Created by michael on 05.07.16.
 */
public class MigrationEntryDAO_Impl extends PortalBaseJdbcDAO<MigrationEntry> implements MigrationEntryDAO {

    public MigrationEntryDAO_Impl () {
        super();
    }

    @Override
    public Long getLastMigratedID(String code) {
        return getLastMigratedID(code, null);
    }

    @Override
    public Long getLastMigratedID(String code, Long defId) {
        MigrationEntry entry = getByCondition("entry_code=?", code);
        return entry != null ? entry.getLastId() : defId;
    }

    @Override
    public void confirmMigratedID(String code, Long id) {
        MigrationEntry entry = getByCondition("entry_code=?", code);
        if (entry == null) {
            entry = new MigrationEntry();
            entry.setCode(code);
        }

        entry.setLastId(id);
        persist(entry);
    }
}
