package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.DutyLogFilter;

import java.util.List;

public interface DutyLogFilterDAO extends PortalBaseDAO<DutyLogFilter> {
    List<DutyLogFilter> getListByLoginId(Long loginId);
    DutyLogFilter checkExistsByParams(String name, Long loginId );
}