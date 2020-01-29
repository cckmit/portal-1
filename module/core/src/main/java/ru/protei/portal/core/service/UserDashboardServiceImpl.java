package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.UserDashboardDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserDashboard;

import java.util.List;
import java.util.Objects;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class UserDashboardServiceImpl implements UserDashboardService {

    @Override
    public Result<UserDashboard> createUserDashboard(AuthToken token, UserDashboard dashboard) {

        if (token == null || token.getUserLoginId() == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (dashboard.getCaseFilterId() == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long loginId = token.getUserLoginId();
        dashboard.setId(null);
        dashboard.setLoginId(loginId);
        dashboard.setId(userDashboardDAO.persist(dashboard));

        if (dashboard.getId() == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        return ok(dashboard);
    }

    @Override
    public Result<UserDashboard> removeUserDashboard(AuthToken token, Long dashboardId) {

        if (token == null || token.getUserLoginId() == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (dashboardId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        UserDashboard dashboard = userDashboardDAO.get(dashboardId);
        if (dashboard == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        Long loginId = token.getUserLoginId();
        if (!Objects.equals(dashboard.getLoginId(), loginId)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!userDashboardDAO.removeByKey(dashboardId)) {
            return error(En_ResultStatus.NOT_REMOVED);
        }

        return ok(dashboard);
    }

    @Override
    public Result<List<UserDashboard>> getUserDashboards(AuthToken token) {

        if (token == null || token.getUserLoginId() == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        Long loginId = token.getUserLoginId();
        List<UserDashboard> dashboardList = userDashboardDAO.findByLoginId(loginId);
        return ok(dashboardList);
    }

    @Autowired
    UserDashboardDAO userDashboardDAO;
}
