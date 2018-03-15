package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.RedminePriorityMapEntryDAO;
import ru.protei.portal.core.model.ent.RedminePriorityMapEntry;

import java.util.List;

public class RedminePriorityMapEntryDAO_Impl extends PortalBaseJdbcDAO<RedminePriorityMapEntry> implements RedminePriorityMapEntryDAO {
    @Override
    public RedminePriorityMapEntry getByPortalPriorityId(long id, long mapId) {
        return getByCondition("MAP_ID = ? AND LOCAL_priority_id = ?", mapId, id);
    }

    @Override
    public RedminePriorityMapEntry getByRedminePriorityId(long id, long mapId) {
        return getByCondition("MAP_ID = ? AND RM_priority_id = ?", mapId, id);
    }

    @Override
    public RedminePriorityMapEntry getByPortalPriorityName(String name, long mapId) {
        return getByCondition("MAP_ID = ? AND LOCAL_priority_name = ?", mapId, name);
    }

    @Override
    public List<RedminePriorityMapEntry> getListByMapId(long mapId) {
        return getListByCondition("MAP_ID = ?", mapId);
    }
}
