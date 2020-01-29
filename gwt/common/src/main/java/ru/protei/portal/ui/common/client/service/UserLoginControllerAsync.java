package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.AsyncCallback;
import ru.protei.portal.core.model.ent.UserDashboard;

import java.util.List;

public interface UserLoginControllerAsync {

    void createUserDashboard(UserDashboard dashboard, AsyncCallback<UserDashboard> async);

    void removeUserDashboard(Long dashboardId, AsyncCallback<UserDashboard> async);

    void getUserDashboards(AsyncCallback<List<UserDashboard>> async);
}
