package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.RedminePriorityMapEntry;

import java.util.List;

public interface JiraPriorityMapEntryDAO extends PortalBaseDAO<RedminePriorityMapEntry> {
    RedminePriorityMapEntry getByPortalPriorityId(long id, long endpointId);

    RedminePriorityMapEntry getByRedminePriorityId(long id, long endpointId);

    RedminePriorityMapEntry getByPortalPriorityName(String name, long endpointId);

    RedminePriorityMapEntry getByRedminePriorityName(String rmName, long mapId);

    List<RedminePriorityMapEntry> getListByMapId(long endpointId);
}
