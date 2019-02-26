package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.JiraStatusMapEntry;

import java.util.List;

public interface JiraStatusMapEntryDAO extends PortalBaseDAO<JiraStatusMapEntry> {
    List<JiraStatusMapEntry> getListByEndpointId(long endpointId);

    String getJiraStatus(long stateId);

    int getByJiraStatus(String statusId);
}
