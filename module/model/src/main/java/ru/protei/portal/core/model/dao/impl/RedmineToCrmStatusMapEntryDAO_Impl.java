package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.RedmineToCrmStatusMapEntryDAO;
import ru.protei.portal.core.model.ent.RedmineToCrmEntry;

public class RedmineToCrmStatusMapEntryDAO_Impl extends PortalBaseJdbcDAO<RedmineToCrmEntry> implements
        RedmineToCrmStatusMapEntryDAO {

    @Override
    public RedmineToCrmEntry getLocalStatus(long mapId, int redmineStateId) {
        return getByCondition("MAP_ID = ? and RM_status_id = ?", mapId, redmineStateId);
    }
}
