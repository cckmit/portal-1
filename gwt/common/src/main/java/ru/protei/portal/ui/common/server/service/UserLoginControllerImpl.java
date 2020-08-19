package ru.protei.portal.ui.common.server.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.protei.portal.core.model.ent.AuthToken;
import ru.protei.portal.core.model.ent.UserDashboard;
import ru.protei.portal.core.service.UserDashboardService;
import ru.protei.portal.core.service.session.SessionService;
import ru.protei.portal.ui.common.client.service.UserLoginController;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import javax.servlet.http.HttpServletRequest;
import java.util.List;

import static ru.protei.portal.ui.common.server.ServiceUtils.*;

@Service("UserLoginController")
public class UserLoginControllerImpl implements UserLoginController {

    @Override
    public Long saveUserDashboard(UserDashboard dashboard) throws RequestFailedException {
        log.info("saveUserDashboard(): dashboard={}", dashboard);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        boolean isNew = dashboard == null || dashboard.getId() == null;
        if (isNew) {
            return checkResultAndGetData(userDashboardService.createUserDashboard(token, dashboard));
        } else {
            return checkResultAndGetData(userDashboardService.editUserDashboard(token, dashboard)).getId();
        }
    }

    @Override
    public void removeUserDashboard(Long dashboardId) throws RequestFailedException {
        log.info("removeUserDashboard(): dashboardId={}", dashboardId);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        checkResult(userDashboardService.removeUserDashboard(token, dashboardId));
    }

    @Override
    public List<UserDashboard> getUserDashboards() throws RequestFailedException {
        log.info("getUserDashboards():");
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(userDashboardService.getUserDashboards(token));
    }

    @Override
    public List<UserDashboard> swapUserDashboards(Long srcDashboardId, Long dstDashboardId) throws RequestFailedException {
        log.info("swapUserDashboards(): src={}, dst={}", srcDashboardId, dstDashboardId);
        AuthToken token = getAuthToken(sessionService, httpServletRequest);
        return checkResultAndGetData(userDashboardService.swapUserDashboards(token, srcDashboardId, dstDashboardId));
    }

    @Autowired
    SessionService sessionService;
    @Autowired
    HttpServletRequest httpServletRequest;
    @Autowired
    UserDashboardService userDashboardService;

    private static final Logger log = LoggerFactory.getLogger(UserLoginControllerImpl.class);
}
