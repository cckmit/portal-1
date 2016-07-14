package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.MigrationEntry;

/**
 * Created by michael on 05.07.16.
 */
public interface MigrationEntryDAO extends PortalBaseDAO<MigrationEntry> {

    public Long getMigratedLastId(String code, Long defId);
    public void confirmMigratedLastId(String code, Long id);

    public Long getMigratedLastUpdate(String code);
    public Long getMigratedLastUpdate(String code, Long defDate);
    public void confirmMigratedLastUpdate(String code, Long date);

}
