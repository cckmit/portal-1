package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.UserDashboard;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/UserLoginController")
public interface UserLoginController extends RemoteService {

    Long saveUserDashboard(UserDashboard dashboard) throws RequestFailedException;

    void removeUserDashboard(Long dashboardId) throws RequestFailedException;

    List<UserDashboard> getUserDashboards() throws RequestFailedException;

    Boolean swapUserDashboards(Long srcDashboardId, Long dstDashboardId) throws RequestFailedException;
}
