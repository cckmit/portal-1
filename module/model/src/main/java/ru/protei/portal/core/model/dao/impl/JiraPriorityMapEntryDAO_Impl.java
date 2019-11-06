package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraPriorityMapEntryDAO;
import ru.protei.portal.core.model.dict.En_ImportanceLevel;
import ru.protei.portal.core.model.ent.JiraPriorityMapEntry;
import ru.protei.portal.core.utils.JiraUtils;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class JiraPriorityMapEntryDAO_Impl extends PortalBaseJdbcDAO<JiraPriorityMapEntry> implements JiraPriorityMapEntryDAO {

    @Override
    public JiraPriorityMapEntry getByPortalPriorityId(long mapId, En_ImportanceLevel level) {
        List<JiraPriorityMapEntry> list = getListByCondition("MAP_ID = ? AND LOCAL_priority_id = ?", mapId, level.getId());

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
