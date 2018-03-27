package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.RedmineStatusMapEntryDAO;
import ru.protei.portal.core.model.ent.RedmineStatusMapEntry;

import java.util.List;

public class RedmineStatusMapEntryDAO_Impl extends PortalBaseJdbcDAO<RedmineStatusMapEntry> implements RedmineStatusMapEntryDAO {
    @Override
    public RedmineStatusMapEntry getRedmineStatus(long oldStateId, long stateId, long mapId) {
        return getByCondition("MAP_ID = ? AND LOCAL_status_id = ? and LOCAL_previous_status_id = ?",
                mapId, stateId, oldStateId);
    }

    @Override
    public List<RedmineStatusMapEntry> getListByEndpointId(long mapId) {
        return getListByCondition("MAP_ID = ?", mapId);
    }
}
