package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.AbsenceFilter;

import java.util.List;

public interface AbsenceFilterDAO extends PortalBaseDAO<AbsenceFilter> {
    List<AbsenceFilter> getListByLoginId(Long loginId);

    void removeNotUniqueFilters();
}
