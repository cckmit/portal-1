package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.RedmineStatusMapEntryDAO;
import ru.protei.portal.core.model.dict.En_CaseState;
import ru.protei.portal.core.model.ent.RedmineStatusMapEntry;

import java.util.List;

public class RedmineStatusMapEntryDAO_Impl extends PortalBaseJdbcDAO<RedmineStatusMapEntry> implements RedmineStatusMapEntryDAO {
    @Override
    public RedmineStatusMapEntry getRedmineStatus(En_CaseState oldState, En_CaseState state, long mapId) {
        return getByCondition("MAP_ID = ? AND LOCAL_status_id = ? and LOCAL_previous_status_id = ?",
                mapId, state.getId(), oldState.getId());
    }

    @Override
    public List<RedmineStatusMapEntry> getListByEndpointId(long mapId) {
        return getListByCondition("MAP_ID = ?", mapId);
    }
}
