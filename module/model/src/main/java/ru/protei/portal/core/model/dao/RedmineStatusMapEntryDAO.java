package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.RedmineStatusMapEntry;

import java.util.List;

public interface RedmineStatusMapEntryDAO extends PortalBaseDAO<RedmineStatusMapEntry> {
    RedmineStatusMapEntry getRedmineStatus(long oldStateId, long stateId, long mapId);
    List<RedmineStatusMapEntry> getListByEndpointId(long endpointId);
}
