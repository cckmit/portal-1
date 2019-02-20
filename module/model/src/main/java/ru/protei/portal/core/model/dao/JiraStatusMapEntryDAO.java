package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.JiraStatusMapEntry;
import ru.protei.portal.core.model.ent.RedmineStatusMapEntry;

import java.util.List;

public interface JiraStatusMapEntryDAO extends PortalBaseDAO<JiraStatusMapEntry> {
    List<JiraStatusMapEntry> getListByEndpointId(long endpointId);

    String getJiraStatus(long stateId);
}
