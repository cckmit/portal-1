package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.RedmineStatusMapEntry;

import java.util.List;

public interface RedmineStatusMapEntryDAO extends PortalBaseDAO<RedmineStatusMapEntry> {
    RedmineStatusMapEntry getByPortalStatusId(long id, long endpointId);

    RedmineStatusMapEntry getByRedmineStatusId(long id, long endpointId);

    RedmineStatusMapEntry getByPortalStatusName(String name, long endpointId);

    List<RedmineStatusMapEntry> getListByEndpointId(long endpointId);
}
