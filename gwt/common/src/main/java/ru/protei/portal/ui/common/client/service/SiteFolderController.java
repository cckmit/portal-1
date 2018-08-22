package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/SiteFolderController")
public interface SiteFolderController extends RemoteService {

    long getPlatformsCount(PlatformQuery query) throws RequestFailedException;

    long getServersCount(ServerQuery query) throws RequestFailedException;

    long getApplicationsCount(ApplicationQuery query) throws RequestFailedException;

    List<Platform> getPlatforms(PlatformQuery query) throws RequestFailedException;

    List<Server> getServers(ServerQuery query) throws RequestFailedException;

    List<Application> getApplications(ApplicationQuery query) throws RequestFailedException;

    List<Server> getServersWithAppsNames(ServerQuery query) throws RequestFailedException;

    List<EntityOption> getPlatformsOptionList(PlatformQuery query) throws RequestFailedException;

    List<EntityOption> getServersOptionList(ServerQuery query) throws RequestFailedException;

    Platform getPlatform(long id) throws RequestFailedException;

    Server getServer(long id) throws RequestFailedException;

    Application getApplication(long id) throws RequestFailedException;

    Platform savePlatform(Platform platform) throws RequestFailedException;

    Server saveServer(Server server, Long serverIdOfAppsToBeCloned) throws RequestFailedException;

    Application saveApplication(Application application) throws RequestFailedException;

    boolean removePlatform(long id) throws RequestFailedException;

    boolean removeServer(long id) throws RequestFailedException;

    boolean removeApplication(long id) throws RequestFailedException;
}
