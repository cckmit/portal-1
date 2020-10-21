package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.DutyLogFilterDAO;
import ru.protei.portal.core.model.ent.DutyLogFilter;

import java.util.List;

public class DutyLogFilterDAO_Impl extends PortalBaseJdbcDAO<DutyLogFilter> implements DutyLogFilterDAO {
    @Override
    public List<DutyLogFilter> getListByLoginId(Long loginId) {
        return getListByCondition( "login_id=?", loginId);
    }

    @Override
    public DutyLogFilter checkExistsByParams( String name, Long loginId) {
        return getByCondition("name=? and login_id=?", name, loginId);
    }
}

