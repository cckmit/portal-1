package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraStatusMapEntryDAO;
import ru.protei.portal.core.model.ent.JiraStatusMapEntry;

import java.util.List;

public class JiraStatusMapEntryDAO_Impl extends PortalBaseJdbcDAO<JiraStatusMapEntry> implements JiraStatusMapEntryDAO {
    @Override
    public List<JiraStatusMapEntry> getListByEndpointId(long endpointId) {
        return null;
    }

    @Override
    public String getJiraStatus(long stateId) {
        return null;
    }

    @Override
    public int getByJiraStatus(String statusName) {
        return getByCondition("jira_status_name=?", statusName).getLocalStatusId();
    }
}
