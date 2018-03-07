package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.RedmineStatusMapEntryDAO;
import ru.protei.portal.core.model.ent.RedmineStatusMapEntry;

import java.util.List;

public class RedmineStatusMapEntryDAO_Impl extends PortalBaseJdbcDAO<RedmineStatusMapEntry> implements RedmineStatusMapEntryDAO {
    @Override
    public RedmineStatusMapEntry getByPortalStatusId(long id, long mapId) {
        return getByCondition("MAP_ID = ? AND LOCAL_status_id = ?", mapId, id);
    }

    @Override
    public RedmineStatusMapEntry getByRedmineStatusId(long id, long mapId) {
        return getByCondition("MAP_ID = ? AND RM_status_id = ?", mapId, id);
    }

    @Override
    public RedmineStatusMapEntry getByPortalStatusName(String name, long mapId) {
        return getByCondition("MAP_ID = ? AND LOCAL_status_name = ?", name, mapId);
    }

    @Override
    public List<RedmineStatusMapEntry> getListByEndpointId(long mapId) {
        return getListByCondition("MAP_ID = ?", mapId);
    }
}
