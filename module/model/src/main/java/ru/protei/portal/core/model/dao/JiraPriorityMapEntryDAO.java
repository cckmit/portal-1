package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.JiraPriorityMapEntry;
import ru.protei.portal.core.model.ent.RedminePriorityMapEntry;

import java.util.List;

public interface JiraPriorityMapEntryDAO extends PortalBaseDAO<JiraPriorityMapEntry> {
    JiraPriorityMapEntry getByPortalPriorityId(long id, long endpointId);

    JiraPriorityMapEntry getByJiraPriorityId(String id);

    JiraPriorityMapEntry getByPortalPriorityName(String name, long endpointId);

    JiraPriorityMapEntry getByJiraPriorityName(String rmName, long mapId);

    List<JiraPriorityMapEntry> getListByMapId(long endpointId);
}
