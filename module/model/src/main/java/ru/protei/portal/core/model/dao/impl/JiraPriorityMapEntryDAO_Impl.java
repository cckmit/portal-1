package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraPriorityMapEntryDAO;
import ru.protei.portal.core.model.dao.RedminePriorityMapEntryDAO;
import ru.protei.portal.core.model.ent.JiraPriorityMapEntry;
import ru.protei.portal.core.model.ent.RedminePriorityMapEntry;

import java.util.List;

public class JiraPriorityMapEntryDAO_Impl extends PortalBaseJdbcDAO<JiraPriorityMapEntry> implements JiraPriorityMapEntryDAO {

    @Override
    public JiraPriorityMapEntry getByPortalPriorityId(long id, long endpointId) {
        return getByCondition("MAP_ID = ? AND LOCAL_priority_id = ?", endpointId, id);
    }

    @Override
    public JiraPriorityMapEntry getByJiraPriorityId(String id) {
        return getByCondition("jira_priority_id = ?", id);
    }

    @Override
    public JiraPriorityMapEntry getByPortalPriorityName(String name, long mapId) {
        return getByCondition("MAP_ID = ? AND LOCAL_priority_name = ?", mapId, name);
    }

    @Override
    public JiraPriorityMapEntry getByJiraPriorityName(String name, long mapId) {
        return getByCondition("MAP_ID = ? AND JIRA_priority_name = ?", mapId, name);
    }

    @Override
    public List<JiraPriorityMapEntry> getListByMapId(long mapId) {
        return getListByCondition("MAP_ID = ?", mapId);
    }
}
