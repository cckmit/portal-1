package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.JiraStatusMapEntryDAO;
import ru.protei.portal.core.model.dao.RedmineStatusMapEntryDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.JiraStatusMapEntry;
import ru.protei.portal.core.model.ent.RedmineStatusMapEntry;

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
}
