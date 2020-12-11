package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.JiraPriorityMapEntry;

public interface JiraPriorityMapEntryDAO extends PortalBaseDAO<JiraPriorityMapEntry> {
    JiraPriorityMapEntry getByPortalPriorityId(long mapId, Integer importanceId);

    JiraPriorityMapEntry getByJiraPriorityName(long mapId, String name);

//    JiraPriorityMapEntry getByPortalPriorityName(String name, long endpointId);
//
//    JiraPriorityMapEntry getByJiraPriorityName(String rmName, long mapId);
//
//    List<JiraPriorityMapEntry> getListByMapId(long endpointId);
}
