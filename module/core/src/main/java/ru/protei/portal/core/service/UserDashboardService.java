package ru.protei.portal.core.service;

import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.annotations.Privileged;
import ru.protei.portal.core.model.dict.En_Privilege;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserDashboard;

import java.util.List;

public interface UserDashboardService {

    @Privileged(En_Privilege.DASHBOARD_VIEW)
    Result<Long> createUserDashboard(AuthToken token, UserDashboard dashboard);

    @Privileged(En_Privilege.DASHBOARD_VIEW)
    Result<UserDashboard> editUserDashboard(AuthToken token, UserDashboard dashboard);

    @Privileged(En_Privilege.DASHBOARD_VIEW)
    Result<Void> removeUserDashboard(AuthToken token, Long dashboardId);

    @Privileged(En_Privilege.DASHBOARD_VIEW)
    Result<List<UserDashboard>> getUserDashboards(AuthToken token);
}
