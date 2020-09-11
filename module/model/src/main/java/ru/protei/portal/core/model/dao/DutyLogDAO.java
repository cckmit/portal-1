package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.DutyLog;

public interface DutyLogDAO extends PortalBaseDAO<DutyLog> {
    boolean checkExists(DutyLog value);
}