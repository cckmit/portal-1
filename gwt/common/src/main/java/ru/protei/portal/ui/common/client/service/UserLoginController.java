package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.UserDashboard;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/UserLoginController")
public interface UserLoginController extends RemoteService {

    UserDashboard saveUserDashboard(UserDashboard dashboard) throws RequestFailedException;

    UserDashboard removeUserDashboard(Long dashboardId) throws RequestFailedException;

    List<UserDashboard> getUserDashboards() throws RequestFailedException;
}
