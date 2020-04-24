package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.CaseState;
import ru.protei.portal.core.model.ent.RedmineStatusMapEntry;

import java.util.List;

public interface RedmineStatusMapEntryDAO extends PortalBaseDAO<RedmineStatusMapEntry> {
    RedmineStatusMapEntry getRedmineStatus(CaseState oldState, CaseState state, long mapId);
    List<RedmineStatusMapEntry> getListByEndpointId(long endpointId);
}
