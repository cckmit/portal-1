package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.RedmineStatusMapEntry;

import java.util.List;

public interface RedmineStatusMapEntryDAO extends PortalBaseDAO<RedmineStatusMapEntry> {
    RedmineStatusMapEntry getRedmineStatus(En_CaseState oldState, En_CaseState state, long mapId);
    List<RedmineStatusMapEntry> getListByEndpointId(long endpointId);
}
