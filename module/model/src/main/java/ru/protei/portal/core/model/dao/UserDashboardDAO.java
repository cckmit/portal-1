package ru.protei.portal.core.model.dao;

import ru.protei.portal.core.model.ent.UserDashboard;

import java.util.List;

public interface UserDashboardDAO extends PortalBaseDAO<UserDashboard>  {

    List<UserDashboard> findByLoginId(Long loginId);
}
