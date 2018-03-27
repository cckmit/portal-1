package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.RedmineToCrmEntry;

public interface RedmineToCrmStatusMapEntryDAO extends PortalBaseDAO<RedmineToCrmEntry> {
    RedmineToCrmEntry getLocalStatus(long mapId, int redmineStateId);
}
