package ru.protei.portal.ui.common.client.service;

import com.google.gwt.user.client.rpc.RemoteService;
import com.google.gwt.user.client.rpc.RemoteServiceRelativePath;
import ru.protei.portal.core.model.ent.Application;
import ru.protei.portal.core.model.ent.Platform;
import ru.protei.portal.core.model.ent.Server;
import ru.protei.portal.core.model.ent.ServerGroup;
import ru.protei.portal.core.model.query.ApplicationQuery;
import ru.protei.portal.core.model.query.PlatformQuery;
import ru.protei.portal.core.model.query.ServerQuery;
import ru.protei.portal.core.model.view.EntityOption;
import ru.protei.portal.core.model.view.PlatformOption;
import ru.protei.portal.ui.common.shared.exception.RequestFailedException;
import ru.protei.winter.core.utils.beans.SearchResult;

import java.util.List;

@RemoteServiceRelativePath("springGwtServices/SiteFolderController")
public interface SiteFolderController extends RemoteService {

    SearchResult<Platform> getPlatforms(PlatformQuery query) throws RequestFailedException;

    SearchResult<Server> getServers(ServerQuery query) throws RequestFailedException;

    SearchResult<Application> getApplications(ApplicationQuery query) throws RequestFailedException;

    SearchResult<Server> getServersWithAppsNames(ServerQuery query) throws RequestFailedException;


    List<PlatformOption> getPlatformsOptionList(PlatformQuery query) throws RequestFailedException;

    List<EntityOption> getServersOptionList(ServerQuery query) throws RequestFailedException;

    List<ServerGroup> getServerGroups(Long platformId, int limit, int offset) throws RequestFailedException;

    Platform getPlatform(long id) throws RequestFailedException;

    Server getServer(long id) throws RequestFailedException;

    Application getApplication(long id) throws RequestFailedException;

    Platform savePlatform(Platform platform) throws RequestFailedException;

    Server saveServer(Server server, Long serverIdOfAppsToBeCloned) throws RequestFailedException;

    Application saveApplication(Application application) throws RequestFailedException;

    ServerGroup saveServerGroup(ServerGroup serverGroup) throws RequestFailedException;

    Long removePlatform(long id) throws RequestFailedException;

    Long removeServer(long id) throws RequestFailedException;

    Long removeServerGroup(Long id) throws RequestFailedException;

    Long removeApplication(long id) throws RequestFailedException;
}
