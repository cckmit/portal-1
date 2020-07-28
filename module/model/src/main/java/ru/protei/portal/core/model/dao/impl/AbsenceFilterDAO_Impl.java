package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.AbsenceFilterDAO;
import ru.protei.portal.core.model.ent.AbsenceFilter;

import java.util.List;

public class AbsenceFilterDAO_Impl extends PortalBaseJdbcDAO<AbsenceFilter> implements AbsenceFilterDAO {
    @Override
    public List<AbsenceFilter> getListByLoginId(Long loginId) {
        return getListByCondition( "login_id=?", loginId);
    }

    @Override
    public AbsenceFilter checkExistsByParams( String name, Long loginId) {
        return getByCondition("name=? and login_id=?", name, loginId);
    }
}
