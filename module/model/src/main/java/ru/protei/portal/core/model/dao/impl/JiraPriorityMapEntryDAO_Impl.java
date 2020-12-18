package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraPriorityMapEntryDAO;
import ru.protei.portal.core.model.ent.JiraPriorityMapEntry;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JiraPriorityMapEntryDAO_Impl extends PortalBaseJdbcDAO<JiraPriorityMapEntry> implements JiraPriorityMapEntryDAO {

    @Override
    public JiraPriorityMapEntry getByPortalPriorityId(long mapId, Integer importanceId) {
        List<JiraPriorityMapEntry> list = getListByCondition("MAP_ID = ? AND LOCAL_priority_id = ?", mapId, importanceId);

        if (list.isEmpty())
            return null;

        if (list.size() > 1)
            Collections.sort(list, Comparator.comparing(JiraPriorityMapEntry::getJiraPriorityId));

        return list.get(0);
    }

    @Override
    public JiraPriorityMapEntry getByJiraPriorityName(long mapId, String jiraName) {
        return getByCondition("map_id=? and jira_priority_name = ?", mapId, jiraName);
    }
}
