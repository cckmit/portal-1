package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.RedmineStatusMapEntryDAO;
import ru.protei.portal.core.model.ent.RedmineStatusMapEntry;

import java.util.List;

public class RedmineStatusMapEntryDAO_Impl extends PortalBaseJdbcDAO<RedmineStatusMapEntry> implements RedmineStatusMapEntryDAO {
    @Override
    public RedmineStatusMapEntry getByPortalStatusId(long id, long endpointId) {
        return getByCondition("MAP_ID = ? AND LOCAL_status_id = ?", endpointId, id);
    }

    @Override
    public RedmineStatusMapEntry getByRedmineStatusId(long id, long endpointId) {
        return getByCondition("MAP_ID = ? AND RM_status_id = ?", endpointId, id);
    }

    @Override
    public RedmineStatusMapEntry getByPortalStatusName(String name, long endpointId) {
        return getByCondition("LOCAL_status_name = ? AND ENDPOINT_ID = ?", name, endpointId);
    }

    @Override
    public List<RedmineStatusMapEntry> getListByEndpointId(long endpointId) {
        return getListByCondition("ENDPOINT_ID = ?", endpointId);
    }
}
