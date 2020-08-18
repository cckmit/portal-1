package ru.protei.portal.core.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import ru.protei.portal.api.struct.Result;
import ru.protei.portal.core.model.dao.UserDashboardDAO;
import ru.protei.portal.core.model.dict.En_ResultStatus;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserDashboard;
import ru.protei.portal.core.model.helper.StringUtils;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

import static java.util.stream.Collectors.*;
import static ru.protei.portal.api.struct.Result.error;
import static ru.protei.portal.api.struct.Result.ok;
import static ru.protei.portal.core.model.helper.CollectionUtils.*;

public class UserDashboardServiceImpl implements UserDashboardService {
    @Override
    public Result<Long> createUserDashboard(AuthToken token, UserDashboard dashboard) {

        if (!validateToken(token)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (dashboard == null || dashboard.getCaseFilterId() == null || StringUtils.isEmpty(dashboard.getName())) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        Long loginId = token.getUserLoginId();
        dashboard.setId(null);
        dashboard.setLoginId(loginId);
        dashboard.setOrderNumber(userDashboardDAO.getAll().size());
        dashboard.setId(userDashboardDAO.persist(dashboard));

        if (dashboard.getId() == null) {
            return error(En_ResultStatus.NOT_CREATED);
        }

        return ok(dashboard.getId());
    }

    @Override
    public Result<UserDashboard> editUserDashboard(AuthToken token, UserDashboard dashboard) {

        if (!validateToken(token)) {
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

        if (!validateToken(token)) {
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

        updateOrders(loginId);

        return ok();
    }

    @Override
    public Result<List<UserDashboard>> getUserDashboards(AuthToken token) {
        if (!validateToken(token)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        Long loginId = token.getUserLoginId();
        List<UserDashboard> dashboardList = stream(userDashboardDAO.findByLoginId(loginId)).sorted(Comparator.comparingLong(UserDashboard::getOrderNumber)).collect(toList());
        return ok(dashboardList);
    }

    @Override
    @Transactional
    public Result<Boolean> swapUserDashboards(AuthToken token, Long srcDashboardId, Long dstDashboardId) {
        if (!validateToken(token)) {
            return error(En_ResultStatus.PERMISSION_DENIED);
        }

        if (srcDashboardId == null || dstDashboardId == null) {
            return error(En_ResultStatus.INCORRECT_PARAMS);
        }

        UserDashboard srcDashboard = userDashboardDAO.get(srcDashboardId);
        UserDashboard dstDashboard = userDashboardDAO.get(dstDashboardId);

        Integer srcOrder = srcDashboard.getOrderNumber();
        Integer dstOrder = dstDashboard.getOrderNumber();

        srcDashboard.setOrderNumber(dstOrder);
        dstDashboard.setOrderNumber(srcOrder);

        userDashboardDAO.mergeBatch(Arrays.asList(srcDashboard, dstDashboard));

        return ok(true);
    }

    private void updateOrders(Long loginId) {
        List<UserDashboard> userDashboards = userDashboardDAO.findByLoginId(loginId);

        for (int i = 0; i < userDashboards.size(); i++) {
            userDashboards.get(i).setOrderNumber(i);
        }
    }

    private boolean validateToken(AuthToken authToken) {
        if (authToken == null) {
            return false;
        }

        if (authToken.getUserLoginId() == null) {
            return false;
        }

        return true;
    }

    @Autowired
    UserDashboardDAO userDashboardDAO;
}
