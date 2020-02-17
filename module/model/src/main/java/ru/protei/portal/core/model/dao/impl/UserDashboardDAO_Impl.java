package ru.protei.portal.core.model.dao.impl;

import ru.protei.portal.core.model.dao.UserDashboardDAO;
import ru.protei.portal.core.model.ent.UserDashboard;

import java.util.List;

public class UserDashboardDAO_Impl extends PortalBaseJdbcDAO<UserDashboard> implements UserDashboardDAO {

    @Override
    public List<UserDashboard> findByLoginId(Long loginId) {
        return getListByCondition("user_dashboard.login_id = ?", loginId);
    }
}
