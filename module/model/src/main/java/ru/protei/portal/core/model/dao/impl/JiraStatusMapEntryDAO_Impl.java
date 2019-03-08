package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraStatusMapEntryDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.JiraStatusMapEntry;

import java.util.List;

public class JiraStatusMapEntryDAO_Impl extends PortalBaseJdbcDAO<JiraStatusMapEntry> implements JiraStatusMapEntryDAO {
    @Override
    public List<JiraStatusMapEntry> getListByEndpointId(long endpointId) {
        return null;
    }

    @Override
    public String getJiraStatus(En_CaseState state) {
        JiraStatusMapEntry entry = getByCondition("LOCAL_status_id=?",state.getId());
        return entry == null ? null : entry.getJiraStatusName();
    }

    @Override
    public En_CaseState getByJiraStatus(String statusName) {
        JiraStatusMapEntry entry = getByCondition("jira_status_name=?", statusName);
        return entry == null ? null : entry.getLocalStatus();
    }
}
