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
    public Long getMigratedLastId(String code, Long defId) {
        MigrationEntry entry = getByCondition("entry_code=?", code);
        return entry != null ? entry.getLastId() : defId;
    }

    @Override
    public void confirmMigratedLastId(String code, Long id) {
        MigrationEntry entry = getByCondition("entry_code=?", code);
        if (entry == null) {
            entry = new MigrationEntry();
            entry.setCode(code);
        }

        entry.setLastId(id);

        if (entry.getId() == null)
            persist(entry);
        else
            this.merge(entry);
    }



    @Override
    public Long getMigratedLastUpdate(String code) {
        return getMigratedLastUpdate(code, null);
    }

    @Override
    public Long getMigratedLastUpdate(String code, Long defDate) {
        MigrationEntry entry = getByCondition("entry_code=?", code);
        return entry != null ? entry.getLastUpdate().getTime() : defDate;
    }

    @Override
    public void confirmMigratedLastUpdate(String code, Long date) {
        MigrationEntry entry = getByCondition("entry_code=?", code);
        if (entry == null) {
            entry = new MigrationEntry();
            entry.setCode(code);
        }

        entry.setLastUpdate(date);

        if (entry.getId() == null)
            persist(entry);
        else
            this.merge(entry);
    }
}
