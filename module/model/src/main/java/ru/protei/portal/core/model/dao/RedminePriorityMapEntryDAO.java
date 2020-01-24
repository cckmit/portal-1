package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.RedminePriorityMapEntry;

import java.util.List;

public interface RedminePriorityMapEntryDAO extends PortalBaseDAO<RedminePriorityMapEntry> {
    RedminePriorityMapEntry getByPortalPriorityId(long id, long mapId);

    RedminePriorityMapEntry getByRedminePriorityId(long id, long mapId);

    RedminePriorityMapEntry getByPortalPriorityName(String name, long mapId);

    RedminePriorityMapEntry getByRedminePriorityName(String rmName, long mapId);

    List<RedminePriorityMapEntry> getListByMapId(long mapId);
}
