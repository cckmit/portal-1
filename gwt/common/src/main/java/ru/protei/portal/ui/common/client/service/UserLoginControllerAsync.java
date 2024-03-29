package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.UserDashboard;

import java.util.List;

public interface UserLoginControllerAsync {

    void saveUserDashboard(UserDashboard dashboard, AsyncCallback<Long> async);

    void removeUserDashboard(Long dashboardId, AsyncCallback<Long> async);

    void getUserDashboards(AsyncCallback<List<UserDashboard>> async);

    void swapUserDashboards(Long srcDashboardId, Long dstDashboardId, AsyncCallback<List<UserDashboard>> async);
}
