package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.UserDashboardDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserDashboard;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.List;
import java.util.Objects;

import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;

public class UserDashboardServiceImpl implements UserDashboardService {

    @Override
    public Result<Long> createUserDashboard(AuthToken token, UserDashboard dashboard) {

        if (token == null || token.getUserLoginId() == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (dashboard == null || dashboard.getCaseFilterId() == null || StringUtils.isEmpty(dashboard.getName())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long loginId = token.getUserLoginId();
        dashboard.setId(null);
        dashboard.setLoginId(loginId);
        dashboard.setId(userDashboardDAO.persist(dashboard));

        if (dashboard.getId() == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        return ok(dashboard.getId());
    }

    @Override
    public Result<UserDashboard> editUserDashboard(AuthToken token, UserDashboard dashboard) {

        if (token == null || token.getUserLoginId() == null) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (dashboard == null ||
            dashboard.getId() == null ||
            dashboard.getLoginId() == null ||
            dashboard.getCaseFilterId() == null ||
            StringUtils.isEmpty(dashboard.getName())
        ) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        if (!Objects.equals(token.getUserLoginId(), dashboard.getLoginId())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        UserDashboard dashboardFromDb = userDashboardDAO.get(dashboard.getId());
        if (dashboardFromDb == null) {
            return error(En_ResultStatus.NOT_FOUND);
        }

        if (!Objects.equals(dashboardFromDb.getLoginId(), dashboard.getLoginId())) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (!userDashboardDAO.merge(dashboard)) {
            return error(En_ResultStatus.NOT_UPDATED);
        }

        return ok(dashboard);
    }

    @Override
    public Result<Void> removeUserDashboard(AuthToken token, Long dashboardId) {

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

        return ok();
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
