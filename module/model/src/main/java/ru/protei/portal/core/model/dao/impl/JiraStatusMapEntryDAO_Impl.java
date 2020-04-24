package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraStatusMapEntryDAO;
import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.JiraStatusMapEntry;

public class JiraStatusMapEntryDAO_Impl extends PortalBaseJdbcDAO<JiraStatusMapEntry> implements JiraStatusMapEntryDAO {

    @Override
    public String getJiraStatus(long mapId, CaseState state) {
        JiraStatusMapEntry entry = getByCondition("map_id=? and LOCAL_status_id=?", mapId, state.getId());
        return entry == null ? null : entry.getJiraStatusName();
    }

    @Override
    public CaseState getByJiraStatus(long mapId, String statusName) {
        JiraStatusMapEntry entry = getByCondition("map_id=? and jira_status_name=?", mapId, statusName);
        return entry == null ? null : entry.getLocalStatus();
    }
}
