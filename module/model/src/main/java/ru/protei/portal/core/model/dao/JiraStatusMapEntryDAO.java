package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.JiraStatusMapEntry;

public interface JiraStatusMapEntryDAO extends PortalBaseDAO<JiraStatusMapEntry> {

    String getJiraStatus(long mapId, long stateId);

    Long getByJiraStatus(long mapId, String statusId);
}
